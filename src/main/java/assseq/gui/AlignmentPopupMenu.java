package assseq.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import assseq.AssseqWindow;


public class AlignmentPopupMenu extends JPopupMenu implements MouseListener {

	private AssseqWindow aliViewWin;
	private AliViewJMenuBar aliViewMenuBar;
	private MouseEvent mouseActivationEvent;

	public AlignmentPopupMenu(final AssseqWindow aliViewWin, final AliViewJMenuBar aliViewMenuBar){
		this.aliViewWin = aliViewWin;
		this.aliViewMenuBar = aliViewMenuBar;

		JMenuItem setTraceSeq = new JMenuItem("Set this sequence as template when Highlighting difference");
		setTraceSeq.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				aliViewWin.setDifferenceTraceSequence(mouseActivationEvent.getPoint());
			}
		});
		add(setTraceSeq);

		add(new JSeparator());

		JMenuItem copyItem = new JMenuItem("Copy (as fasta)");
		copyItem.setModel(aliViewMenuBar.getCopyAsFastaButtonModel());
		add(copyItem);

		JMenuItem copyNucItem = new JMenuItem("Copy (the characters only)");		
		copyNucItem.setModel(aliViewMenuBar.getCopyAsCharactersButtonModel());
		add(copyNucItem);

		JMenuItem copyNameItem = new JMenuItem("Copy name(s) only");		
		copyNameItem.setModel(aliViewMenuBar.getCopyNameButtonModel());
		add(copyNameItem);

		add(new JSeparator());

        JMenuItem renameItem = new JMenuItem("Rename sequence");
		renameItem.setModel(aliViewMenuBar.getRenameButtonModel());
		add(renameItem);

		add(new JSeparator());
		
		JMenuItem deleteItem = new JMenuItem("Delete selected sequence(s)");
		deleteItem.setModel(aliViewMenuBar.getDeleteSequencesButtonModel());
		add(deleteItem);
		
		add(new JSeparator());


	}

	/*
	 * 
	 * The mouse listener
	 * 
	 */

	public void mousePressed(MouseEvent e){
		if (e.isPopupTrigger())
			showPopupMenu(e);
	}

	public void mouseReleased(MouseEvent e){
		if (e.isPopupTrigger())
			showPopupMenu(e);
	}

	private void showPopupMenu(MouseEvent e){
		mouseActivationEvent = e;
		this.show(e.getComponent(), e.getX(), e.getY());
	}


	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}


	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub		
	}


	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}


