package assseq.gui.pane;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import assseq.AminoAcid;
import assseq.NucleotideUtilities;
import assseq.alignment.Alignment;
import assseq.alignment.NucleotideHistogram;
import assseq.color.ColorScheme;
import assseq.color.DefaultColorScheme;
import assseq.sequences.AminoAcidAndPosition;
import assseq.sequences.Sequence;
import assseq.sequences.SequenceUtils;
import assseq.sequences.TraceSequence;
import assseq.sequences.Traces;
import assseq.utils.ArrayUtilities;

public class TracePainter implements Runnable{
	private static final Logger logger = Logger.getLogger(TracePainter.class);

	private TraceSequence seq;
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
	private int clipXPosStart;
	private int clipYPosStart;

	private ColorScheme colorSchemeNucleotide;


	public TracePainter(TraceSequence seq, int seqYPos, int clipPosY, int xMinSeqPos,
			int xMaxSeqPos, double seqPerPix, double charWidth, double charHeight, double highDPIScaleFactor,
			TracePanel tracePanel, Alignment alignment, Graphics2D g2d, Rectangle clip, int clipXPosStart, int clipYPosStart, ColorScheme colorSchemeNucleotide) {
		super();
		this.seq = seq;
		this.seqYPos = seqYPos;
		this.clipPosY = clipPosY;
		this.xMinSeqPos = xMinSeqPos;
		this.xMaxSeqPos = xMaxSeqPos;
		this.seqPerPix = seqPerPix;
		this.charWidth = charWidth;
		this.charHeight = charHeight;
		this.highDPIScaleFactor = highDPIScaleFactor;
		this.tracePanel = tracePanel;
		this.alignment = alignment;
		this.g2d = g2d;
		this.clip = clip;
		this.clipXPosStart = clipXPosStart;
		this.clipYPosStart = clipYPosStart;
		this.colorSchemeNucleotide = colorSchemeNucleotide;
	}

	public void run(){
		// TODO maybe check before that sequence not is null
		if(seq != null){
			drawTrace(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, seqPerPix, charWidth, charHeight, highDPIScaleFactor, tracePanel, alignment, g2d, clip, clipXPosStart, clipYPosStart, colorSchemeNucleotide);
		}
	}

	public void drawTrace(TraceSequence seq, int seqYPos, int clipPosY, int xMin, int xMax, double seqPerPix, double charWidth, double charHeight, double highDPIScaleFactor,
			TracePanel tracePanel, Alignment alignment, Graphics2D g2d,Rectangle clip, int clipXPosStart, int clipYPosStart, ColorScheme colorSchemeNucleotide){


		// Make sure not outside length of seq
		int seqLength = seq.getLength();
		int clipPosX = 0;
		for(int x = xMin; x < xMax && x >=0 ; x ++){
			int seqXPos = (int)((double)x * seqPerPix);
			if(seqXPos >=0 && seqXPos < seqLength){

				//				logger.debug("xmin" + xMin);
				//				logger.debug("xmax" + xMax);
				//				logger.debug("clipPosX" + clipPosX);
				//				logger.debug("charWidth" + charWidth);
				//				logger.debug("highDPIScaleFactor" + highDPIScaleFactor);
				//				logger.debug("clip" + clip);
				//				logger.debug("clipXPosStart" + clipXPosStart);
				//				logger.debug("clipYPosStart" + clipYPosStart);


				int pixelPosX = (int)(clipPosX*charWidth*highDPIScaleFactor);
				int pixelPosY = (int)(clipPosY*charHeight*highDPIScaleFactor);

				// Since I am drawing on the actual display and not buffer
				// add the clipPos here (when buffering draw on new image (clip size + little more) and copy)
				int drawPixelXPos = pixelPosX + clipXPosStart;
				int drawPixelYPos = pixelPosY + clipYPosStart;

				//				logger.debug("drawPixelXPos" + pixelPosX);
				//				logger.debug("drawPixelYPos" + pixelPosY);


				//if(pixelPosX < clip.getWidth() && pixelPosY < clip.getHeight()){
				draw(seq,seqXPos, seqYPos, drawPixelXPos , drawPixelYPos, tracePanel, alignment, g2d, clip, colorSchemeNucleotide, charWidth, charHeight);
				//}
			}
			clipPosX ++;
		}
	}

	private void draw(TraceSequence seq, int seqXPos, int seqYPos, int drawPixelXPos, int drawPixelYPos, TracePanel tracePanel,
			Alignment alignment, Graphics2D g2d, Rectangle clip, ColorScheme colorScheme, double charWidth, double charHeight) {	

		byte residue = seq.getBaseAtPos(seqXPos);

		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		int baseVal = NucleotideUtilities.baseValFromBase(residue);

		// Set default colors (can be overridden below depending on if trace is qual-clipped, selected etc.)
		Color bgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);
		Color colorTraceG = colorScheme.getBaseForegroundColor(NucleotideUtilities.baseValFromBase((byte)'G'));
		Color colorTraceC = colorScheme.getBaseForegroundColor(NucleotideUtilities.baseValFromBase((byte)'C'));
		Color colorTraceT = colorScheme.getBaseForegroundColor(NucleotideUtilities.baseValFromBase((byte)'T'));
		Color colorTraceA = colorScheme.getBaseForegroundColor(NucleotideUtilities.baseValFromBase((byte)'A'));

		// adjustment if non-cons to be highlighted
		if(tracePanel.isHighlightNonCons()){
			NucleotideHistogram nucHistogram = (NucleotideHistogram) alignment.getHistogram();
			if(baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! nucHistogram.isMajorityRuleConsensus(seqXPos,baseVal)){
				//bgColor = colorScheme.getBaseNonConsensusBackgroundColor();
			}
		}

		if(tracePanel.isHighlightNonCons()){
			byte cons = alignment.getFixedNucleotideConsensusBaseValAt(seqXPos);
			if(baseVal == NucleotideUtilities.UNKNOWN ||
					cons == NucleotideUtilities.UNKNOWN ||
					baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(residue != cons){
				bgColor = colorScheme.getBaseNonConsensusBackgroundColor();
			}
		}


		int[] traceA = seq.getTraces().getTraceAVals(seqXPos);
		//		if(seq.isBaseSelected(seqXPos)){
		//				logger.debug("traceA" + Arrays.toString(traceA));
		//		}
		traceA = transformTraceArrayToLineYCoords(traceA, charWidth, charHeight, drawPixelYPos);

		int[] traceG = seq.getTraces().getTraceGVals(seqXPos);
		//		if(seq.isBaseSelected(seqXPos)){
		//			logger.debug("traceG" + Arrays.toString(traceG));
		//	    }
		traceG = transformTraceArrayToLineYCoords(traceG, charWidth, charHeight, drawPixelYPos);

		int[] traceC = seq.getTraces().getTraceCVals(seqXPos);
		//		if(seq.isBaseSelected(seqXPos)){
		//			logger.debug("traceC" + Arrays.toString(traceC));
		//	    }
		traceC = transformTraceArrayToLineYCoords(traceC, charWidth, charHeight, drawPixelYPos);

		int[] traceT = seq.getTraces().getTraceTVals(seqXPos); 
		//		if(seq.isBaseSelected(seqXPos)){
		//			logger.debug("traceT" + Arrays.toString(traceT));
		//	    }
		traceT = transformTraceArrayToLineYCoords(traceT, charWidth, charHeight, drawPixelYPos);

		// All traces have same base-calls so one array of x-pos is enough
		int[] traceXpos = createMatchingLineXCoords(traceA, charWidth, drawPixelXPos);


		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}

		if(seq.isQualClippedAtPos(seqXPos)){
			bgColor = colorScheme.getBaseBackgroundColor(NucleotideUtilities.GAP);

			colorTraceG = colorScheme.getQualClipForegroundColor();
			colorTraceC = colorScheme.getQualClipForegroundColor();
			colorTraceT = colorScheme.getQualClipForegroundColor();
			colorTraceA = colorScheme.getQualClipForegroundColor();

		}

		if(seq.isBaseSelected(seqXPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			bgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal);

			//			logger.debug("traceA" + Arrays.toString(traceA));
			//			logger.debug("traceC" + Arrays.toString(traceC));
			//			logger.debug("traceG" + Arrays.toString(traceG));
			//			logger.debug("traceT" + Arrays.toString(traceT));
			//			logger.debug("traceXpos" + Arrays.toString(traceXpos));

		}



		try {

			//logger.debug("DrawInTrace, drawPixelXPos" + drawPixelXPos);

			//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			//		RenderingHints.VALUE_ANTIALIAS_ON);

			// bgcolor background
			g2d.setColor(bgColor);
			g2d.fillRect(drawPixelXPos, drawPixelYPos, (int)charWidth, (int)charHeight);

//			// Only draw if some vals are there
			//if(! ArrayUtils.contains(traceA, Traces.NO_DATA_TRACEVAL)){
			if(seq.getTraces().hasTraceDataAtPos(seqXPos)) {

				g2d.setColor(colorTraceG);
				g2d.drawPolyline(traceXpos, traceG, traceXpos.length);

				g2d.setColor(colorTraceC);
				g2d.drawPolyline(traceXpos, traceC, traceXpos.length);

				g2d.setColor(colorTraceT);
				g2d.drawPolyline(traceXpos, traceT, traceXpos.length);

				g2d.setColor(colorTraceA);
				g2d.drawPolyline(traceXpos, traceA, traceXpos.length);
			}

		} catch (Exception e) {
			logger.info("x" + seqXPos);
			logger.info("y" + seqYPos);
		}

		/*

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
		 */


		/*
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

		 */



	}

	private int[] createMatchingLineXCoords(int[] trace, double charWidth, int drawPixelXPos) {

		//		logger.info("trace.length" + trace.length);

		if(trace.length == 0) {
			return trace;
		}

		// Create an array of matching length to the trace with min and max values and values evenly spread out in between
		int minVal = 0;
		int maxVal = (int)charWidth ; // Draw one pos longer to make it conect to next char, since index is 0, maxVal should have been charWidth -1 otherwise
		int[] traceXpos = ArrayUtilities.createEvenDistributedArray(trace.length, minVal, maxVal);

		// Add the panel-draw-pos offset to all x-values
		ArrayUtilities.addToArrayValues(traceXpos, drawPixelXPos);
		//logger.debug(Arrays.toString(traceXpos));

		return traceXpos;
	}

	private int[] transformTraceArrayToLineYCoords(int[] trace, double charWidth, double charHeight, int yPosOffset) {

		if(trace.length == 0) {
			return trace;
		}

		charHeight = charHeight - 1;

		// Scale trace vals to charHeight
		int MAX_TRACE_VAL = 1000;
		ArrayUtilities.scaleVals(trace, charHeight/MAX_TRACE_VAL);

		// Scale length of array if needed
		if(trace.length > charWidth) {
			trace = ArrayUtilities.scaleLength(trace, (int)charWidth);
		}

		// Invert y -values (since in drawing coord-space higher values are going down)
		ArrayUtilities.invert(trace, (int)charHeight);

		// Add the panel-draw-pos offset to all y-values
		ArrayUtilities.addToArrayValues(trace, yPosOffset);

		return trace;
	}



}
