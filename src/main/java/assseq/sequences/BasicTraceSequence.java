package assseq.sequences;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import assseq.AminoAcid;
import assseq.Assseq;
import assseq.NucleotideUtilities;
import assseq.sequencelist.AlignmentListModel;
import assseq.sequencelist.Interval;
import assseq.utils.ArrayUtilities;

// todo can save memory by changing data implementation into byte instead of char
public class BasicTraceSequence extends BasicQualCalledSequence implements TraceSequence{
	private static final Logger logger = Logger.getLogger(BasicTraceSequence.class);
	protected Traces traces;


	public BasicTraceSequence(){
		this.id = SequenceUtils.createID();
		selectionModel = new DefaultSequenceSelectionModel();
	}

	public BasicTraceSequence(DefaultQualCalledBases bases, Traces traces) {
		this();
		this.bases = bases;
		this.traces = traces;
	}

	public BasicTraceSequence(BasicTraceSequence template) {
		this.name = template.name;
		this.id = template.id;
		this.bases = template.getNonTranslatedBases().getCopy();
		this.alignmentModel = template.alignmentModel;
		this.selectionModel = createNewSelectionModel();
	}

	public Sequence getCopy() {
		return new BasicTraceSequence(this);
	}

	public void insertGapAt(int n){
		getBases().insertAt(n, SequenceUtils.GAP_SYMBOL);
		getTraces().insertAt(n, 1);
		// do the same with selmodel
		selectionModel.insertNewPosAt(n);
	}

	public Traces getTraces() {
		return traces;
	}

	public void deleteBase(int index){	
		logger.debug("Delete in basicSeq (index):" + index);
		getBases().delete(index);
		getTraces().delete(index);
		selectionModel.removePosition(index);
	}


	public void rightPadSequence(int finalLength, byte symbol) {

		int addCount = finalLength - getBases().getLength();
		if(addCount > 0){
			byte[] additional = new byte[addCount];
			Arrays.fill(additional, symbol);
			getBases().append(additional);
		}

		// insert into traces
		logger.info("RightPad trace " + addCount);
		
		if(getTraces() != null) {
			getTraces().append(addCount);
		}
		
	}


	public void leftPadSequence(int finalLength, byte symbol) {

		int addCount = finalLength - getBases().getLength();
		if(addCount > 0){
			byte[] additional = new byte[addCount];
			Arrays.fill(additional, symbol);
			getBases().insertAt(0,additional);
		}

		// insert into traces
		if(addCount > 0){
			if(getTraces() != null) {
				getTraces().insertAt(0, addCount);
			}
		}

	}

	public void deleteBasesFromMask(boolean[] mask){
		logger.debug("delete from mask");
		int nTruePos = ArrayUtilities.count(mask, true);

		int[] toDelete = new int[nTruePos];

		int deleteCount = 0;
		for(int n = 0; n < getBases().getLength() && n < mask.length ; n++){
			if(mask[n] == true){
				toDelete[deleteCount] = n;
				deleteCount ++;
			}
		}

		getBases().delete(toDelete);
		getTraces().delete(toDelete);

		// and do same for sel-model
		for(int n = mask.length-1; n >= 0; n--){
			if(mask[n] == true){
				selectionModel.removePosition(n);
			}
		}
	}




	public void trimTraces() {
		getTraces().trim();
	}




	public void complement() {
		getBases().complement();
		Traces traces = getTraces();
		if(traces != null) {
			traces.complement();
		}
	}

	public void reverse(){
		getBases().reverse();
		Traces traces = getTraces();
		if(traces != null) {
			traces.reverse();
		}
	}

}
