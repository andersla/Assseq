package assseq.sequences;

import java.io.IOException;
import java.io.Writer;

public interface QualCalledSequence extends Sequence {
	
	public short getQualValAtPos(int pos);
	
	public void setQualClipStart(int pos);
	
	public void setQualClipEnd(int pos);

	public boolean isQualClippedAtPos(int pos);
	
	public int getQualValAt(int x);
	
	public void writeQualityFastQ(Writer out) throws IOException;
	
	public void writeQuality(Writer out) throws IOException;
	
}
