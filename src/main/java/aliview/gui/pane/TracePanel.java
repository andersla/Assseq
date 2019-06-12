package aliview.gui.pane;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import aliview.Assseq;
import aliview.Base;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.color.ColorScheme;
import aliview.color.DefaultColorScheme;
import aliview.color.TraceColorScheme;
import aliview.messenges.Messenger;
import aliview.sequencelist.AlignmentSelectionEvent;
import aliview.sequences.Sequence;
import aliview.sequences.TraceSequence;
import aliview.settings.Settings;
import utils.OSNativeUtils;
import utils.nexus.CharSet;
import utils.nexus.CharSets;

public class TracePanel extends AlignmentPane{
	
	private static final Logger logger = Logger.getLogger(TracePanel.class);
//	private static final double MIN_CHAR_SIZE = 0;
//	private static final int MAX_CHAR_SIZE = 100;
//	private static final double CHAR_HEIGHT_RATIO = 2.5;
//	public static final int MAX_CHARSIZE_TO_DRAW = 6;
	//private static final Color ALPHACOLOR = new Color(255, 255,255, 128 );
	
	private ColorScheme colorSchemeNucleotide = new TraceColorScheme();


	// TODO This should instead be tracing a sequence instead of a position?
	int differenceTraceSequencePosition = 0;

	
//	double getCharHeightRatio() {
//		return CHAR_HEIGHT_RATIO;
//	}
	
	@Override
	public ColorScheme getColorSchemeNucleotide() {
		return colorSchemeNucleotide;
	}

	public TracePanel(ViewModel viewModel) {
		super(viewModel);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		paintAlignment(g);
	}


	public void paintAlignment(Graphics g){
		drawCounter ++;
		long startTime = System.currentTimeMillis();	
		if(Assseq.isDebugMode() && drawCounter % DRAWCOUNT_LOF_INTERVAL == 0){
			logger.info("Inside paintAlignment: Time from last endTim " + (startTime - endTime) + " milliseconds");
			System.out.println("Inside paintAlignment: Time from last endTim " + (startTime - endTime) + " milliseconds");
		}

		Graphics2D g2d = (Graphics2D) g;

		// What part of alignment matrix is in view (what part of matrix is in graphical view)
		Rectangle clip = g2d.getClipBounds();

		Rectangle matrixClip = paneCoordToMatrixCoord(clip);

		//		 logger.info(matrixClip);

		int xMin = matrixClip.x - 1;
		int yMin = matrixClip.y - 1;
		int xMax = (int) matrixClip.getMaxX() + 1;
		int yMax = (int) matrixClip.getMaxY() + 1;
		//
		//				logger.info("yMin" + yMin);
		//				logger.info("yMin" + yMax);
		//				logger.info("xMin" + xMin);
		//				logger.info("xMax" + xMax);
		//
		// add one extra position when drawing translated
		// otherwise there could be some white borders when scrolling
		if(showTranslation){
			xMin --;
			xMax ++;
		}

		// adjust for part of matrix that exists
		xMin = Math.min(getAlignment().getMaxX(), xMin);
		xMin = Math.max(0, xMin);

		yMin = Math.min(getAlignment().getMaxY(), yMin);
		yMin = Math.max(0, yMin);

		xMax = Math.min(getAlignment().getMaxX(), xMax);
		yMax = Math.min(getAlignment().getMaxY(), yMax);

		//				logger.info("yMin" + yMin);
		//				logger.info("yMax" + yMax);
		//				logger.info("xMin" + xMin);
		//				logger.info("xMax" + xMax);

		// Extra because pixelCopyDraw
		int height = (yMax - yMin) * (int)charHeight;
		int width = (xMax - xMin) * (int)charWidth;
		//
		//				logger.info("width" + width);
		//				logger.info("height" + height);
		//
		// Small chars
		if(charWidth < 1){
			height = clip.height;
			width = clip.width;
		}
		//
		//				logger.info("yMax" + yMax);
		//				logger.info("yMin" + yMin);
		//				logger.info("width" + width);
		//				logger.info("clipHeight" + clip.height);
		//				logger.info("width" + width);
		//				logger.info("height" + height);




		// HERE FILL RGB-ARRAY DRAW...
		//		fillRGBArrayAndPaint(xMin, xMax, yMin, yMax, clipRGB, clip, g2d);
		
		paintMultithreaded(xMin, xMax, yMin, yMax, clip, g2d);

		if(drawCounter % DRAWCOUNT_LOF_INTERVAL == 0){
			endTime = System.currentTimeMillis();
			logger.info("TracePanel paintAlignment took " + (endTime - startTime) + " milliseconds");
		}

		// repaint ruler also if needed
		if(clip.x != lastClip.x || clip.width != lastClip.width || rulerIsDirty){
			alignmentRuler.repaint();
			rulerIsDirty = false;
		}
		lastClip = clip;

	}

	private void paintMultithreaded(int xMin, int xMax, int yMin, int yMax, Rectangle clip, Graphics2D g2d){
		// these vals are not going to change so get it only once
		boolean isNucleotideAlignment = getAlignment().isNucleotideAlignment();
		double seqPerPixX = 1/(double)charWidth;
		double seqPerPixY = 1/(double)charHeight;

		logger.info("Runtime.getRuntime().availableProcessors()" + Runtime.getRuntime().availableProcessors());
		int nThreads = 1;

		// Only one thread if filesequences - more threads make reading file slower
		if(getAlignment().isFileSequences()){
			nThreads = 1;
		}else{
			if(Runtime.getRuntime().availableProcessors() > 2){
				nThreads = 2;
			}
			if(Runtime.getRuntime().availableProcessors() > 3){
				nThreads = 3;
			}
		}


		//
		//
		// Small chars have their own loop here
		// Dont draw anything
		//
		//
		if(charWidth < 3){

			
		}	

		/////////////////////////
		//
		// Normal char width
		//
		/////////////////////////
		else{


			ExecutorService executor = Executors.newFixedThreadPool(nThreads);

			int clipYPos = 0;
			// Loop rows (The sequence painter is painting a row in its own thread)
			for(int y = yMin; y < yMax; y = y + 1){

				int seqYPos = y;
				
				// TODO make sure it is a TraceSequence
				TraceSequence seq = (TraceSequence)getAlignment().getSequences().get(seqYPos);
				
				int normalCharSeqPerPix = 1;
				int xPosStart = xMin;
				int xPosEnd = xMax;

				
				
				int clipXPosStart = (int)(xMin * charWidth);
				int clipYPosStart = (int)(yMin * charHeight);
				
				

				if(isNucleotideAlignment){
					
					TracePainter tracePainter = new TracePainter(seq,
							                      				 seqYPos,
							                                     clipYPos,
							                                     xPosStart,
							                                     xPosEnd,
							                                     normalCharSeqPerPix,
							                                     charWidth,
							                                     charHeight,
							                                     highDPIScaleFactor,
							                                     this,
							                                     getAlignment(),
							                                     g2d,
							                                     clip,
							                                     clipXPosStart,
							                                     clipYPosStart,
							                                     getColorSchemeNucleotide()
							                                     );	
					tracePainter.run();
					//executor.execute(tracePainter);
				}
				// Draw as AminoAcids
				else{
					
		
				}

				clipYPos ++;
			}

			executor.shutdown();
			try {
				executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

/*
				Image img = createImage(new MemoryImageSource(clipRGB.getScanWidth(), clipRGB.getHeight(), clipRGB.getBackend(), 0, clipRGB.getScanWidth()));
				// First fill background
				g2d.setColor(this.getBackground());
				g2d.fill(clip);



				int clipRGBXPos = clip.x;
				int clipRGBYPos = clip.y;
				// Adjust because we start always on exact char upp to one pos before
				if(charWidth > 1){
					clipRGBXPos = (int)(xMin * charWidth);
					clipRGBYPos = (int)(yMin * charHeight);
				}

				if (img != null){	
					// Mac retina screen
					if(highDPIScaleFactor > 1){
						int dx1 = clipRGBXPos;
						int dx2 = dx1 + clipRGB.getScanWidth() / highDPIScaleFactor;
						int dy1 = clipRGBYPos;
						int dy2 = dy1 + clipRGB.getHeight() / highDPIScaleFactor;

						int sx1 = 0;
						int sx2 = sx1 + clipRGB.getScanWidth();
						int sy1 = 0;
						int sy2 = sy1 + clipRGB.getHeight();
						g2d.drawImage(img,dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2, null);
					}else{
						g2d.drawImage(img, clipRGBXPos, clipRGBYPos, null);
					}
				}

*/

		logger.info("done");


	}

}