package aliview.gui.pane;

import java.awt.Color;
import java.awt.Font;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.color.ColorScheme;
import aliview.sequencelist.AlignmentListModel;


public class CharPixelsContainerNucQuality extends CharPixelsContainer{
	static int BASE_VAL_MAX = 255;
	static int QUAL_CLASS_MAX = 9;
	CharPixels[][] backend = new CharPixels[BASE_VAL_MAX + 1][QUAL_CLASS_MAX + 1];
	private static final Logger logger = Logger.getLogger(CharPixelsContainerNucQuality.class);

	// Below is for CompounColorScheme
	private ColorScheme colorScheme;

	public CharPixelsContainerNucQuality() {
		//	logger.info("init CharPixContainer");
	}
	
	@Override
	public RGBArray getRGBArray(byte target) {
		return backend[target][QUAL_CLASS_MAX].getRGBArray();
	}
	
	@Override
	public RGBArray getRGBArray(byte target, short qualVal){
	
		int qualClass = getQualClassFromQualVal(qualVal);
		
//		logger.info("target" + target);
//		logger.info("qualClass" + qualClass);
//		logger.info("backend[target][qualClass]" + backend[target][qualClass]);
		
		
		return backend[target][qualClass].getRGBArray();
	}

	public static int getQualClassFromQualVal(int qualVal) {
		int qualClass = 0;

		qualClass = (int) Math.floor(qualVal / 10);

		if(qualClass < 0){
			qualClass = 0;
		}
		if(qualClass > QUAL_CLASS_MAX){
			qualClass = QUAL_CLASS_MAX;
		}
		return qualClass;
	}

	public int getMaxBaseVal() {
		return BASE_VAL_MAX;
	}

	public int getMaxQualClass() {
		return QUAL_CLASS_MAX;
	}

	public void setCharPixels(int base, int qualVal, CharPixels charPix) {	
		backend[base][qualVal] = charPix;
	}

	public static CharPixelsContainerNucQuality createQualityNucleotideContainer(Font font, int minFontSize, int width, int height, ColorScheme colorScheme, int fontCalse){
		CharPixelsContainerNucQuality container = new CharPixelsContainerNucQuality();

		// Loop all bases and all qualvals
		for(int base = 0; base < container.getMaxBaseVal(); base++){
			for(int qualClass = 0; qualClass <= container.getMaxQualClass(); qualClass++){
				int baseVal = NucleotideUtilities.baseValFromBase((byte)base);
				Color fgColor = colorScheme.getBaseQualityForegroundColor(baseVal, qualClass);
				Color bgColor = colorScheme.getBaseQualityBackgroundColor(baseVal, qualClass);
				CharPixels charPix = new CharPixels((char)base, width, height, fgColor, bgColor, font, minFontSize, fontCalse);
				
				//logger.info("setCharPixels" + "baseVal, qualClass" + baseVal + " " + qualClass);
				
				container.setCharPixels(base, qualClass, charPix);
			}
		}
		return container;
	}
	
}