package assseq.sequences;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import assseq.utils.ArrayUtilities;


public class InMemorySequence extends BasicSequence{
	private static final Logger logger = Logger.getLogger(InMemorySequence.class);

	public InMemorySequence(String name, String basesAsString) {
		this(name, basesAsString.getBytes());
	}

	public InMemorySequence(String name, byte[] bytes) {
		super();
		// replace all . with -
		if(bytes != null){
			ArrayUtilities.replaceAll(bytes, (byte) '.', (byte) '-');
		}

		this.bases = new DefaultBases(bytes);
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
