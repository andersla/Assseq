package aliview.sequences;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.utils.ArrayUtilities;


public class InMemoryTraceSequence extends BasicTraceSequence{
	private static final Logger logger = Logger.getLogger(InMemoryTraceSequence.class);

	public InMemoryTraceSequence(String name, String basesAsString) {
		this(name, basesAsString.getBytes());
	}

	public InMemoryTraceSequence(String name, byte[] bytes, byte[] qCalls, byte[] traceA, byte[] traceG, byte[] traceC, byte[] traceT) {
		super();
		// replace all . with -
		if(bytes != null){
			ArrayUtilities.replaceAll(bytes, (byte) '.', (byte) '-');
		}

		this.bases = new DefaultBases(bytes);
		this.qCalls = 
		this.name = name;

	}


	public void setBases(byte[] bytes) {
		logger.info("setnewbases");
		this.bases = new DefaultBases(bytes);
		translatedBases = null;
		if(selectionModel == null){
			createNewSelectionModel();
		}
	}
	
}
