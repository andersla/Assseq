package assseq;

import java.awt.Point;

import org.apache.log4j.Logger;

import assseq.gui.pane.AlignmentPane;
import assseq.gui.pane.TracePanel;
import assseq.gui.pane.ViewEvent;
import assseq.gui.pane.ViewListener;

public class AlignmentAndTraceViewSynchronizer implements ViewListener {
	private static final Logger logger = Logger.getLogger(AlignmentAndTraceViewSynchronizer.class);

	private AlignmentPane alignmentPane;
	private TracePanel tracePanel;

	public AlignmentAndTraceViewSynchronizer(AlignmentPane alignmentPane, TracePanel tracePanel) {
		this.alignmentPane = alignmentPane;
		this.tracePanel = tracePanel;
	}

	public void viewChanged(ViewEvent event) {

		AlignmentPane source = (AlignmentPane) event.getSource();

		logger.info("source:" + source);
		
		/*
		if(source == alignmentPane){
			AlignmentPane dest = tracePanel;
			Point p = source.getVisibleCenterMatrixPos();
			dest.scrollToPos(p);
			
			logger.debug("scrollToPos" + p);
			
		}
		
*/
	}
	


}
