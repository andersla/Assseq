package aliview.sequences;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import aliview.utils.ArrayUtilities;

public class Trace {
	private static final Logger logger = Logger.getLogger(Trace.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	int[] backend;
	int NO_TRACE_VAL = 0;

	public Trace(int[] vals) {
		this.backend = vals;
	}

	public void insertAt(int n, int[] newVals) {
		assureSize(n - 1);
		int[] newArray = ArrayUtilities.insertAt(backend, n, newVals);
		backend = newArray;
	}
	
	private void assureSize(int n) {
		if(n >= backend.length){
			resize(n + 1);
		}
	}
	
	private void resize(int n) {
		logger.info("resize=" + n);
		int additionalCount = n - backend.length;
		int[] additional = new int[additionalCount];
		Arrays.fill(additional, NO_TRACE_VAL);
		backend = ArrayUtils.addAll(backend, additional);
		logger.info("backend.length=" + backend.length);
	}
	
	public void debug() {
		ArrayUtilities.debug(backend);
	}
}
