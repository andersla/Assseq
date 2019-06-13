package assseq.sequencelist;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JList;

import org.apache.log4j.Logger;

import assseq.Assseq;
import assseq.AssseqWindow;
import assseq.alignment.Alignment;
import assseq.gui.pane.AlignmentPane;
import assseq.settings.Settings;
import utils.OSNativeUtils;

/*
 * 
 * 
 * 
 */
public class SequenceListMouseWheelListener implements MouseWheelListener{
	private static final Logger logger = Logger.getLogger(SequenceListMouseWheelListener.class);

	private AlignmentPane alignmentPane;


	public SequenceListMouseWheelListener(AlignmentPane alignmentPane) {
		this.alignmentPane = alignmentPane;
	}


	public void mouseWheelMoved(MouseWheelEvent e) {
		// Zoom in out if ctrl is pressed
		if(e.getModifiersEx() ==  OSNativeUtils.getMouseWheelZoomModifierMask()){
			if(e.getWheelRotation() > 0){
				alignmentPane.zoomOutAt(e.getPoint());
			}
			else if(e.getWheelRotation() < 0){		
				alignmentPane.zoomInAt(e.getPoint());
			}

			return;
			//e.consume();
		}

		// Else scroll pane up or down				
		else{

			int wheelRotation = e.getWheelRotation();
			if(alignmentPane.isReverseVerticalRotation()){							
				wheelRotation = wheelRotation * -1;
			}

			JList list = (JList) e.getSource();

			if(wheelRotation > 0){

				Rectangle preferedVisible = list.getVisibleRect();
				preferedVisible.setLocation(preferedVisible.x, (int) (preferedVisible.y + (double)Settings.getVerticalScrollModifier().getIntValue()/200 * preferedVisible.getHeight()));
				list.scrollRectToVisible(preferedVisible);

				return;
			}
			else if(wheelRotation < 0){	
				Rectangle preferedVisible = list.getVisibleRect();
				preferedVisible.setLocation(preferedVisible.x, (int) (preferedVisible.y - (double)Settings.getVerticalScrollModifier().getIntValue()/200 * preferedVisible.getHeight()));
				list.scrollRectToVisible(preferedVisible);

			}


		}


	}	

}


