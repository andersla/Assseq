package assseq.undo;

import java.util.List;

import assseq.AssseqWindow;
import assseq.alignment.AlignmentMeta;
import assseq.sequencelist.AlignmentListModel;
import assseq.sequences.Sequence;

public class UndoSavedStateEverything extends UndoSavedState{

	public String fastaAlignment;
	public AlignmentMeta meta;
	public AlignmentListModel sequences;

	public UndoSavedStateEverything(String fastaAlignment,AlignmentMeta meta){
		this.fastaAlignment = fastaAlignment;
		this.meta = meta;
	}

	public UndoSavedStateEverything(AlignmentListModel copy, AlignmentMeta meta) {
		this.sequences = copy;
		this.meta = meta;
	}
}
