package assseq.assembler;

import java.util.ArrayList;
import java.io.*;

public class SequenceQ {
    private String ID;
    private String seq;
    private String fileName;
    private int[] qual;
    private int scoreMod;
    SequenceQ () {
	ID = "";
	seq = "";
	fileName = "";
	qual = null;
	scoreMod = 33;
    }
    SequenceQ (InputStream in, String name) throws Exception {
	this();
	fileName = name;
	readFromInstream(in);
    }
    SequenceQ ( String name ) throws Exception {
	this();
	this.fileName = name;
	this.scoreMod = 0;
	readPHD1FromFile( name );
    }
    SequenceQ (InputStream in, String name, char mode) throws Exception {
	this();
	fileName = name;
	readFastQFromInstream(in, mode);
    }
    private void readFromInstream (InputStream in) throws Exception {
	readFastQFromInstream(in, 'b');
    }
    private void readFastQFromInstream (InputStream in, char mode) throws Exception {
	boolean newline = false;
	int c;
	int pos = 0;
	while ((c=in.read()) !=-1) {
    	    if ((char) c == '\n' || (char) c == '\r') { //newline has meaning
		if (!newline) { // if first newline char
		    if (mode == 'i') {
			mode = 's';
		    }
		    else if (mode == '+') {
			mode = 'q';
			qual = new int[pos];
			pos = 0;
		    }
		    else if (mode == 'q') {
			break;
		    }
		    else if (mode == 's') {
			mode = '+';
		    }
		}
		newline = true;
	    }
	    else {
		newline = false; // not reading newline chars
		if ((char) c == '@' && mode == 'b') { // start of ID
		    mode = 'i';
		}
		else if (mode == 'i') { // read ID
		    ID+=(char) c;
		}
		else if ((char) c == ' ' || (char) c == '\t') {
		    //ignore whitespace characters that are not in ID
		}
		else if (mode == 'q' && pos < seq.length()) {
		    qual[pos] = c;
		    ++pos;
		    //addQualScore(c);
		}
		else if (mode == 's') {
		    addBase((char) c);
		    ++pos;
		}
	    }
	}
    }
    private void readPHD1FromFile ( String fileName ) throws Exception {
	int c;
	int pos=0;
	String word = "";
	char mode = 'b';
	int word_on_row = 0;
	
	InputStream fr = new FileInputStream(fileName);
	while ((c=fr.read()) !=-1) {
	    if ((char) c != ' ' && (char) c != '\t' && (char) c != '\n' && (char) c != '\r') {
		word += (char) c;
	    }
	    else {
		    //System.err.print(word + " ");
		    if (word.length() > 0) {
		    if (word.equals("END_DNA")) { mode = 'e'; }
		    else if (word.equals("BEGIN_DNA")) {
			//System.err.print("Read seq");
			mode = 's';
		    }
		    else if (word.equals("BEGIN_SEQUENCE")) { mode = 'n'; }
		    else if (mode == 's') {
			if (word_on_row == 0) {
			    addBase(word.charAt(0));
			}
			else if (word_on_row == 1) {
			    addQualScore( Integer.parseInt(word) );
			}
		    }
		    else if(mode == 'n') {
			this.ID = word;
			mode = 'b';
		    }
		}
		if ((char) c == '\n' || (char) c == '\r') {
		    word_on_row = 0;
		    //System.err.println("");
		}
		else 
		    ++word_on_row;
		word = "";
	    }
	}
    }
    int test () {
	if (seq.length() != qual.length) {
	    return 1;
	}
	else { return 0; }
    }
    static public char compBase ( char base ) {
    	if (base == 'a') { return 't'; }
	else if (base == 'A') { return 'T'; }
	else if (base == 't') { return 'a'; }
	else if (base == 'T') { return 'A'; }
	else if (base == 'c') { return 'g'; }
	else if (base == 'C') { return 'G'; }
	else if (base == 'g') { return 'c'; }
	else if (base == 'G') { return 'C'; }
	else { return 'N'; }
    }
    /*
    void setSeq ( String sequence ) { seq = sequence; }
    void setID ( String name ) { seq = name; }
    void setQualScores (ArrayList<Integer> scores) { qual = scores; }
    */
    private void addBase (char base) { seq += base; }
    private void addQualScore (int score) { 
	int[] temp;
	int i=0;
	if (qual != null) {
	    temp = new int[qual.length+1];
	    while (i < qual.length) {
		temp[i] = qual[i];
		++i;
	    }
	}
	else { temp = new int[1]; }
	temp[i] = score;
	qual = temp;
    }
    private void addToID (char letter) { ID += letter; }
    //void setScoreMod (int mod) { scoreMod = mod; }
    // get methods
    String getFileName() { return fileName; }
    String getID () { return ID; }
    String getSeq () { return seq; }
    String getRevCompSeq () {
	String revComp = "";
	for (int i=seq.length()-1; i>=0; --i) {
	    revComp += compBase(seq.charAt(i));
	}
	return revComp;
    }
    char getBase (int pos, boolean revComp) {
	if (revComp) return getRevCompBase(pos);
	else return getBase(pos);
    }
    char getBase (int pos) {
	if (seq.length() > pos) return seq.charAt(pos);
	else return '*';
    }
    char getRevCompBase(int pos) {
	if (seq.length() > pos) return compBase(seq.charAt(seq.length()-pos-1));
	else return '*';
    }
    int getQualScore( int pos, boolean revComp) {
	if (revComp) return getRevQualScore(pos);
	else return getQualScore(pos);
    }
    int getQualScore( int pos ) {
	if (pos < qual.length) return qual[pos]-scoreMod;
	else return 0;
    }
    int getRevQualScore( int pos ) {
	if (pos < qual.length) return (qual[seq.length()-pos-1]-scoreMod);
	else return 0;
    }
    int getUnModQualScore( int pos ) { return qual[pos]; }
    int getUnModRevQualScore( int pos ) { return (qual[seq.length()-pos-1]); }
    int length () {
	if (seq != null) { return seq.length();}
	else return 0;
    }
    
}
