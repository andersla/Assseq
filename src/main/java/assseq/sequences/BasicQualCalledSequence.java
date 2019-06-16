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
public class BasicQualCalledSequence extends BasicSequence implements QualCalledSequence{
	private static final Logger logger = Logger.getLogger(BasicQualCalledSequence.class);


	public BasicQualCalledSequence(){
		this.id = SequenceUtils.createID();
		selectionModel = new DefaultSequenceSelectionModel();
	}

	public BasicQualCalledSequence(DefaultQualCalledBases bases) {
		this();
		this.bases = bases;
	}

	public BasicQualCalledSequence(BasicQualCalledSequence template) {
		this.name = template.name;
		this.id = template.id;
		this.bases = template.getNonTranslatedBases().getCopy();
		this.alignmentModel = template.alignmentModel;
		this.selectionModel = createNewSelectionModel();
	}

	public Sequence getCopy() {
		return new BasicQualCalledSequence(this);
	}

	protected DefaultQualCalledBases getBases(){
		return (DefaultQualCalledBases) this.bases;
	}

	public void rightPadSequenceWithNoData(int finalLength) {
		rightPadSequence(finalLength, SequenceUtils.NO_DATA);
	}
	
	public void setQualClipStart(int pos) {
		getBases().setQualClipStart(pos);
	}

	public void setQualClipEnd(int pos) {
		getBases().setQualClipEnd(pos);
	}

	public boolean isQualClippedAtPos(int pos) {
		return getBases().isQualClipped(pos);
	}

	public void writeQualityFastQ(Writer out) throws IOException{
		out.write(qualCallsAsFastQString());
	}
	
	public void writeQuality(Writer out) throws IOException{
		short[] qualCalls = getBases().getQualCalls();
		for(int n = 0; n < qualCalls.length;  n++) {	
			out.write( "" + qualCalls[n] + " ");
		}
	}
	
	public String qualCallsAsString() {
		short[] qualCalls = getBases().getQualCalls();
		StringBuilder builder = new StringBuilder();
		for(int n = 0; n < qualCalls.length;  n++) {	
			builder.append( qualCalls[n] + " ");
		}
		
		return builder.toString();
	}
	
	public String qualCallsAsFastQString() {
		//String FastQQuals = "#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
		short[] qualCalls = getBases().getQualCalls();
		ArrayUtilities.addToArrayValuesShort(qualCalls, (short)33);
	
		StringWriter buffer = new StringWriter();
		for(int n = 0; n < qualCalls.length;  n++) {	
			buffer.append( (char) qualCalls[n] );
		}
		
		return buffer.toString();
	}
	

	public int[] qualCallsAsIntArray() {
		short[] qualCalls = getBases().getQualCalls();
		return ArrayUtilities.shortArray2IntArray(qualCalls);
	}
	
	public int getQualValAt(int x) {
		return getBases().getQualCall(x);
	}
	
	public short getQualValAtPos(int seqXPos) {
		return getBases().getQualCall(seqXPos);
	}
}
