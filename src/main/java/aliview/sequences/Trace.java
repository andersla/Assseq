package aliview.sequences;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import aliview.utils.ArrayUtilities;

public class Trace {
	private static final Logger logger = Logger.getLogger(Trace.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	byte[] backend;

	public Trace(byte[] bytes) {
		this.backend = bytes;
	}

	public void insertAt(int n, byte[] newBytes) {
		assureSize(n - 1);
		byte[] newArray = ArrayUtilities.insertAt(backend, n, newBytes);
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
		byte[] additional = new byte[additionalCount];
		Arrays.fill(additional, SequenceUtils.GAP_SYMBOL);
		backend = ArrayUtils.addAll(backend, additional);
		logger.info("backend.length=" + backend.length);
	}
	
}
