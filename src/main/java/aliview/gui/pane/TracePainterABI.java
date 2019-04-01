package aliview.gui.pane;

import org.apache.log4j.Logger;

import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.alignment.NucleotideHistogram;
import aliview.sequences.Sequence;

public class TracePainterABI extends TracePainter {

	private static final Logger logger = Logger.getLogger(TracePainterABI.class);

	public TracePainterABI(Sequence seq, int seqYPos, int clipPosY,
			int xMinSeqPos, int xMaxSeqPos, double step, double charWidth,
			double charHeight, double highDPIScaleFactor, RGBArray clipRGB,
			TracePanel tracePanel, Alignment alignment) {
		super(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, step, charWidth,
				charHeight, highDPIScaleFactor, clipRGB, tracePanel, alignment);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void copyPixels(Sequence seq, RGBArray clipArray, int seqXPos, int seqYPos, int pixelPosX, int pixelPosY, TracePanel tracePanel, Alignment alignment){

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
			ImageUtils.insertRGBArrayAt(pixelPosX, pixelPosY, newPiece, clipArray);
		} catch (Exception e) {
			logger.info("x" + seqXPos);
			logger.info("y" + seqYPos);
			logger.info("clipX" + pixelPosX);
			logger.info("clipY" + pixelPosY);
			//break;
		}
	}

}
