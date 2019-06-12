package aliview.gui.pane;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import aliview.ScrollViewChangeListener;
import aliview.alignment.AlignmentEvent;
import aliview.alignment.AlignmentListener;

public class ViewModel implements ChangeListener{
	
	private static final Logger logger = Logger.getLogger(ScrollViewChangeListener.class);
	private double charWidth;
	private double charHeight;
	private Point viewPoint;
	private ArrayList<ViewListener> viewListeners = new ArrayList<ViewListener>();
	private boolean disabled = false;

	public Point getViewPoint() {
		return viewPoint;
	}
	
	public double getCharHeight() {
		return charHeight;
	}
	
	public double getCharWidth() {
		return charWidth;
	}

	public void setNewView(Object source, double charWidth, double charHeight, Point viewPoint) {	
		this.charWidth = charWidth;
		this.charHeight = charHeight;
		this.viewPoint = viewPoint;
		fireViewChanged(source);
	}
	
	public void addViewListener(ViewListener listener) {
		viewListeners.add(listener);
	}

	private void fireViewChanged(Object source) {
		
		for(ViewListener listener: viewListeners){
			if(source != listener) {
				listener.viewChanged(new ViewEvent(this));
			}
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		
		logger.debug("stateChanged" + e);
		
		JViewport source = (JViewport) e.getSource();
		AlignmentPane sourcePane = (AlignmentPane) source.getView();
		if(sourcePane.getCharWidth() != charWidth) {
			// Skip it
		}
		else if(source.getViewPosition() != viewPoint) {
			this.viewPoint =  source.getViewPosition();
			fireViewChanged(sourcePane);
		}
		
	}
	
}
