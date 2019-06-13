package assseq.undo;

import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;

import assseq.alignment.AlignmentMeta;
import assseq.sequences.Sequence;


public class UndoableReorder extends AbstractUndoableEdit{

	private List<Sequence> sequenceList;
	private AlignmentMeta meta;

	public UndoableReorder(List<Sequence> sequenceList, AlignmentMeta meta){
		this.sequenceList = sequenceList;
		this.meta = meta;
	}

	public String getPresentationName() { return "Reorder sequences"; }

}
