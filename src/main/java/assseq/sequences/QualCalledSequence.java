package assseq.sequences;

import java.io.IOException;
import java.io.Writer;

public interface QualCalledSequence {
	
	public void writeQualityFastQ(Writer out) throws IOException;
	public void writeQuality(Writer out) throws IOException;
	
}
