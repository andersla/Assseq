package assseq.sequences;

import org.apache.log4j.Logger;

import assseq.importer.ClustalFileIndexer;
import assseq.sequencelist.MemoryMappedSequencesFile;

public class PositionsToPointerFileSequence extends FileSequence {
	private static final Logger logger = Logger.getLogger(PositionsToPointerFileSequence.class);

	public PositionsToPointerFileSequence(MemoryMappedSequencesFile sequencesFile,long startPointer){
		super(new PositionsToPointerFileSequenceBases(sequencesFile, startPointer));
	}

	public void add(PositionToPointer posToPoint){
		PositionsToPointerFileSequenceBases fileSeqBases = (PositionsToPointerFileSequenceBases) getNonTranslatedBases();
		fileSeqBases.add(posToPoint);
	}
}
