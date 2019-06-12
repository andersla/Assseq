package aliview.gui.pane;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.alignment.NucleotideHistogram;
import aliview.sequences.AminoAcidAndPosition;
import aliview.sequences.Sequence;

public class TracePainter_noThread{
	private static final Logger logger = Logger.getLogger(TracePainter_noThread.class);

	private Sequence seq;
	private int seqYPos;
	private int clipPosY;
	private int xMinSeqPos;
	private int xMaxSeqPos;
	private double seqPerPix;
	private double charWidth;
	private double charHeight;
	private double highDPIScaleFactor;
	private TracePanel tracePanel;
	private Alignment alignment;
	private Graphics2D g2d;
	private Rectangle clip;

	
	public TracePainter_noThread(Sequence seq, int seqYPos, int clipPosY, int xMinSeqPos,
			int xMaxSeqPos, double seqPerPix, double charWidth, double charHeight,
			TracePanel tracePanel, Alignment alignment, Graphics2D g2d, Rectangle clip) {
		super();
		this.seq = seq;
		this.seqYPos = seqYPos;
		this.clipPosY = clipPosY;
		this.xMinSeqPos = xMinSeqPos;
		this.xMaxSeqPos = xMaxSeqPos;
		this.seqPerPix = seqPerPix;
		this.charWidth = charWidth;
		this.charHeight = charHeight;
		this.tracePanel = tracePanel;
		this.alignment = alignment;
		this.g2d = g2d;
		this.clip = clip;
	}

	public void run(){
		// TODO maybe check before that sequence not is null
		if(seq != null){
			drawTrace(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, seqPerPix, charWidth, charHeight, highDPIScaleFactor, tracePanel, alignment, g2d, clip);
		}
	}

	public void drawTrace(Sequence seq, int seqYPos, int clipPosY, int xMin, int xMax, double seqPerPix, double charWidth, double charHeight, double highDPIScaleFactor,
			TracePanel tracePanel, Alignment alignment, Graphics2D g2d,Rectangle clip){
		

		// Make sure not outside length of seq
		int seqLength = seq.getLength();
		int clipPosX = 0;
		for(int x = xMin; x < xMax && x >=0 ; x ++){
			int seqXPos = (int)((double)x * seqPerPix);
			if(seqXPos >=0 && seqXPos < seqLength){
				int pixelPosX = (int)(clipPosX*charWidth*highDPIScaleFactor);
				int pixelPosY = (int)(clipPosY*charHeight*highDPIScaleFactor);
                 
				if(pixelPosX < clip.getWidth() && pixelPosY < clip.getHeight()){
					draw(seq,seqXPos, seqYPos,pixelPosX, pixelPosY, tracePanel, alignment, g2d, clip);
				}
			}
			clipPosX ++;
		}
	}

	private void draw(Sequence seq2, int seqXPos, int seqYPos2, int pixelPosX, int pixelPosY, TracePanel tracePanel2,
			Alignment alignment2, Graphics2D g2d2, Rectangle clip2) {
		byte residue = seq.getBaseAtPos(seqXPos);

		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		CharPixelsContainer pixContainerToUse = tracePanel.charPixDefaultNuc;
		byte byteToDraw = residue;
		int baseVal = NucleotideUtilities.baseValFromBase(residue);

		// adjustment if only diff to be shown
		if(tracePanel.isHighlightDiffTrace()){ // TODO CHANGE THIS SO IT IS WORKING EVEN IF TRACING SEQUENCE IS SHORTER THAN OTHER
			if(seqYPos != tracePanel.differenceTraceSequencePosition){
				if(baseVal == NucleotideUtilities.baseValFromBase(alignment.getBaseAt(seqXPos,tracePanel.getDifferenceTraceSequencePosition()))){
					byteToDraw = '.';
					pixContainerToUse = tracePanel.charPixDefaultNuc;
				}
			}
		}

		// adjustment if non-cons to be highlighted
		if(tracePanel.isHighlightNonCons()){
			NucleotideHistogram nucHistogram = (NucleotideHistogram) alignment.getHistogram();
			if(baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(nucHistogram.isMajorityRuleConsensus(seqXPos,baseVal)){
				pixContainerToUse = tracePanel.charPixConsensusNuc;
			}
		}
		if(tracePanel.highlightCons){
			NucleotideHistogram nucHistogram = (NucleotideHistogram) alignment.getHistogram();
			if(baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! nucHistogram.isMajorityRuleConsensus(seqXPos,baseVal)){
				pixContainerToUse = tracePanel.charPixConsensusNuc;
			}
		}

		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(seq.isBaseSelected(seqXPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			pixContainerToUse = tracePanel.charPixSelectedNuc;
		}

		RGBArray newPiece = pixContainerToUse.getRGBArray(byteToDraw);

		try {
			
			// Do something?
			
		} catch (Exception e) {
			logger.info("x" + seqXPos);
			logger.info("y" + seqYPos);
			logger.info("clipX" + pixelPosX);
			logger.info("clipY" + pixelPosY);
			//break;
		}
		
	}
	
	

}
