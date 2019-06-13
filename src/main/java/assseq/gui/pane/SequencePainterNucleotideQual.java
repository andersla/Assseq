package assseq.gui.pane;

import org.apache.log4j.Logger;

import assseq.NucleotideUtilities;
import assseq.alignment.Alignment;
import assseq.sequences.ABISequence;
import assseq.sequences.Sequence;
import assseq.sequences.TraceSequence;

public class SequencePainterNucleotideQual extends SequencePainter {

	private static final Logger logger = Logger.getLogger(SequencePainterNucleotideQual.class);

	public SequencePainterNucleotideQual(Sequence seq, int seqYPos, int clipPosY,
			int xMinSeqPos, int xMaxSeqPos, double step, double charWidth,
			double charHeight, double highDPIScaleFactor, RGBArray clipRGB,
			AlignmentPane aliPane, Alignment alignment) {
		super(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, step, charWidth,
				charHeight, highDPIScaleFactor, clipRGB, aliPane, alignment);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void copyPixels(Sequence sequence, RGBArray clipArray, int seqXPos, int seqYPos, int pixelPosX, int pixelPosY, AlignmentPane aliPane, Alignment alignment){
	/*	
		ABISequence seq = (ABISequence) sequence;
		
		byte residue = seq.getBaseAtPos(seqXPos);
		
		short qualVal = 100;
		if(seq instanceof TraceSequence){
			qualVal = ((TraceSequence) seq).getQualValAtPos(seqXPos);
		}
		

		// A small hack
		if(residue == 0){
			residue = ' ';
		}

		// set defaults
		CharPixelsContainer pixContainerToUse = aliPane.charPixQualityNuc;
		byte byteToDraw = residue;
		int baseVal = NucleotideUtilities.baseValFromBase(residue);
		
		
		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		
		logger.info("Exit here");
		System.exit(1);
		
		if(seq.isQualClippedAtPos(seqXPos)){
			pixContainerToUse = aliPane.charPixQualClipNuc;
			logger.info("use:" + aliPane.charPixQualClipNuc);
		}
		
		if(seq.isBaseSelected(seqXPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){
			pixContainerToUse = aliPane.charPixSelectedNuc;
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
		*/
	}
	

}
