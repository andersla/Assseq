package assseq.assembler;

import java.io.*;

public class MSAQ {
    private static int[] getSeqNo ( int pair, int length) {
	int pos = 0;
	int[] a = {-1,-1};
        for (int i=0; i <= length; ++i) {
            for (int j=i+1; j < length; ++j) {
		if (pair == pos) { a[0] = i; a[1] = j; return a; }
		++pos;
	    }
	}
	return a;
    }
    private static int getPairNo (int one, int two) {
	int pos = -1;
        for (int i=0; i <= one; ++i) {
            for (int j=i+1; j <= two ; ++j) {
                ++pos;
            }
        }
	return pos;
    }
    public static AlignedQ[] align( SequenceQ [] sequences, int trim_qual ) {
	//////////////////////////////////////////////////////////////////////////////////
	// Make all pairwise alignment and store the order from highest to lowest score //
	//////////////////////////////////////////////////////////////////////////////////
	if (sequences == null) {
	    System.err.println("No sequences given to make contig");
	    return null;
	}
	int n_pairs = getPairNo(sequences.length-2,sequences.length-1)+1;
	//System.err.println(n_pairs);	
     	AlignedQ[] pairs = new AlignedQ[n_pairs];
	int pos = 0;
	int [] scoreOrder = new int[pairs.length];
	for (int i=0; i < scoreOrder.length; ++i) { scoreOrder[i] = -1; }
	scoreOrder[0] = 0;
 	for (int i=0; i < sequences.length; ++i) {
	    for (int j=i+1; j < sequences.length; ++j) {
		//System.err.println(i + " " + j);
		if (sequences[i] != null && sequences[j] != null) {
		    pairs[pos] = AlignedQ.align(new AlignedQ(sequences[i],trim_qual), new AlignedQ(sequences[j],trim_qual));
		    for (int k = pos; k > 0; --k) {
			if (pairs[scoreOrder[k-1]].getScore() > pairs[pos].getScore()) { scoreOrder[k] = pos; }
			else {
			    scoreOrder[k] = scoreOrder[k-1];
			    scoreOrder[k-1] = pos;
			}
		    }
		    ++pos;
		}
	    }
	}
	//for (int i=0; i < scoreOrder.length; ++i) { System.err.println("Order: " + scoreOrder[i]); }
	//////////////////////////////////////////////
	// Build contig in order of pairwise scores //
	//////////////////////////////////////////////
	AlignedQ[] return_alignments = null;
	int[][] included_seq = null;
	for (int i=0; i < scoreOrder.length; ++i) {
	    if (scoreOrder[i] >= 0) {
		System.err.println("Processing pairwise alignment: " + (scoreOrder[i]+1));
		if (return_alignments == null) { // if first alignment
		    //System.err.println("Add first alignment: " + scoreOrder[i]);
		    return_alignments = new AlignedQ[1];
                    return_alignments[0] = pairs[scoreOrder[i]];
		    included_seq = new int[1][];
		    included_seq[0] = getSeqNo(scoreOrder[i],sequences.length);
		    //System.err.println("Including sequences: " + included_seq[0][0] + " " + included_seq[0][1]);
		}
		else {
		    int[] inAliToAdd = getSeqNo(scoreOrder[i],sequences.length);
		    //System.err.println("Checking sequences: " + inAliToAdd[0] + " " + inAliToAdd[1]);
		    int[] inAli = {-1,-1};
		    boolean flag = true;
		    for (int ali=0; ali < included_seq.length; ++ali) {
			for (int seqs =0; seqs < included_seq[ali].length; ++seqs) {
			    if (inAliToAdd[0] == included_seq[ali][seqs]) {
				inAli[0] = ali;
			    }
			    if (inAliToAdd[1] == included_seq[ali][seqs]) {
                                inAli[1] = ali;
                            }
			}
		    }
		    //System.err.println("Sequence " + inAliToAdd[0] + " was found in " + inAli[0] + ", and " + inAliToAdd[1] + " found in " + inAli[1]);
		    if (inAli[0] == inAli[1]) {
			//System.err.println("Both seqs already in: " + inAli[1]);
			continue;
		    }
		    if (inAli[0] < 0 && inAli[1] < 0) {
			// if no overlapp in sequences with previous contigs, add as new alignment
			//System.err.println("Add another alignment: " + scoreOrder[i]);
			AlignedQ[] temp = new AlignedQ[return_alignments.length+1];
			for (int j = 0; j < return_alignments.length; ++j) {
                            temp[j] = return_alignments[j];
                        }
			temp[return_alignments.length] = pairs[scoreOrder[i]];
			return_alignments = temp;
			int [][] tempSeqs = new int[included_seq.length+1][];
			for (int j = 0; j < included_seq.length; ++j) {
			    tempSeqs[j] = included_seq[j];
			}
			tempSeqs[included_seq.length] = getSeqNo(scoreOrder[i],sequences.length);
			included_seq = tempSeqs;
		    }
		    else if (inAli[0] >= 0 && inAli[1] >= 0) {
			// if sequences in different alignments merge alignments
			//System.err.println("Mearge alignment: " + inAli[0] + " and " + inAli[1]);
			//AlignQpair temp = new AlignQpair(return_alignments[inAli[0]],return_alignments[inAli[1]]);
			AlignedQ newAli = AlignedQ.align(return_alignments[inAli[0]],return_alignments[inAli[1]]);
			int[] new_included_seq = new int[included_seq[inAli[0]].length+included_seq[inAli[1]].length];
			System.arraycopy(included_seq[inAli[0]], 0, new_included_seq, 0, included_seq[inAli[0]].length);
			System.arraycopy(included_seq[inAli[1]],0, new_included_seq, included_seq[inAli[0]].length, included_seq[inAli[0]].length);
			// store new alignment and get rid of old
			AlignedQ[] tempAlis = new AlignedQ[return_alignments.length-1];
			int [][] tempSeqs = new int[included_seq.length-1][];
			int newPos = 0;
			for (int j = 0; j < return_alignments.length; ++j) {
			    if (j==inAli[0]) {
				tempAlis[newPos] = newAli;
				tempSeqs[newPos] = new_included_seq;
				++newPos;
			    }
                            else if (j != inAli[1]) {
				tempAlis[newPos] = return_alignments[j];
				tempSeqs[newPos] = included_seq[j];
				++newPos;
			    }
                        }
		    }
		    else {
			//add one sequence to alignments
			int includeIn = -1;
			int includeSeq = -1;
			if(inAli[0] >= 0) { includeIn = inAli[0]; includeSeq = inAliToAdd[1]; }
			else { includeIn = inAli[1]; includeSeq = inAliToAdd[0]; }
			//System.err.println("Add sequence " + includeSeq + " to alignment " + includeIn);
			//AlignQpair temp = new AlignQpair(return_alignments[includeIn],new AlignedQ(sequences[includeSeq],trim_qual));
			return_alignments[includeIn] = AlignedQ.align(return_alignments[includeIn],new AlignedQ(sequences[includeSeq],trim_qual));
			int [] tempSeqs = new int [included_seq[includeIn].length+1];
			System.arraycopy(included_seq[includeIn],0,tempSeqs,0,included_seq[includeIn].length);
			//System.err.println("Temp: " + tempSeqs.length + " " + included_seq[includeIn].length + " In Ali: " + inAliToAdd.length + " " + includeSeq);
			tempSeqs[included_seq[includeIn].length] = /*inAliToAdd[*/includeSeq/*]*/;
			included_seq[includeIn] = tempSeqs;
		    }
		}
	    }
       	}
	System.err.println("Number of sequences in final alignment: " + return_alignments[0].getNseq());
	return return_alignments;
    }
}
