package assseq.undo;

import java.util.List;

import assseq.alignment.AlignmentMeta;
import assseq.sequencelist.AlignmentListModel;
import assseq.sequences.Sequence;

public class UndoSavedStateSequenceOrder extends UndoSavedState{
	public AlignmentMeta meta;
	public List<Sequence> sequencesBackend;

	public UndoSavedStateSequenceOrder(List<Sequence> sequencesBackendCopy,AlignmentMeta meta){
		this.sequencesBackend = sequencesBackendCopy;
		this.meta = meta;
	}
}
