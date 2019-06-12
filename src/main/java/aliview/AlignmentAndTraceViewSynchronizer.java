package aliview;

import java.awt.Point;

import org.apache.log4j.Logger;

import aliview.gui.pane.AlignmentPane;
import aliview.gui.pane.TracePanel;
import aliview.gui.pane.ViewEvent;
import aliview.gui.pane.ViewListener;

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
