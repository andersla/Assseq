package aliview.gui.pane;

import java.awt.Color;
import java.awt.Font;

import org.apache.log4j.Logger;

import aliview.AminoAcid;
import aliview.NucleotideUtilities;
import aliview.alignment.Alignment;
import aliview.color.ColorScheme;
import aliview.sequencelist.AlignmentListModel;


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
	
}