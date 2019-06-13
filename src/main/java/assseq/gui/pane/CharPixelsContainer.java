package assseq.gui.pane;

import java.awt.Color;
import java.awt.Font;

import org.apache.log4j.Logger;

import assseq.AminoAcid;
import assseq.NucleotideUtilities;
import assseq.alignment.Alignment;
import assseq.color.ColorScheme;
import assseq.sequencelist.AlignmentListModel;


public class CharPixelsContainer {
	CharPixels[] backend = new CharPixels[256];
	private static final Logger logger = Logger.getLogger(CharPixelsContainer.class);

	// Below is for CompounColorScheme
	private ColorScheme colorScheme;

	public CharPixelsContainer() {
		//	logger.info("init CharPixContainer");
	}

	public RGBArray getRGBArray(byte target){
		return backend[target].getRGBArray();
	}
	
	public RGBArray getRGBArray(byte target, short qualVal) {
		return backend[target].getRGBArray();
	}

	public static CharPixelsContainer createDefaultNucleotideContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCalse){

		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){
			int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
			Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
			Color bgColor = colorScheme.getBaseBackgroundColor(baseVal);
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCalse);
		}
		return container;
	}

	public static CharPixelsContainer createSelectedNucleotideContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase){
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
			Color fgColor = colorScheme.getBaseSelectionForegroundColor(baseVal);
			Color bgColor = colorScheme.getBaseSelectionBackgroundColor(baseVal);
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
		}
		return container;
	}

	public static CharPixelsContainer createConsensusNucleotideContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase){
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
			Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
			Color bgColor = colorScheme.getBaseConsensusBackgroundColor();
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
		}
		return container;
	}

	public static CharPixelsContainer createNonConsensusNucleotideContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase){
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			int baseVal = NucleotideUtilities.baseValFromBase((byte)n);
			Color fgColor = colorScheme.getBaseForegroundColor(baseVal);
			Color bgColor = colorScheme.getBaseNonConsensusBackgroundColor();
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
		}
		return container;
	}

	public static CharPixelsContainer createQualityClippedNucleotideContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCase){
		CharPixelsContainer container = new CharPixelsContainer();	
		for(int n = 0; n < container.backend.length; n++){	
			Color fgColor = colorScheme.getQualClipForegroundColor();
			Color bgColor = colorScheme.getQualClipBackgroundColor();
			container.backend[n] = new CharPixels((char)n, width, height, fgColor, bgColor, font, minFontSize, fontCase);
		}
		return container;
	}

}