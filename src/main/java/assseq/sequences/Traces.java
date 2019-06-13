package assseq.sequences;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import assseq.utils.ArrayUtilities;

public class Traces {
	private static final Logger logger = Logger.getLogger(Traces.class);

	int INSERTED_BASECALL = 0;

	private Trace traceA;
	private Trace traceG;
	private Trace traceC;
	private Trace traceT;
	private int[] baseCalls;

	//private int lengthInBases;
	//private int oneBaseLength;
	//private int baseCalledTraceLength;

	public Traces(Trace traceA, Trace traceG, Trace traceC, Trace traceT, int[] baseCalls) {
		super();
		this.traceA = traceA;
		this.traceG = traceG;
		this.traceC = traceC;
		this.traceT = traceT;
		this.baseCalls = baseCalls;
		//this.baseCalledTraceLength = baseCalls[baseCalls.length - 1];
		//this.oneBaseLength = baseCalledTraceLength/baseCalls.length;
		// Add 1/2 baseLength at end to get real baseCalledTrace
		//this.oneBaseLength = (int)(baseCalledTraceLength + (oneBaseLength*0.5)) / baseCalls.length;
		logger.debug(Arrays.toString(baseCalls));
		logger.debug(Arrays.toString(traceA.backend));

	}

	public int[] getTraceAVals(int basePos){
		return getTraceVals(traceA, basePos);
	}

	public int[] getTraceGVals(int basePos){
		return getTraceVals(traceG, basePos);
	}

	public int[] getTraceTVals(int basePos){
		return getTraceVals(traceT, basePos);
	}

	public int[] getTraceCVals(int basePos){
		return getTraceVals(traceC, basePos);
	}

	// TODO fix this so it can take more than one pos at a time?
	public int[] getTraceVals(Trace trace, int basePos){
		
		int startPos = getTraceStartPos(basePos);
		int endPos = getTraceEndPos(basePos);

		int [] traceVals = ArrayUtils.subarray(trace.backend, startPos, endPos);
		return traceVals;
	}

	private int getTraceStartPos(int basePos) {

		if(basePos > baseCalls.length) {
			logger.error("basePos > baseCalls.length - This should not happen!!");
			logger.error("basePos=" + basePos);
			logger.error(" baseCalls.length=" +  baseCalls.length);
			return 0;
		}
		
		int baseCallVal = baseCalls[basePos];

		// If basePos is first let last = 0
		int startPos;
		if(basePos == 0) {
			startPos = 0;
		}
		else {
			// Set start pos to be in the middle between previous and this call
			int prevCallVal = baseCalls[basePos - 1];
			startPos = prevCallVal + (baseCallVal - prevCallVal)/2;
		}
		return startPos;

	}

	// returns end of trace for this pos (Inclusive)
	private int getTraceEndPos(int basePos) {

		if(basePos > baseCalls.length) {
			logger.error("basePos > baseCalls.length - This should not happen!!");
			logger.error("basePos=" + basePos);
			logger.error(" baseCalls.length=" +  baseCalls.length);
			return 0;
		}
		
		int baseCallVal = baseCalls[basePos];

		// If basePos is last pos, let next be last
		int endPos;
		if(basePos == baseCalls.length -1) {
			endPos = basePos;
		}
		else {
			// Set end pos to be in the middle between this and next call
			int nextCallVal = baseCalls[basePos + 1];
			endPos = baseCallVal + (nextCallVal - baseCallVal)/2;
			// add one to endpos to make it connect to first point of next pos when line is drawn
			endPos += 1;
		}

		return endPos;

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

	private void assureSize(int n) {
		if(n >= baseCalls.length){
			resize(n + 1);
		}
	}
	
	private void resize(int n) {
		logger.info("resize=" + n);
		int additionalCount = n - baseCalls.length;
		int[] additional = new int[additionalCount];
		Arrays.fill(additional, INSERTED_BASECALL);
		baseCalls = ArrayUtils.addAll(baseCalls, additional);
	}
	
	private int getOneBaseLength() {
		int oneBaseLength = getBaseCalledTraceLength()/baseCalls.length;
		return oneBaseLength;
	}

	private int getBaseCalledTraceLength() {
		return baseCalls[baseCalls.length - 1];
	}
	
	public void append() {
		insertAt(baseCalls.length -1);
	}

	public void insertAt(int basePos) {
		
		int startPos = getTraceStartPos(basePos);
		
		int[] newTracePiece = new int[getOneBaseLength()];
		traceA.insertAt(startPos, newTracePiece);
		traceC.insertAt(startPos, newTracePiece);
		traceG.insertAt(startPos, newTracePiece);
		traceT.insertAt(startPos, newTracePiece);
		
		int newCallPos = (int) (startPos + 0.5 * getOneBaseLength());
		int[] newCalls = new int[] {newCallPos};
		
		assureSize(basePos - 1);
		int[] newArray = ArrayUtilities.insertAt(baseCalls, basePos, newCalls);
		
		// offset basecalls after inserted position with the inserted baseLength
		ArrayUtilities.addToArrayValues(newArray, getOneBaseLength(), basePos + 1);
		
		
		baseCalls = newArray;
	}
	
	public void delete(int[] toDelete) {
		Arrays.sort(toDelete);
		if(toDelete == null || toDelete.length == 0){
			return;
		}
		
		for(int onePos: toDelete) {
			delete(onePos);
		}
	}

	public void delete(int posToDelete) {
		
		logger.debug("Delete in trace (posToDelete):" + posToDelete);
		
		// Find out positions this baseCall has in traces
		int startPos = getTraceStartPos(posToDelete);
		int endPos = getTraceEndPos(posToDelete);
		
		// Delete pos from traces
		traceA.deletePos(startPos, endPos);
		traceC.deletePos(startPos, endPos);
		traceG.deletePos(startPos, endPos);
		traceT.deletePos(startPos, endPos);
		
		
		// delete from basecalls
		int[] newBaseCalls = ArrayUtilities.deletePos(baseCalls, posToDelete);
		
		// adjust basecalls after the position removed
		
		// Add one because endPos is inclusive (not exclusive)
		int valToSubtract = endPos - startPos + 1;
	    ArrayUtilities.subtractFromArrayValues(newBaseCalls, valToSubtract, posToDelete);
		baseCalls = newBaseCalls;
		
	}

	public void complement() {
		Trace previousTraceA = traceA;
		traceA = traceT;
		traceT = previousTraceA;
		
		Trace previousTraceC = traceC;
		traceC = traceG;
		traceG = previousTraceC;
		
		// Nothing needs to be done with basecalls
		
	}

	public void reverse() {
		traceA.reverse();
		traceC.reverse();
		traceT.reverse();
		traceG.reverse();

		ArrayUtils.reverse(baseCalls);
		
		int traceLength = traceA.backend.length;
		for(int n = 0; n < baseCalls.length; n++) {
			baseCalls[n] = traceLength - baseCalls[n];
		}
	}
	
	public void trim() {
		int oneBaseLength = getOneBaseLength();
		int maxBaseCallPos = baseCalls[baseCalls.length - 1];	
		traceA.trim(maxBaseCallPos + oneBaseLength/2);
		traceC.trim(maxBaseCallPos + oneBaseLength/2);
		traceG.trim(maxBaseCallPos + oneBaseLength/2);
		traceT.trim(maxBaseCallPos + oneBaseLength/2);
		
		
	}

}
