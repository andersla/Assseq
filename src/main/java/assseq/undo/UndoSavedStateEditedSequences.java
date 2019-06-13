package assseq.undo;

import java.util.List;

import assseq.alignment.AlignmentMeta;
import assseq.sequences.Sequence;

public class UndoSavedStateEditedSequences extends UndoSavedState{
	public AlignmentMeta meta;
	public List<Sequence> editedSequences;

	public UndoSavedStateEditedSequences(List<Sequence> editedSequences,AlignmentMeta meta){
		this.editedSequences = editedSequences;
		this.meta = meta;
	}

}
