package assseq.assembler;

import java.util.ArrayList;

public class AlignedQ {
    public class Base {
	char nuc;
	int qual;
	Base() {
	    nuc = 0;
	    qual = 0;
	}
	Base(char base, int score) {
	    nuc=base;
	    qual=score;
	}
    }
    private SequenceQ[] sequences;
    private int[][] positions;
    private boolean[] revcomp;
    //private String name;
    private int score;
    AlignedQ (AlignedQ[] seqs, int[][] pos, boolean[] rev, int score) {
	this.score =score;
	int n_seq = 0;
	for (int i=0; i < seqs.length; ++i) {
	    n_seq += seqs[i].nSeq();
	}
	//System.out.println("N seqs: " + n_seq);
	//System.err.println("Length contig = " + pos[0].length);
	this.sequences = new SequenceQ[n_seq];
	this.positions = new int[n_seq][pos[0].length];
	this.revcomp = new boolean[n_seq];
	n_seq=0; // for indexing
	for (int i=0; i < seqs.length; ++i) {
	    for (int j=0; j < seqs[i].sequences.length; ++j) {
		//System.err.println(seqs[i].sequences[j].getID());
		this.sequences[n_seq] = seqs[i].sequences[j];
		this.revcomp[n_seq] = (rev[i] && !seqs[i].revcomp[j]) || (!rev[i] && seqs[i].revcomp[j]);
		for (int k=0; k < pos[i].length; ++k) {
		    if (rev[i]) {
			if (pos[i][k] < 0) this.positions[n_seq][k] = pos[i][k];
			else this.positions[n_seq][k] = seqs[i].sequences[j].length()-1-seqs[i].positions[j][seqs[i].positions[j].length-1-pos[i][k]];
			//System.err.println(this.positions[n_seq][k]);
		    }
		    else {
			if (pos[i][k] < 0) this.positions[n_seq][k] = pos[i][k];
			else this.positions[n_seq][k] = seqs[i].positions[j][pos[i][k]];
    		    }
	    	}
		++n_seq;
	    }
	}
    }
    AlignedQ(SequenceQ one, boolean rev, int trimQual) {
	score = 0;
	sequences = new SequenceQ[1];
	sequences[0] = one;
	revcomp = new boolean[1];
	revcomp[0] = rev;
	int seqlength = one.length();
	int start=0;
	//System.err.println(seqlength);
	//System.err.println(one.getID());
	for (int i=0; i<one.length(); ++i) {
	    if (sequences[0].getQualScore(i, rev) > trimQual) {start=i; break;}
	    else {
	       	--seqlength;
		//System.err.println("Trimming start (" + sequences[0].getBase(i, rev) + "), qual: " + sequences[0].getQualScore(i, rev));
	    }
	}
	//System.err.println(one.getID());
	for (int i=one.length()-1; i>=0; --i) {
	    if (sequences[0].getQualScore(i, rev) > trimQual) break;
	    else {
	       	--seqlength;
		//System.err.println("Trimming end (" + sequences[0].getBase(i, rev) + "), qual: " + sequences[0].getQualScore(i, rev));
	    }
	}
	//System.err.println(seqlength + " " + start);
	positions = new int[1][seqlength];
	for (int i=0; i < seqlength; ++i) {
		positions[0][i]=i+start;
		//System.err.print(i+start + "(" +i + ":" + sequences[0].getQualScore(i+start, rev) + ")" + sequences[0].getBase(i+start, rev) + " ");
	}
	//System.err.println("");
	//System.err.println(getConSequence(rev));
    }
    AlignedQ(SequenceQ one, int trimQual) {
	this(one,false, trimQual);
    }
    AlignedQ(SequenceQ one) {
	this(one,false, 0);
    }
    int length() {
	return positions[0].length;
    }
    int nSeq() {
	return sequences.length;
    }
    int getHighScoreSeq(char nuc, int pos, boolean rev) {
	int highNuc = -1;
	int score = 0;
	for (int i=0; i < nSeq(); ++i) {
	    if (positions[i][pos] >= 0) {
		if (sequences[i].getBase(positions[i][pos],revcomp[i]) == nuc) {
		    if (sequences[i].getQualScore(positions[i][pos],revcomp[i]) > score) {
			score = sequences[i].getQualScore(pos,revcomp[i]);
			highNuc = i;
		    }
		}
	    }
	    else if (nuc == '*') {
		if (getGapScore(i,pos,rev) > score) {
		    if (getGapScore(i,pos,revcomp[i]) > score) {
			score = sequences[i].getQualScore(pos,revcomp[i]);
			highNuc = i;
		    }
		}
	    }
	}
	return highNuc;
    }
    String getConSequence() {
	return getConSequence(false);
    }
    String getConSequence (boolean rev) {
	String seq = "";
	for (int i =0; i < positions[0].length; ++i) {
	    Base temp = getConBase(i,rev);
	    seq += temp.nuc;
	}
	return seq;
    }
    int getStartInSeq(int n) {
	for (int i=0; i < positions[n].length; ++i) {
	    if (positions[n][i] >= 0) return positions[n][i];
	}
	return sequences[n].length();
    }
    int getPosInSeq(int n, int pos) {
	if (pos >= 0 && pos < positions[n].length) return positions[n][pos];
	else return -1;
    }
    int getEndInSeq(int n) {
	for (int i=positions[n].length-1; i>=0; --i) {
	    if (positions[n][i] >= 0) return positions[n][i];
	}
	return -1;
    }
    int getStartInAlignment(int n) {
	for (int i=0; i < positions[n].length; ++i) {
	    if (positions[n][i] >= 0) return i;
	}
	return length();
    }
    int getEndInAlignment(int n) {
	for (int i=positions[n].length-1; i>=0; --i) {
	    if (positions[n][i] >= 0) return i;
	}
	return -1;
    }
    int[] getFirsts () {
	int[] numb = new int[positions.length];
	for (int i = 0; i < positions.length; ++i) {
	    numb[i] = positions[i][0];
	}
	return numb;
    }
    Base getConBase (int pos) {
	return getConBase(pos, false);
    }
    Base getConBase (int pos, boolean revComp) {
	int G = 0;
	int C = 0;
	int T = 0;
	int A = 0;
	int gap = 0;
	char base;
	int qual = 0;
	if (revComp) pos = this.length()-1-pos;
	for (int i=0; i < positions.length; ++i) {
	    if (positions[i][pos] >= 0) {
		if (revcomp[i]) {
		    base = sequences[i].getRevCompBase(positions[i][pos]);
                    qual = sequences[i].getRevQualScore(positions[i][pos]);
                }
		else {
                    base = sequences[i].getBase(positions[i][pos]);
                    qual = sequences[i].getQualScore(positions[i][pos]);
                }
		if (revComp) { base = SequenceQ.compBase(base); }
		/*//System.err.print(positions[i][pos] + " ");
		if ((revcomp[i] && !comp) || (!revcomp[i] && comp)) { 
		    base = sequences[i].getRevCompBase(positions[i][pos]);
		    qual = sequences[i].getRevQualScore(positions[i][pos]);
		}
		else {
		    base = sequences[i].getBase(positions[i][pos]);
		    qual = sequences[i].getQualScore(positions[i][pos]);
		}*/
		
		if (base=='G' || base=='g') {
		    G+=qual;
		}
		else if (base=='C' || base=='c') {
		    C+=qual;
		}
		else if (base=='T' || base=='t') {
		    T+=qual;
		}
		else if (base=='A' || base=='a') {
		    A+=qual;
		}
		else {
		    System.err.print("Unknown char '" + base + "' at position " + positions[i][pos]);
		    if ((revcomp[i] && !revComp) || (!revcomp[i] && revComp)) System.err.print(" (revcomp)");
		    System.err.println(" in sequence " + sequences[i].getFileName());
		}
	    }
	    else {
		gap += getGapScore(i, pos, revComp);
	    }
	}
	if (gap >= G && gap >= C && gap >= T && gap >= A) {
	    //System.err.println('*');
	    return new Base('*', gap-G-T-A-C);
	}
    	else if (G >= C && G >=T && G >= A && G>gap) {
	    //System.err.println('G');
	    return new Base('G',G-T-A-C-gap);
	}
	else if (C >=T && C >= A && C > G && C > gap) {
	    //System.err.println('C');
	    return new Base('C',C-T-G-A-gap);
	}
	else if (T >=A && T > C && T > A && T > gap) {
	    //System.err.println('T');
	    return new Base('T',T-A-C-G-gap);
	}
	else {
	    //System.err.println('A');
	    return new Base('A',A-G-T-C-gap);
	}
    }
    char getBase (int seq, int pos) {
	if (positions[seq][pos] < 0) return '*';
	else return sequences[seq].getBase(positions[seq][pos],revcomp[seq]);
    }
    int getBaseQual (int seq, int pos) {
	if (positions[seq][pos] < 0) return 0;
	else return sequences[seq].getQualScore(positions[seq][pos],revcomp[seq]);
    }
    int getGapScore(int seq, int pos, boolean rev) {
	if (positions[seq][pos] >= 0) return 0;
	int score = 0;
	int pre = pos-1;
	int post = pos+1;
	while (pre>=0 && positions[seq][pre]<0) --pre;
	while (post < positions[seq].length && positions[seq][post] < 0) ++post; 
	if (pre > 0 && post < positions[seq].length-1) {
	    if ((revcomp[seq] && !rev) || (!revcomp[seq] && rev)) score = (sequences[seq].getRevQualScore(positions[seq][pre])+sequences[seq].getRevQualScore(positions[seq][post]))/2;
	    else score = (sequences[seq].getQualScore(positions[seq][pre])+sequences[seq].getQualScore(positions[seq][pre]))/2;
	}
	return score;
    }
    String getSubSequence( int seq, int start ) {
	return getSubSequence(seq, start, sequences[seq].length());
    }
    String getSubSequence( int seq, int start, int end ) {
	String sequence;
	if (revcomp[seq]) sequence = sequences[seq].getRevCompSeq();
       	else sequence = sequences[seq].getSeq();
	//System.err.println(sequence + " " + start + " " +end);
	return sequence.substring(start,end);
    }
    String getPaddedSequence(int seq) {
	int startInAlignment = getStartInAlignment(seq);
	String paddedseq = getSubSequence(seq,0,positions[seq][startInAlignment]);
	int endInAlignment = getEndInAlignment(seq);
	for (int i = startInAlignment; i < endInAlignment; ++i) {
	    paddedseq += getBase(seq,i);
	}
	paddedseq += getSubSequence(seq,positions[seq][endInAlignment]);
	return paddedseq;
    }
    int getPaddedLength(int seq) {
	int start = getStartInAlignment(seq);
	int end = getEndInAlignment(seq);
	return positions[seq][start] + end - start + sequences[seq].length() - positions[seq][end];
    }
    //String getName() { return name; }
    SequenceQ getSequence(int seq) {
	if (seq >= sequences.length) { return null; }
	return sequences[seq];
    }
    boolean isRevComp ( int seq ) {
	if (seq >= revcomp.length) { return false; }
	return revcomp[seq];
    }
    public boolean seqInAlignment(String name) {
	for (int i = 0; i < sequences.length; ++i) {
	    if (name.equals(sequences[i].getID())) return true;
	}
	return false;
    }
    public boolean overlappInSeqs(AlignedQ alignment) {
	for (int i = 0; i < sequences.length; ++i) {
	    if (alignment.seqInAlignment(sequences[i].getID())) return true;
	}
	return false;
    }
    public String[] getSequenceNames() {
	String[] names = new String[sequences.length];
	for (int i = 0; i < sequences.length; ++i) {
	    names[i] = this.sequences[i].getID();
	}
	return names;
    }
    public int getScore () { return this.score; }
    public int getNseq () { return sequences.length; }
    public static AlignedQ align ( AlignedQ sequenceOne, AlignedQ sequenceTwo) {
	/*System.err.println("Seq one");
	System.err.println(sequenceOne.getConSequence(false));
	System.err.println("rev");
	System.err.println(sequenceOne.getConSequence(true));
	System.err.println("Seq two");
	System.err.println(sequenceTwo.getConSequence(false));
	System.err.println("rev");
	System.err.println(sequenceTwo.getConSequence(true));*/
	class AlignmentScores {
	    int [][] scoreMatrix;
	    int [][] prevOne;
	    int [][] prevTwo;
	    int maxScore;
	    int endI;
	    int endJ;
	    AlignmentScores ( AlignedQ sequenceOne, AlignedQ sequenceTwo ) {
		scoreMatrix = new int [sequenceOne.length()][sequenceTwo.length()];
		prevOne = new int [sequenceOne.length()][sequenceTwo.length()];
		prevTwo = new int [sequenceOne.length()][sequenceTwo.length()];
		maxScore=0;
		endI=-1;
		endJ=-1;
	    }
	}
	AlignmentScores forScores = new AlignmentScores(sequenceOne,sequenceTwo);
	AlignmentScores revScores = new AlignmentScores(sequenceOne,sequenceTwo);
	//////////////////////////////////////////////////////////////////
	// get score for different alignments using dynamic programming //
	//////////////////////////////////////////////////////////////////
	for (int i=1; i < sequenceOne.length(); ++i) {
	    for (int j=1; j < sequenceTwo.length(); ++j) {
		int match = forScores.scoreMatrix[i-1][j-1];
		int gapJ = forScores.scoreMatrix[i][j-1];
		int gapI = forScores.scoreMatrix[i-1][j];
		int revMatch = revScores.scoreMatrix[i-1][j-1];
		int revGapJ = revScores.scoreMatrix[i][j-1];
		int revGapI = revScores.scoreMatrix[i-1][j];
		AlignedQ.Base oneBase = sequenceOne.getConBase(i); 
		AlignedQ.Base twoBase = sequenceTwo.getConBase(j);
		AlignedQ.Base twoRevBase = sequenceTwo.getConBase(j,true);
		if (oneBase.qual < 1) {
		    System.out.println("No qual score for first seq at pos " + i);
		}
		if (twoBase.qual < 1) {
		    System.out.println("No qual score for second seq at pos " + j);
		}
		if (twoRevBase.qual < 1) {
		    System.out.println("No qual score for second seq (revcomp) at pos " + j);
		}
		if (oneBase.nuc == twoBase.nuc) {
		    match += oneBase.qual + twoBase.qual;
		}
		else {
		    match -= (oneBase.qual + twoBase.qual);
		}
		if (oneBase.nuc == twoRevBase.nuc) {
		    revMatch += oneBase.qual + twoRevBase.qual;
		}
		else {
		    revMatch -= (oneBase.qual + twoRevBase.qual);
		}
		gapJ -= 2*twoBase.qual;
		gapI -= 2*oneBase.qual;
		revGapJ -= 2*twoRevBase.qual;
		revGapI -= 2*oneBase.qual;
		if (match >= gapJ && match >= gapI) {
		    forScores.scoreMatrix[i][j] = match;
		    forScores.prevOne[i][j] = i-1;
		    forScores.prevTwo[i][j] = j-1;
		}
		else if (gapJ > match && gapJ >= gapI) {
		    forScores.scoreMatrix[i][j] = gapJ;
		    forScores.prevOne[i][j] = i;
		    forScores.prevTwo[i][j] = j-1;
		}
		else if (gapI > match && gapI > gapJ) {
		    forScores.scoreMatrix[i][j] = gapI;
		    forScores.prevOne[i][j] = i-1;
		    forScores.prevTwo[i][j] = j;
		}
		else {
		    forScores.scoreMatrix[i][j] = 0;
		    forScores.prevOne[i][j] = -1;
		    forScores.prevTwo[i][j] = -1;
		}
		if (revMatch >= revGapJ && revMatch >= revGapI) {
		    revScores.scoreMatrix[i][j] = revMatch;
		    revScores.prevOne[i][j] = i-1;
		    revScores.prevTwo[i][j] = j-1;
		}
		else if (revGapJ > revMatch && revGapJ >= revGapI) {
		    revScores.scoreMatrix[i][j] = revGapJ;
		    revScores.prevOne[i][j] = i;
		    revScores.prevTwo[i][j] = j-1;
		}
		else if (revGapI > revMatch && revGapI > revGapJ) {
		    revScores.scoreMatrix[i][j] = revGapI;
		    revScores.prevOne[i][j] = i-1;
		    revScores.prevTwo[i][j] = j;
		}
		else {
		    revScores.scoreMatrix[i][j] = 0;
		    revScores.prevOne[i][j] = -1;
		    revScores.prevTwo[i][j] = -1;
		}
		//System.out.print(revScoreMatrix[i][j]+" ");
	    }
	    //System.out.println("");
	}
	////////////////////
	// Get max scores //
	////////////////////
	for (int i=1; i < sequenceOne.length(); ++i) {
	    for (int j=1; j < sequenceTwo.length(); ++j) {
		if (forScores.scoreMatrix[i][j] >= forScores.maxScore) {
		    forScores.maxScore = forScores.scoreMatrix[i][j];
		    forScores.endI = i;
		    forScores.endJ = j;
		}
		if (revScores.scoreMatrix[i][j] >= revScores.maxScore) {
		    revScores.maxScore = revScores.scoreMatrix[i][j];
		    revScores.endI = i;
		    revScores.endJ = j;
		}
	    }
	}
	///////////////////////////////////////////////////
	// Check if forward or reverse alignment is best //
	///////////////////////////////////////////////////
	boolean[] revcomp = {false,false};
	AlignmentScores bestScores;
	//System.err.println("For score: " + forScores.maxScore + " (" + forScores.endI + "," + forScores.endJ + ")");
	//System.err.println("Rev score: " + revScores.maxScore + " (" + revScores.endI + "," + revScores.endJ + ")");
	if (forScores.maxScore >= revScores.maxScore) {
	    bestScores = forScores;
	}
	else {
	    revcomp[1] = true;
	    bestScores = revScores;
	}
	/////////////////////////////
	// Get best alignment path //
	/////////////////////////////
	int prevI = -1;
    	int prevJ = -1;
	int startI = bestScores.endI;
	int startJ = bestScores.endJ;
	ArrayList<Integer[]> alignmentPath = new ArrayList<Integer[]>();
	while (startI >= 0 && startJ >= 0 && bestScores.scoreMatrix[startI][startJ] > 0) {
	    Integer [] temp = {startI, startJ, bestScores.scoreMatrix[startI][startJ]};
	    alignmentPath.add(temp);
	    prevI = startI;
	    prevJ = startJ;
	    startI = bestScores.prevOne[prevI][prevJ];
	    startJ = bestScores.prevTwo[prevI][prevJ];
	}
	////////////////////
	// Trim alignment //
	////////////////////
	int trimTo = 0;
	int trimFrom = 0;
	double cut_off = (alignmentPath.get(0)[2]-alignmentPath.get(alignmentPath.size()-1)[2])/(2.0*alignmentPath.size());
	for (int i=0; i < alignmentPath.size(); ++i) {
	    if (((alignmentPath.get(0)[2] - alignmentPath.get(i)[2])/ (i+1.0)) < cut_off) trimFrom = i;
	    if (trimTo == 0 && ((alignmentPath.get(i)[2] - alignmentPath.get(alignmentPath.size()-1)[2]) / (alignmentPath.size()-i*1.0)) < cut_off) trimTo = i;
	}
	if (trimTo-trimFrom > 10) {
	    while (alignmentPath.size()-1 > trimTo) { alignmentPath.remove(alignmentPath.size()-1); }
	    while (trimFrom > 0) { alignmentPath.remove(0); --trimFrom; }
	}
	int score = (alignmentPath.get(0)[2]-alignmentPath.get(alignmentPath.size()-1)[2]);
	/////////////////////////
	// Put contig together //
	/////////////////////////////////////
	// Add best seq after aligned part //
	/////////////////////////////////////
	//System.err.println("Seq one");
	//System.err.println(sequenceOne.getConSequence(revcomp[0]));
	//System.err.println("Seq Two");
	//System.err.println(sequenceTwo.getConSequence(revcomp[1]));
	ArrayList<Integer> posInI = new ArrayList<Integer>();
    	ArrayList<Integer> posInJ = new ArrayList<Integer>();
	int AccQualI = getWeightedQual(bestScores.endI,sequenceOne.length(),sequenceOne,revcomp[0]);
	int AccQualJ = getWeightedQual(bestScores.endJ,sequenceTwo.length(),sequenceTwo,revcomp[1]);
	if (AccQualI > 0 && AccQualI > AccQualJ) {
	    for (int pos = sequenceOne.length()-1; pos > alignmentPath.get(0)[0]; --pos) {
		AlignedQ.Base one = sequenceOne.getConBase(pos,revcomp[0]);
		if (one.nuc != '*') {
		    posInI.add(pos);
		    posInJ.add(-1);
		}
	    }
	}
	else if (AccQualJ > 0) {
	    for (int pos = sequenceTwo.length()-1; pos > alignmentPath.get(0)[1]; --pos) {
		AlignedQ.Base two = sequenceTwo.getConBase(pos,revcomp[1]);
		if (two.nuc != '*') {
		    posInJ.add(pos);
		    posInI.add(-1);
		}
	    }
	}
	//////////////////////
	// Add aligned part //
	//////////////////////
	//System.err.println("Alignment length: " + aliLength);
	prevI = -1;
    	prevJ = -1;
	for (Integer [] pos : alignmentPath) {
	    //System.err.println(pos[0] + " " + pos[1] + " " + pos[2]);
	    //if (pos[2] > prevScore + minVal) { System.err.println("Ending on: " + (prevScore + minVal)); break; }
	    //else if (pos[2] < prevScore) { prevScore = pos[2]; }
	    if ( pos[0]== prevI) {
		posInI.set(posInI.size()-1, -1);
	    }
	    posInI.add(pos[0]);
	    if (pos[1] == prevJ) {
		posInJ.set(posInJ.size()-1, -1);
	    }
	    posInJ.add(pos[1]);
	    prevI = pos[0];
	    prevJ = pos[1];
	}
	/////////////////////////////////////////////////
	// Add best sequence to beginning of alignment //
	/////////////////////////////////////////////////
	AccQualI = getWeightedQual(prevI-1,0,sequenceOne,revcomp[0]);
	AccQualJ = getWeightedQual(prevJ-1,0,sequenceTwo,revcomp[1]);
	if (AccQualI > 0 && AccQualI > AccQualJ) {
	    for (int pos = prevI-1; pos >= 0; --pos) {
		AlignedQ.Base one = sequenceOne.getConBase(pos,revcomp[0]);
		if (one.nuc != '*') {
		    posInI.add(pos);
		    posInJ.add(-1);
		}
		//System.err.println(pos + "(" + one.nuc + "): " + posInI.get(posInI.size()-1) + " " + posInJ.get(posInJ.size()-1));
	    }
	}
	else if (AccQualJ > 0) {
	    for (int pos = prevJ-1; pos >=0; --pos) {
		AlignedQ.Base two = sequenceTwo.getConBase(pos,revcomp[1]);
		if (two.nuc != '*') {
		    posInJ.add(pos);
		    posInI.add(-1);
		}
		//System.err.println(pos + "(" + two.nuc + ":" + two.qual + "): " + posInI.get(posInI.size()-1) + " " + posInJ.get(posInJ.size()-1));
	    }
	}
	//////////////////////////////
	// prepare return alignment //
	//////////////////////////////
	//System.out.println("sizes: " + posInI.size() + " " + posInJ.size());
	AlignedQ alignment;
	AlignedQ[] pair = {sequenceOne,sequenceTwo};
     	int[][] pos = new int[2][posInI.size()];
	for (int i = 0; i < posInI.size(); ++i) {
	    //System.out.println(i + ": " + sequenceOne.getPosInSeq(0,posInI.get(posInI.size()-1-i)) + " " + sequenceTwo.getPosInSeq(0,posInJ.get(posInJ.size()-1-i)));
	    pos[0][i] = posInI.get(posInI.size()-1-i);
	    pos[1][i] = posInJ.get(posInJ.size()-1-i);
	}
	return new AlignedQ(pair,pos,revcomp,score);
    }
    static private int getWeightedQual(int start, int end, AlignedQ seq, boolean revcomp) {
	int accQual = 0;
	int mod = 1;
	int finish;
	if (end < start) {
	    mod = -1;
	    if (end == 0 && start > 0) {
		AlignedQ.Base base = seq.getConBase(0,revcomp);
		accQual += base.qual/start;
	    }
	}
	for (int a=1; a+start*mod < end*mod; ++a) {
    	    AlignedQ.Base base = seq.getConBase(start+(mod*a),revcomp);
	    accQual += base.qual/a;
	}
	return accQual;
    }
}

