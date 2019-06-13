package assseq.gui.pane;

import org.apache.log4j.Logger;

import assseq.AminoAcid;
import assseq.alignment.Alignment;
import assseq.sequences.ABISequence;
import assseq.sequences.Sequence;
import assseq.sequences.TraceSequence;

public class SequencePainterAminoAcidTranslated extends SequencePainter {

	private static final Logger logger = Logger.getLogger(SequencePainterAminoAcidTranslated.class);

	public SequencePainterAminoAcidTranslated(Sequence seq, int seqYPos,
			int clipPosY, int xMinSeqPos, int xMaxSeqPos, double seqPerPix,
			double charWidth, double charHeight, double highDPIScaleFactor,
			RGBArray clipRGB, AlignmentPane aliPane, Alignment alignment) {
		super(seq, seqYPos, clipPosY, xMinSeqPos, xMaxSeqPos, seqPerPix, charWidth,
				charHeight, highDPIScaleFactor, clipRGB, aliPane, alignment);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void copyPixels(Sequence seq, RGBArray clipRGB, int seqXPos, int seqYPos, int pixelPosX, int pixelPosY, AlignmentPane aliPane, Alignment alignment) {


		byte residue = seq.getBaseAtPos(seqXPos);
		AminoAcid acid = seq.getTranslatedAminoAcidAtNucleotidePos(seqXPos);	

		// set defaults
		//AminoAcid acid =  aaTransSeq.getAminoAcidAtNucleotidePos(x);
		CharPixelsContainerTranslation pixContainerToUse = aliPane.charPixTranslationDefault;
		CharPixelsContainerTranslation pixLetterContainerToUse = aliPane.charPixTranslationLetter;
		CharPixelsContainerTranslation pixLetterContainerToUseNoAALetter = aliPane.charPixTranslationDefault;


		// adjust colors if selected and temp selection
		// We have to calculate within this way - because rect.contains(Point) is always returning false on a 0-width or 0 height Rectangle
		boolean isPointWithinSelectionRect = false;
		if(alignment.getTempSelection() != null){
			if(seqXPos <= alignment.getTempSelection().getMaxX() && seqXPos >= alignment.getTempSelection().getMinX() && seqYPos <= alignment.getTempSelection().getMaxY() && seqYPos >= alignment.getTempSelection().getMinY()){
				isPointWithinSelectionRect = true;
			}
		}
		if(seq.isBaseSelected(seqXPos) || (alignment.getTempSelection() != null && isPointWithinSelectionRect)){		
			pixContainerToUse = aliPane.charPixTranslationSelected;
			pixLetterContainerToUse = aliPane.charPixTranslationSelectedLetter;			
		}
		
		if(seq instanceof TraceSequence){
			ABISequence abiSeq = (ABISequence) seq;
			if(abiSeq.isQualClippedAtPos(seqXPos)){
				pixContainerToUse = aliPane.charPixTranslationQualClip;
				pixLetterContainerToUse = aliPane.charPixTranslationQualClip;
				pixLetterContainerToUseNoAALetter = aliPane.charPixTranslationQualClip;
				
				logger.info("use:" + aliPane.charPixQualClipNuc);
			}
		}

		RGBArray newPiece;

		if(! aliPane.isDrawAminoAcidCode()){	
			newPiece = pixContainerToUse.getRGBArray(acid, residue);
		}else{
			if(seq.isCodonSecondPos(seqXPos)){
				newPiece = pixLetterContainerToUse.getRGBArray(acid, residue);
			}else{
				residue = ' ';
				newPiece = pixLetterContainerToUseNoAALetter.getRGBArray(acid, residue);
			}
		}

		try {
			ImageUtils.insertRGBArrayAt(pixelPosX, pixelPosY, newPiece, clipRGB);
		} catch (Exception e) {
			logger.info("clipX" + pixelPosX);
			logger.info("clipY" + pixelPosY);
		}
	}

}
