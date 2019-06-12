package aliview.gui;

import java.awt.Point;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import aliview.gui.pane.AlignmentPane;
import aliview.sequencelist.SequenceJList;

public class PanelAndListSynchronizer {
	
	private static final Logger logger = Logger.getLogger(PanelAndListSynchronizer.class);
	private JScrollPane listScrollPane;
	private JScrollPane alignmentScrollPane;

	public PanelAndListSynchronizer(SequenceJList sequenceJList, AlignmentPane alignmentPane,
			JScrollPane listScrollPane, JScrollPane alignmentScrollPane) {
		this.listScrollPane = listScrollPane;
		this.alignmentScrollPane = alignmentScrollPane;
	}
	
	private void synchAlignmentScrollPane(){
		logger.info("synch ScrollPanes");
		JScrollPane source = listScrollPane;
		JScrollPane dest = alignmentScrollPane;
		if(source != null && dest != null){
			Point viewPos = new Point(dest.getViewport().getViewPosition().x, source.getViewport().getViewPosition().y );
			dest.getViewport().setViewPosition(viewPos);	
		}
	}
	

}
