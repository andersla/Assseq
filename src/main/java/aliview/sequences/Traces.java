package aliview.sequences;

import org.apache.log4j.Logger;

public class Traces {
	private static final Logger logger = Logger.getLogger(Traces.class);
	
	private Trace traceA;
	private Trace traceG;
	private Trace traceC;
	private Trace traceT;
	
	public Traces(Trace traceA, Trace traceG, Trace traceC, Trace traceT) {
		super();
		this.traceA = traceA;
		this.traceG = traceG;
		this.traceC = traceC;
		this.traceT = traceT;
	}
	
	public Trace getTraceA() {
		return traceA;
	}
	public void setTraceA(Trace traceA) {
		this.traceA = traceA;
	}
	public Trace getTraceG() {
		return traceG;
	}
	public void setTraceG(Trace traceG) {
		this.traceG = traceG;
	}
	public Trace getTraceC() {
		return traceC;
	}
	public void setTraceC(Trace traceC) {
		this.traceC = traceC;
	}
	public Trace getTraceT() {
		return traceT;
	}
	public void setTraceT(Trace traceT) {
		this.traceT = traceT;
	}

	public void insertAt(int n, byte[] newBytes) {
		traceA.insertAt(n, newBytes);
		traceG.insertAt(n, newBytes);
		traceC.insertAt(n, newBytes);
		traceT.insertAt(n, newBytes);
	}
	
	// convenience
	public void insertAt(int n, byte newByte) {
		insertAt(n, new byte[]{newByte});
	}
}
