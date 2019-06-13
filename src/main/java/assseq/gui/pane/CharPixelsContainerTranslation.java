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


public class CharPixelsContainerTranslation {
	private static final Logger logger = Logger.getLogger(CharPixelsContainerTranslation.class);
	private CharPixelsContainer[] allAAContainers;

	public RGBArray getRGBArray(AminoAcid aa, byte residue){
		return allAAContainers[aa.intVal].getRGBArray(residue);
	}


	public static CharPixelsContainerTranslation createDefaultTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color bgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createSelectedTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				// It looks better without the selected color for foreground
				Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color bgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createLetterTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				Color fgColor = Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color bgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels(containerAcid.getCodeCharVal(), width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createSelectedLetterTranslationPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				Color fgColor = Color.white; //colorScheme.getAminoAcidForgroundColor(containerAcid);
				Color bgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid);
				container.backend[n] = new CharPixels(containerAcid.getCodeCharVal(), width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	////////////////////////


	public static CharPixelsContainerTranslation createDefaultTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.WHITE;
				Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				Color nucFgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal).brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createDefaultTranslationAndNucPixelsContainerNoAALetter(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.WHITE;
				Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid);
				Color nucFgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal).brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,' ', width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createSelectedTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.white;
				Color aaBgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid);
				Color nucFgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal).brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createSelectedTranslationAndNucPixelsContainerNoAALetter(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.white; 
				Color aaBgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid);
				Color nucFgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal).brighter();
				container.backend[n] = new CharPixelsBothNucAndAA((char)n,' ', width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}


	public static CharPixelsContainerTranslation createDominantNucTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.WHITE;
				Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid).brighter().brighter();;
				Color nucFgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal);
				container.backend[n] = new CharPixelsBothNucAndAA((char)n, containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createDominantNucTranslationAndNucPixelsContainerNoAALetter(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.WHITE;
				Color aaBgColor = colorScheme.getAminoAcidBackgroundColor(containerAcid).brighter().brighter();
				Color nucFgColor = colorScheme.getBaseForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseBackgroundColor(baseVal);
				container.backend[n] = new CharPixelsBothNucAndAA((char)n, ' ', width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

	public static CharPixelsContainerTranslation createSelectedDominantNucTranslationAndNucPixelsContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.white;
				Color aaBgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid).brighter().brighter();;
				Color nucFgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal);
				container.backend[n] = new CharPixelsBothNucAndAA((char)n, containerAcid.getCodeCharVal(), width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;

	}

	public static CharPixelsContainerTranslation createSelectedDominantNucTranslationAndNucPixelsContainerNoAALetter(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color aaFgColor = Color.white;
				Color aaBgColor = colorScheme.getAminoAcidSelectionBackgroundColor(containerAcid).brighter().brighter();
				Color nucFgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
				Color nucBgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal);
				container.backend[n] = new CharPixelsBothNucAndAA((char)n, ' ', width, height, nucFgColor, nucBgColor, aaFgColor, aaBgColor, font.deriveFont((float)font.getSize()*.67f), minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;

	}


	public static CharPixelsContainerTranslation createTranslationQualClip(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase) {
		CharPixelsContainerTranslation transContainer = new CharPixelsContainerTranslation();
		transContainer.allAAContainers = new CharPixelsContainer[AminoAcid.HIGEST_AA_INT_VAL + 1];
		for(AminoAcid containerAcid: AminoAcid.GROUP_ALL){
			CharPixelsContainer container = new CharPixelsContainer();
			for(int n = 0; n < container.backend.length; n++){	
				int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
				Color fgColor = colorScheme.getQualClipForegroundColor();
				Color bgColor = colorScheme.getQualClipBackgroundColor();
				container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
			}
			transContainer.allAAContainers[containerAcid.intVal] = container;
		}
		return transContainer;
	}

}