package assseq.sequences;

import org.apache.log4j.Logger;

import assseq.importer.ClustalFileIndexer;
import assseq.sequencelist.MemoryMappedSequencesFile;

public class ClustalFileSequence extends PositionsToPointerFileSequence {

	public ClustalFileSequence(MemoryMappedSequencesFile sequencesFile, long startPointer) {
		super(sequencesFile, startPointer);
	}	
}
