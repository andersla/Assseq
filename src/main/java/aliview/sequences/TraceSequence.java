package aliview.sequences;

public interface TraceSequence extends Sequence {
	
	public short getQualValAtPos(int pos);
	
	public Traces getTraces();
	
	public void trimTraces();
	
	public void setQualClipStart(int pos);
	
	public void setQualClipEnd(int pos);

	public boolean isQualClippedAtPos(int pos);
	
	public int getQualValAt(int x);
	
}
