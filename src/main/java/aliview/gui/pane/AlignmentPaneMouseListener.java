package aliview.gui.pane;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.apache.log4j.Logger;

import aliview.AssseqWindow;
import aliview.alignment.Alignment;
import aliview.sequencelist.SequenceJList;
import aliview.settings.Settings;
import aliview.utils.Utils;
import utils.OSNativeUtils;

public class AlignmentPaneMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final Logger logger = Logger.getLogger(AlignmentPaneMouseListener.class);
	private Point startPoint;
	private Point startPointScreen;
	private Point startPaneVisibleRectLocation;
	private Point dragPointStart;
	private boolean isDragging;
	private Rectangle lastRect;
	private Rectangle maxRepaintRect;
	private AlignmentPane alignmentPane;
	private SequenceJList sequenceJList;
	private Alignment alignment;
	private AssseqWindow aliViewWindow;

	public AlignmentPaneMouseListener(Alignment alignment, AlignmentPane alignmentPane, SequenceJList sequenceJList) {
		super();
		this.alignment = alignment;
		this.alignmentPane = alignmentPane;
		this.sequenceJList = sequenceJList;
	}

	/*
			// Chech if no ctrl modifier - then clear previous selection
			if(! e.isControlDown()){
				logger.info("modifiers" + e.getModifiers());
				alignmentPane.clearSelection();
				alignmentList.clearSelection();
			}

			try {
				alignmentPane.selectBaseAt(mousePos);
			} catch (InvalidAlignmentPositionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 */

	public void mousePressed(MouseEvent e) {

		alignmentPane.requestFocus();

		// Skip right click
		if(e.getButton() == e.BUTTON3){
			return;
		}

		logger.info("mouse pressed" + e.getClickCount());

		// Save some startpoints
		startPoint = e.getPoint();
		startPointScreen = e.getLocationOnScreen();
		startPaneVisibleRectLocation = alignmentPane.getVisibleRect().getLocation();
		lastRect = new Rectangle(e.getPoint());

		logger.info("points done");

		// if click is within an existing selection
		// we should think about drag possibility
		if(alignmentPane.isWithinExistingSelection(e.getPoint())){
			dragPointStart = e.getPoint();
		}else{



			// if shift is down something is selected already make a new rect selection
			if(e.isShiftDown()){

				// this is done in mouse released instead
			}
			// new single point selection
			else{
				// clear list selection
				sequenceJList.clearSelection();
				alignmentPane.requestFocus();

				alignment.clearSelection();
				sequenceJList.clearSelection();

				try {
					alignmentPane.selectBaseAt(startPoint);
					//			statusPanel.setPointerPos(startPoint);
				} catch (InvalidAlignmentPositionException e1) {
					//nothing needs to be done
					e1.printStackTrace();
				}
			}
			// requestPaneRepaint();
		}
	}

	public void mouseReleased(MouseEvent e){

		logger.info("mouseReleased e.getPoint=" + e.getPoint());

		// Skip right click
		if(e.getButton() == e.BUTTON3){
			return;
		}

		//	logger.info("mouse released" + e.getClickCount());

		if(startPoint == null){
			logger.info("nostartpos");

		}else{
			// if startpoint is same as release-point select by point
			if(alignmentPane.paneCoordToMatrixCoord(e.getPoint()).distance(alignmentPane.paneCoordToMatrixCoord(startPoint)) == 0){				
				// if shift is down something is selected already make a new rect selection
				if(e.isShiftDown()){	

					//						// set default first pos and override if cursor is selected
					//						Point firstPos = alignment.getFirstSelectedPosition();
					//						// if there is a cursor-pos and it is selected then use it instead of
					//						if(aliCursor != null){
					//							if(alignment.isBaseSelected(aliCursor.x, aliCursor.y)){
					//								firstPos = new Point(aliCursor.x, aliCursor.y);
					//							}
					//						}
					Point clickPoint = alignmentPane.paneCoordToMatrixCoord(e.getPoint());
					Rectangle clickRect = new Rectangle(clickPoint);
					logger.info(clickRect);
					Rectangle currentSelection = alignment.getSelectionAsMinRect();
					logger.info(currentSelection);
					Rectangle newSelection = Utils.addRects(clickRect, currentSelection);
					logger.info(newSelection);


					// clear before new selection - this to avoid non-rectangle selections
					alignment.clearSelection();
					alignment.setSelectionWithin(newSelection);
					//int selectionSize = alignmentPane.selectWithin(newSelection);
				}

				else{
					logger.info("mouse released");
					// clear selection
					alignment.clearSelection();
					alignment.clearTempSelection();
					// if click is on ruler, all should get select
					if(e.getComponent() == alignmentPane.getRulerComponent()){
						alignmentPane.selectColumnAt(startPoint);
						// cursor have to change
						int x = alignmentPane.getColumnAt(e.getPoint());
						aliViewWindow.getAliCursor().setPosition(x,0);
					}else{
						try {
							alignmentPane.selectBaseAt(startPoint);
						} catch (InvalidAlignmentPositionException e1) {
							// nothing needs to be done
							e1.printStackTrace();
						}
					}
				}
			}else if(isDragging){
				isDragging = false;
				alignment.clearSelectionOffset();
				dragPointStart = null;
				// else select by rectangle
			}else{
				logger.info("select Within");
				Rectangle selectRect = new Rectangle(e.getPoint());
				selectRect.add(startPoint);


				logger.info("selectRect" + selectRect);

				if(e.isControlDown()){
					//	alignmentPane.addSelectionWithin(selectRect);
				}
				else{
					int selectionSize = 0;
					alignment.clearSelection();
					if(e.getComponent() == alignmentPane.getRulerComponent()){
						selectionSize = alignmentPane.selectColumnsWithin(selectRect);
					}else{
						selectionSize = alignmentPane.selectWithin(selectRect);
					}
					logger.info(alignment.getSelectionAsMinRect());
				} 
				alignment.clearTempSelection();
			}


			// Clear stuff when released
			startPoint = null;
			startPointScreen = null;
			isDragging = false;
			alignment.clearSelectionOffset();
			dragPointStart = null;
		}
		maxRepaintRect = null;
		//sequenceJList.validateSelection();
		//requestPaneRepaint();
		//		logger.info(e.getPoint());

		// new cursor-pos if shift is not pressed
		if(! e.isShiftDown()){
			Point clickPos = alignmentPane.paneCoordToMatrixCoord(e.getPoint());
			logger.info(clickPos);
			aliViewWindow.getAliCursor().setPosition(clickPos.x, clickPos.y);
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		logger.info("mouse clicked" + e.getClickCount());

		// Skip right click
		if(e.getButton() == e.BUTTON3){
			return;
		}

		if(e.getClickCount() == 2){
			Point matrixCoord = alignmentPane.paneCoordToMatrixCoord(e.getPoint());
			logger.info(matrixCoord);
			alignment.selectEverythingWithinGaps(matrixCoord);
		}

		/*
			if(e.getButton() == e.BUTTON3){
				logger.info("right-click");

				try {
					alignmentPane.setDifferenceTraceSequence(e.getPoint());
				} catch (InvalidAlignmentPositionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				alignmentPane.repaint();
			}
		 */

	}


	public void mouseDragged(MouseEvent e) {
		//		logger.info("mouse dragged start");
		// Theese lines makes sure pane is scrolling when user selects
		// and moves outside current visible rect (keyword scroll speed)
		Rectangle preferredVisisble = new Rectangle(e.getPoint());

		// scroll if pointer outside of scrollpane window
		if(! alignmentPane.getVisibleRect().contains(e.getPoint())){	
			// grow little extra so it scrolls quickly in beginning
			preferredVisisble.grow(30,30);
			alignmentPane.scrollRectToVisible(preferredVisisble);
		}

		//statusPanel.setPointerPos(e.getPoint());

		if(startPoint != null){


			// Dragging bases
			if(dragPointStart != null){

				if(isDragging != true){
					isDragging = true;
					aliViewWindow.getUndoControler().pushUndoState();
				}

				Rectangle selectRect = alignment.getSelectionAsMinRect();
				Rectangle selectInPaneCoord = alignmentPane.matrixCoordToPaneCoord(selectRect);
				//if(selectInPaneCoord.contains(e.getPoint())){		
				int diff = e.getPoint().x - dragPointStart.x;
				double diffInSeqPositons = diff/alignmentPane.getCharWidth();
				int intDiffInseqPos = (int)diffInSeqPositons;

				// Test first if move is possible - otherwise many false requestedit if not possible
				if(alignment.isMoveSelectionRightPossible() || alignment.isMoveSelectionLeftPossible()){
					if(aliViewWindow.requestEditMode()){

						//if(e.getPoint().x >= selectInPaneCoord.x && e.getPoint().x <= selectInPaneCoord.getMaxX()){
						alignment.moveSelection(intDiffInseqPos, aliViewWindow.isUndoable());
						//}

					}	
				}

				selectInPaneCoord.grow(4 + 3 * Math.abs(diff),0);
				//requestPaneRepaintRect(selectInPaneCoord); 

			}
			// Selecting
			else{
				Rectangle selectRect = new Rectangle(e.getPoint());
				selectRect.add(startPoint);

				Rectangle selectRectMatrixCoords = alignmentPane.paneCoordToMatrixCoord(selectRect);
				alignment.setTempSelection(selectRectMatrixCoords);


				if(maxRepaintRect == null){	
					maxRepaintRect = new Rectangle(selectRect);
				}else{
					maxRepaintRect.add(selectRect);
				}

				//		requestPaneRepaintRect(new Rectangle(maxRepaintRect));

			}

			// sequenceJList.validateSelection();

		}	

		//		logger.info("mouse dragged done");
	}

	public void mouseMoved(MouseEvent e) {
		//			int ungapedPos = alignmentPane.getUngapedSequenceXPositionAt(e.getPoint());
		//			// add one because of program internaly works with pos 0 as the first
		//			lblSelectionInfo.setText("" + (ungapedPos + 1) + " (ungaped position) ");

	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		// Zoom in out if ctrl is pressed
		if(e.getModifiersEx() ==  OSNativeUtils.getMouseWheelZoomModifierMask()){
			if(e.getWheelRotation() > 0){
				aliViewWindow.zoomOutAt(e.getPoint());
			}
			else if(e.getWheelRotation() < 0){		
				aliViewWindow.zoomInAt(e.getPoint());
			}
		}
		// Else scroll pane left or right
		else if(e.isShiftDown()){
			int wheelRotation = e.getWheelRotation();
			if(aliViewWindow.isReverseHorizontalRotation()){
				wheelRotation = wheelRotation * -1;
			}
			if(wheelRotation > 0){
				Rectangle preferedVisible = alignmentPane.getVisibleRect();  
				preferedVisible.setLocation((int) (preferedVisible.x - (double)Settings.getHorizontalScrollModifier().getIntValue()/200 * preferedVisible.getWidth()), preferedVisible.y);
				alignmentPane.scrollRectToVisible(preferedVisible);
				alignmentPane.revalidate();
				// break to avoid diagonal moves
				return;
			}
			else if(wheelRotation < 0){	
				Rectangle preferedVisible = alignmentPane.getVisibleRect();
				preferedVisible.setLocation((int) (preferedVisible.x + (double)Settings.getHorizontalScrollModifier().getIntValue()/200 * preferedVisible.getWidth()), preferedVisible.y);
				alignmentPane.scrollRectToVisible(preferedVisible);
				alignmentPane.revalidate();
				// break to avoid diagonal moves
				return;
			}
			// Else scroll pane up or down				
		}else{
			int wheelRotation = e.getWheelRotation();
			if(aliViewWindow.isReverseVerticalRotation()){
				wheelRotation = wheelRotation * -1;
			}
			if(wheelRotation > 0){
				Rectangle preferedVisible = alignmentPane.getVisibleRect();
				preferedVisible.setLocation(preferedVisible.x, (int) (preferedVisible.y + (double)Settings.getVerticalScrollModifier().getIntValue()/200 * preferedVisible.getHeight()));
				alignmentPane.scrollRectToVisible(preferedVisible);
				alignmentPane.revalidate();
				// break to avoid diagonal moves
				return;
			}
			else if(wheelRotation < 0){	
				Rectangle preferedVisible = alignmentPane.getVisibleRect();
				preferedVisible.setLocation(preferedVisible.x, (int) (preferedVisible.y - (double)Settings.getVerticalScrollModifier().getIntValue()/200 * preferedVisible.getHeight()));
				alignmentPane.scrollRectToVisible(preferedVisible);
				alignmentPane.revalidate();
				// break to avoid diagonal moves
				return;

			}
		}
	}
}	

