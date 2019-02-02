package aliview.sequences;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;



public class ABISequence extends InMemoryTraceSequence{
	private static final Logger logger = Logger.getLogger(ABISequence.class);
	
	public ABISequence(String name, byte[] basecalls, byte[] qCalls, byte[] traceA, byte[] traceG, byte[] traceC, byte[] traceT){
		super(name, basecalls, qCalls, traceA, traceG, traceC, traceT);
	}

	public ABISequence(String name, String basesAsString){
		super(name, basesAsString);
	}

}
