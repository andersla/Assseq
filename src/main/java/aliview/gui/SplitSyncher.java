package aliview.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

public class SplitSyncher {

	public SplitSyncher(final JSplitPane pane1, final JSplitPane pane2) {
		
		// Do initial sync
        int sourceDividerPos = pane1.getDividerLocation();
        int destDividerPos = pane2.getDividerLocation();
        if(destDividerPos != sourceDividerPos) {
        	pane2.setDividerLocation(sourceDividerPos);
        }
        
        // Create listener
		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent changeEvent) {
				JSplitPane sourceSplitPane = (JSplitPane) changeEvent.getSource();

				JSplitPane destSplitPane = null;
				if(sourceSplitPane == pane1) {
					destSplitPane = pane2;
				}else {
					destSplitPane = pane1;
				}

				String propertyName = changeEvent.getPropertyName();
				if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
					int sourceDividerPos = sourceSplitPane.getDividerLocation();
					int destDividerPos = destSplitPane.getDividerLocation();
					if(destDividerPos != sourceDividerPos) {
						destSplitPane.setDividerLocation(sourceDividerPos);
					}

				}
			}
		};
		
		// Register listener
		pane1.addPropertyChangeListener(propertyChangeListener);
		pane2.addPropertyChangeListener(propertyChangeListener);

	}
}