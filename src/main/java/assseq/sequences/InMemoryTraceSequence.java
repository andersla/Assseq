package assseq.sequences;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import assseq.utils.ArrayUtilities;


public class InMemoryTraceSequence extends BasicTraceSequence{
	private static final Logger logger = Logger.getLogger(InMemoryTraceSequence.class);

	public InMemoryTraceSequence(String name, DefaultQualCalledBases bases, Traces traces) {
		super(bases, traces);
		this.name = name;
	}
	
}
