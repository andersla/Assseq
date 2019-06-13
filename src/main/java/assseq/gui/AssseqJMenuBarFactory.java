package assseq.gui;

import org.apache.log4j.Logger;

import assseq.AssseqWindow;


/**
 * 
 *  The idea with this class was to create different JMenuBar depending on OS
 *  (and not creating more than 1 if MacOSX - returning the same one all the time - but this seem not to be needed) 
 * 
 */

public class AssseqJMenuBarFactory{
	private static final Logger logger = Logger.getLogger(AssseqJMenuBarFactory.class);
	//private JMenuBar macMenuBar;

	public AssseqJMenuBarFactory() {		
	}

	public AssseqJMenuBar create(AssseqWindow aliViewWin){
		return new AssseqJMenuBar(aliViewWin);
	}
}
