package assseq;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import assseq.gui.pane.AlignmentPane;


public class ScrollViewChangeListener implements ChangeListener {
	private static final Logger logger = Logger.getLogger(ScrollViewChangeListener.class);
	private JScrollPane scrollPane;

	public ScrollViewChangeListener(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public void stateChanged(ChangeEvent e) {
		
		logger.debug("stateChanged" + e);
		
		JViewport source = (JViewport) e.getSource();
		AlignmentPane sourceAliPane = (AlignmentPane) source.getView();
		
		AlignmentPane destAliPane = (AlignmentPane) scrollPane.getViewport().getView();
		
		if(destAliPane.getCharWidth() != sourceAliPane.getCharWidth()) {
			// There was a zoom event - lets skip it
		}
		else if(scrollPane.getViewport().getViewPosition() != source.getViewPosition()) {
			//destAliPane.setCharWidth(sourceAliPane.getCharWidth());
			
			logger.debug("destAliPane.getCharWidth()" + destAliPane.getCharWidth());
			logger.debug("sourceAliPane.getCharWidth()" + sourceAliPane.getCharWidth());
			
			scrollPane.getViewport().setViewPosition(source.getViewPosition());
		}
	}
}
