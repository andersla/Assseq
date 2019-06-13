package assseq.sequences;

import org.apache.log4j.Logger;

import assseq.importer.ClustalFileIndexer;
import assseq.sequencelist.MemoryMappedSequencesFile;

public class MSFFileSequence extends PositionsToPointerFileSequence {

	public MSFFileSequence(MemoryMappedSequencesFile sequencesFile, long startPointer) {
		super(sequencesFile, startPointer);
	}	
}
