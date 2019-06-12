package aliview.sequences;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;



public class ABISequence extends InMemoryTraceSequence{
	private static final Logger logger = Logger.getLogger(ABISequence.class);
	
	public ABISequence(String name, DefaultQualCalledBases bases, Traces traces) {
		super(name, bases, traces);
	}
}
