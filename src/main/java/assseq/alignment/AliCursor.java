package assseq.alignment;

import org.apache.log4j.Logger;

import assseq.sequences.Sequence;

public class AliCursor{
	int x;
	int y;
	Sequence cursorSeq;
	int posInSeq;
	private Alignment alignment;
	private static final Logger logger = Logger.getLogger(AliCursor.class);

	public AliCursor(int x, int y, Alignment alignment) {
		this.alignment = alignment;
		setPosition(x, y);
	}

	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
		savePosition();
	}

	public void restorePosition(){
		if(cursorSeq != null){
			int xFromSeq = cursorSeq.getPosOfSelectedIndex(posInSeq);
			int yFromSeq = alignment.getSequenceIndex(cursorSeq);
			if(alignment.isPositionValid(xFromSeq ,yFromSeq)){
				x = xFromSeq;
				y = yFromSeq;
			}
		}
	}

	public void savePosition(){
		if(alignment != null && alignment.getSequences() != null){
			if(alignment.isPositionValid(x, y)){
				logger.info("savepos");
				this.cursorSeq = alignment.getSequences().get(y);
				this.posInSeq = (int) cursorSeq.countSelectedPositions(0, x);
			}
		}
	}

	public void moveLeft(boolean isShiftDown){
		restorePosition();
		if(alignment.isPositionValid(x-1,y)){
			if(isShiftDown){
				// if moving into selection deselect previous
				if(alignment.getSelectionAt(x-1, y) == true){
					alignment.clearColumnSelection(x);
				}
				// if moving into clear then select and leave previous
				else{
					alignment.copySelectionFromPosX1toX2(x,x-1);
				}		
			}else{
				alignment.setSelectionAt(x-1, y, true);
			}
			x--;

		}
		savePosition();


	}

	public void moveRight(boolean isShiftDown){
		restorePosition();
		if(alignment.isPositionValid(x+1,y)){
			if(isShiftDown){
				// if moving into selection deselect previous
				if(alignment.getSelectionAt(x+1, y) == true){					
					alignment.clearColumnSelection(x);
				}
				// if moving into clear then select and leave previous
				else{
					alignment.copySelectionFromPosX1toX2(x, x+1);
				}				
			}else{
				alignment.setSelectionAt(x+1, y, true);	
			}		
			x++;
		}
		savePosition();
	}

	public void moveUp(boolean isShiftDown){
		restorePosition();
		if(alignment.isPositionValid(x,y-1)){
			if(isShiftDown){
				// if moving into selection deselect previous
				if(alignment.getSelectionAt(x, y-1) == true){
					alignment.setAllHorizontalSelectionAt(y, false);
				}
				// if moving into clear then select and leave previous
				else{
					alignment.copySelectionFromSequenceTo(y, y-1);
				}		
			}else{
				alignment.setSelectionAt(x, y-1, true);
			}
			y--;
		}
		savePosition();
	}
	public void moveDown(boolean isShiftDown){
		restorePosition();
		if(alignment.isPositionValid(x,y+1)){
			if(isShiftDown){
				// if moving into selection deselect previous
				if(alignment.getSelectionAt(x, y+1) == true){
					alignment.setAllHorizontalSelectionAt(y, false);
					// if moving into clear then select and leave previous
				}else{
					alignment.copySelectionFromSequenceTo(y,y+1);
				}	
			}else{
				alignment.setSelectionAt(x, y+1, true);
			}
			y++;
		}
		savePosition();
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}