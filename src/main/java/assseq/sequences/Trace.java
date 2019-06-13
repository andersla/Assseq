package assseq.sequences;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import assseq.utils.ArrayUtilities;

public class Trace {
	private static final Logger logger = Logger.getLogger(Trace.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	int[] backend;
	int NO_TRACE_VAL = 0;

	public Trace(int[] vals) {
		this.backend = vals;
	}

	public void insertAt(int n, int[] newVals) {
		
	//	logger.debug("newVals.length" + newVals.length);
	//	logger.debug("backend.length" + backend.length);
		
		assureSize(n - 1);
		int[] newArray = ArrayUtilities.insertAt(backend, n, newVals);
		
	//	logger.debug("newArray.length" + newArray.length);
		
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
	
	public int getMaxVal() {
		return ArrayUtilities.getMax(backend);
	}
	
	public void debug() {
		ArrayUtilities.debug(backend);
	}

	public void deletePos(int startPos, int endPos) {
		int[] newBackend = ArrayUtilities.deletePos(backend, startPos, endPos + 1);
		backend = newBackend;
	}

	public void reverse() {
		ArrayUtils.reverse(backend);
	}

	public void trim(int length) {
		logger.info("Trim trace current length:" + backend.length);
		backend = ArrayUtils.subarray(backend, 0, length);
		logger.info("Trim trace:" + length);
	}

}
