package assseq.gui.pane;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import assseq.AminoAcid;
import assseq.NucleotideUtilities;
import assseq.alignment.Alignment;
import assseq.color.ColorScheme;
import assseq.sequencelist.AlignmentListModel;


public class CharPixelsContainerAA {
	private static final Logger logger = Logger.getLogger(CharPixelsContainerAA.class);
	private CharPixelsContainerCompound compoundContainer;
	private CharPixelsContainer container;

	// Below is for CompounColorScheme
	private ColorScheme colorScheme;


	public RGBArray getRGBArray(byte residue, int xPos, Alignment alignment){
		if(compoundContainer != null){
			return compoundContainer.getRGBArray(residue, xPos, alignment);

		}else{
			return container.getRGBArray(residue);
		}
	}

	public void setCompoundContainer(
			CharPixelsContainerCompound compoundContainer) {
		this.compoundContainer = compoundContainer;
	}

	public void setContainer(CharPixelsContainer container) {
		this.container = container;
	}
	
	public static CharPixelsContainerAA createDefaultAAContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerAA aaContainer = new CharPixelsContainerAA();	
		if(colorScheme.getALLCompundColors() != null){
			CharPixelsContainerCompound compContainer = CharPixelsContainerCompound.createDefaultCompoundColorContainer(font, minFontSize, width, height, colorScheme, fontCase);
			aaContainer.setCompoundContainer(compContainer);
		}else{
			CharPixelsContainer container = new CharPixelsContainer();	
			for(int n = 0; n < container.backend.length; n++){	
				AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
				Color fgColor = colorScheme.getAminoAcidForgroundColor(aa);
				Color bgColor = colorScheme.getAminoAcidBackgroundColor(aa);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			aaContainer.setContainer(container);
		}		
		return aaContainer;
	}

	public static CharPixelsContainerAA createSelectedAAContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		
		CharPixelsContainerAA aaContainer = new CharPixelsContainerAA();	
		if(colorScheme.getALLCompundColors() != null){
			CharPixelsContainerCompound compContainer = CharPixelsContainerCompound.createSelectedCompoundColorContainer(font, minFontSize, width, height, colorScheme, fontCase);
			aaContainer.setCompoundContainer(compContainer);
		}else{
			CharPixelsContainer container = new CharPixelsContainer();	
			for(int n = 0; n < container.backend.length; n++){	
				AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
				Color fgColor = colorScheme.getAminoAcidSelectionForegroundColor(aa);
				Color bgColor = colorScheme.getAminoAcidSelectionBackgroundColor(aa);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize,fontCase);
			}
			aaContainer.setContainer(container);
		}		
		return aaContainer;
	}

	public static CharPixelsContainerAA createConsensusAAContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		
		CharPixelsContainerAA aaContainer = new CharPixelsContainerAA();	
		if(colorScheme.getALLCompundColors() != null){
			CharPixelsContainerCompound compContainer = CharPixelsContainerCompound.createDefaultCompoundColorContainer(font, minFontSize, width, height, colorScheme, fontCase);
			aaContainer.setCompoundContainer(compContainer);
		}else{
			CharPixelsContainer container = new CharPixelsContainer();	
			for(int n = 0; n < container.backend.length; n++){	
				AminoAcid aa = AminoAcid.getAminoAcidFromByte((byte)n);
				Color fgColor = colorScheme.getAminoAcidForgroundColor(aa);
				Color bgColor = colorScheme.getBaseConsensusBackgroundColor();
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			aaContainer.setContainer(container);
		}		
		return aaContainer;
		
	}

}