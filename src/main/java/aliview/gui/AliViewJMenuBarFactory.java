package aliview.gui;

import org.apache.log4j.Logger;

import aliview.AssseqWindow;


/**
 * 
 *  The idea with this class was to create different JMenuBar depending on OS
 *  (and not creating more than 1 if MacOSX - returning the same one all the time - but this seem not to be needed) 
 * 
 */

public class AliViewJMenuBarFactory{
	private static final Logger logger = Logger.getLogger(AliViewJMenuBarFactory.class);
	//private JMenuBar macMenuBar;

	public AliViewJMenuBarFactory() {		
	}

	public AliViewJMenuBar create(AssseqWindow aliViewWin){
		return new AliViewJMenuBar(aliViewWin);
	}
}
