package assseq.gui.pane;

import org.apache.log4j.Logger;

import assseq.NucleotideUtilities;
import assseq.alignment.Alignment;
import assseq.alignment.NucleotideHistogram;
import assseq.sequences.ABISequence;
import assseq.sequences.QualCalledSequence;
import assseq.sequences.Sequence;

public class SequencePainterNucleotide extends SequencePainter {

	private static final Logger logger = Logger.getLogger(SequencePainterNucleotide.class);

	public SequencePainterNucleotide(Sequence seq, int seqYPos, int clipPosY,
			int xMinSeqPos, int xMaxSeqPos, double step, double charWidth,
			double charHeight, double highDPIScaleFactor, RGBArray clipRGB,
			AlignmentPane aliPane, Alignment alignment) {
		super(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, step, charWidth,
				charHeight, highDPIScaleFactor, clipRGB, aliPane, alignment);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void copyPixels(Sequence seq, RGBArray clipArray, int seqXPos, int seqYPos, int pixelPosX, int pixelPosY, AlignmentPane aliPane, Alignment alignment){

		byte residue = seq.getBaseAtPos(seqXPos);

		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		short qualVal = 100;
		if(seq instanceof QualCalledSequence){
			qualVal = ((QualCalledSequence) seq).getQualValAtPos(seqXPos);
		}

		// set defaults
		CharPixelsContainer pixContainerToUse = aliPane.charPixQualityNuc;
		//CharPixelsContainer pixContainerToUse = aliPane.charPixDefaultNuc;
		byte byteToDraw = residue;
		int baseVal = NucleotideUtilities.baseValFromBase(residue);

		// adjustment if only diff compared to diff-trace-selected sequence is to be shown 
		if(aliPane.isHighlightDiffTrace()){ // TODO CHANGE THIS SO IT IS WORKING EVEN IF TRACING SEQUENCE IS SHORTER THAN OTHER
			if(seqYPos != aliPane.differenceTraceSequencePosition){
				if(baseVal == NucleotideUtilities.baseValFromBase(alignment.getBaseAt(seqXPos,aliPane.getDifferenceTraceSequencePosition()))){
					byteToDraw = '.';
					pixContainerToUse = aliPane.charPixDefaultNuc;
				}
			}
		}


		if(aliPane.isHighlightCons()){
			NucleotideHistogram nucHistogram = (NucleotideHistogram) alignment.getHistogram();
			if(baseVal == NucleotideUtilities.GAP){
				// no color on gap even if they are in maj.cons
			}
			else if(! nucHistogram.isMajorityRuleConsensus(seqXPos,baseVal)){
				pixContainerToUse = aliPane.charPixConsensusNuc;
			}
		}

		// adjustment if non-cons is to be highlighted
		if(aliPane.isHighlightNonCons()){
			byte cons = alignment.getFixedNucleotideConsensusBaseValAt(seqXPos);
			// no color on gap even if they are in maj.cons
			if(NucleotideUtilities.baseValFromBase(residue) != NucleotideUtilities.baseValFromBase(cons)){
				pixContainerToUse = aliPane.charPixNonConsensusNuc;
			}
		}

		if(seq instanceof QualCalledSequence){
			QualCalledSequence qualCalledSeq = (QualCalledSequence) seq;
			if(qualCalledSeq.isQualClippedAtPos(seqXPos)){
				pixContainerToUse = aliPane.charPixQualClipNuc;
			}
		}

		// Adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(seq.isBaseSelected(seqXPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			pixContainerToUse = aliPane.charPixSelectedNuc;
			if(seq instanceof QualCalledSequence){
				logger.info("qalVal=" + qualVal);
			}
		}	


		RGBArray newPiece = pixContainerToUse.getRGBArray(byteToDraw, qualVal);

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
