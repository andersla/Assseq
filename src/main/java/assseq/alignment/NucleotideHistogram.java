package assseq.alignment;

import assseq.AminoAcid;
import assseq.NucleotideUtilities;
import assseq.sequences.Sequence;

public class NucleotideHistogram extends AliHistogram{

	public NucleotideHistogram(int length) {
		super(length);
	}

	public void addSequence(Sequence seq){	
		for(int pos = 0; pos < seq.getLength(); pos++){
			hist[pos][NucleotideUtilities.baseValFromBase(seq.getBaseAtPos(pos))] ++;	
		}
	}

	public double getSumNonGap(int x){
		int sum = 0;
		// add all
		for(int n = 0; n < hist[x].length; n++){
			sum += hist[x][n];
		}
		// and remove gaps
		sum -= hist[x][NucleotideUtilities.GAP];
		return sum;
	}

}



