package assseq.importer;

import java.io.*;

import org.apache.log4j.Logger;

public class ACEparser {
	private static final Logger logger = Logger.getLogger(ACEparser.class);
	private Contig[] contigs; // Holds individual contigs
	private Read[] reads; // Holds the reads


	public static void main(String[] args) throws Exception {
		File alignmentFile = new File("/home/anders/projekt/alignments/ace/9documentsAssembly.ace");
		FileInputStream fileStream = new FileInputStream(alignmentFile);
		ACEparser parser = new ACEparser();
		parser.parsACE(fileStream);
		
		if(parser.contigs != null) {
			parser.contigs[0].debug();
		}
	}
	
	public ACEparser() {
	}


	//////////////////
	/// The parser ///
	//////////////////
	public void parsACE ( InputStream in ) throws IOException {
		String word = "";
		char mode = '0';
		boolean newline = true;
		int contigNo = 0;
		int readNo = 0;
		int baseNo = 0;
		int c;
		while ((c=in.read()) !=-1) {
			if (isWhitespace((char) c)) {
				if (!word.isEmpty()) {
					/// Identify ACE entry ///
					if (newline && word.equals("AS")) { mode = 'B'; logger.info("At start");}
					else if (newline && contigs !=null && word.equals("CO")) { mode = 'C'; contigs[contigNo] = new Contig(); readNo = 0;}
					else if (newline && contigs !=null && contigs[contigNo] != null && word.equals("BQ")) { mode = 'Q'; baseNo= 0; logger.info("Found q scores");}
					else if (newline && contigs !=null && contigs[contigNo] != null &&
							reads != null  && word.equals("AF")) { mode = 'A'; logger.info("Found read " + readNo);}
					else if (newline && reads != null && word.equals("RD")) { mode = 'R'; readNo = reads.length;}
					else if (newline && reads != null && word.equals("QA")) { mode = 'S'; }
					else if (newline && reads != null && word.equals("DS")) { mode = 'D'; }
					/// read data ///
					else if (mode == 'B') {
						contigs = new Contig [Integer.parseInt(word)];
						mode = 'b';
					}
					else if (mode == 'b') {
						reads = new Read [Integer.parseInt(word)];
						mode = '0';
					}
					else if (mode == 'C') {
						if (contigs[contigNo].name.isEmpty()) contigs[contigNo].name = word;
						else if (contigs[contigNo].qualities == null) contigs[contigNo].qualities = new int [Integer.parseInt(word)];
						else if (contigs[contigNo].reads == null) {
							int n = Integer.parseInt(word);
							contigs[contigNo].reads = new Read[n];
							contigs[contigNo].readsRevcomp = new boolean[n];
							contigs[contigNo].starts = new int [n];
						}
						else if (word.equals("C")) contigs[contigNo].revcomp = true;
						if (isNewlineChar ((char) c)) mode = 'c';
					}
					else if (mode == 'c') {
						contigs[contigNo].sequence += word;
					}
					else if (mode == 'Q') {
						if (baseNo < contigs[contigNo].qualities.length) contigs[contigNo].qualities[baseNo] = Integer.parseInt(word);
						++baseNo;
					}
					else if (mode == 'A') {
						if (contigs[contigNo].reads[readNo] == null) {
							for (int i = 0; i < this.reads.length; ++i) {
								if (this.reads[i] == null) {
									this.reads[i] = new Read(word);
									contigs[contigNo].reads[readNo]=this.reads[i];
									break;
								}
								else if (this.reads[i].name.equals(word)) {
									contigs[contigNo].reads[readNo]=this.reads[i];
									break;
								}
							}
							logger.info("Reading read: " + contigs[contigNo].reads[readNo].name);
						}
						else if (word.equals("C")) {
							contigs[contigNo].readsRevcomp[readNo] = true;
						}
						else if (word.equals("U")) {
							contigs[contigNo].readsRevcomp[readNo] = false;
						}
						else {
							contigs[contigNo].starts[readNo] = Integer.parseInt(word);
						}
						if (isNewlineChar ((char) c)) {mode = '0'; ++readNo;};
					}
					else if (mode == 'R') {
						if (readNo == this.reads.length) {
							for (int i =0; i < this.reads.length; ++i) {
								if (this.reads[i] == null) {
									this.reads[i] = new Read(word);
									readNo=i;
								}
								else if (this.reads[i].name.equals(word)) {
									readNo=i;
								}
							}
							if (readNo == this.reads.length) {
								logger.info("Could not find read " + word + ", and the given number of reads have been read.");
								return;
							}
						}
						if (isNewlineChar ((char) c)) { mode = 'r'; }
					}
					else if (mode == 'r') {
						if (readNo < this.reads.length) this.reads[readNo].sequence += word;
					}
					else if (mode == 'S') {
						if (readNo < this.reads.length) {
							if (this.reads[readNo].qualStart < 0) this.reads[readNo].qualStart = Integer.parseInt(word);
							else if (this.reads[readNo].qualEnd < 0) this.reads[readNo].qualEnd = Integer.parseInt(word);
							else if (this.reads[readNo].alignStart < 0) this.reads[readNo].alignStart = Integer.parseInt(word);
							else if (this.reads[readNo].alignEnd < 0) this.reads[readNo].alignEnd = Integer.parseInt(word);
						}
					}
					else if (mode == 'D') {
						if (!this.reads[readNo].description.isEmpty()) this.reads[readNo].description += " ";
						this.reads[readNo].description += word;
					}
					else mode = '0';
					word = "";
				}
				newline = isNewlineChar((char) c);
			}
			else { word+=(char) c; }
		}
	}
	///////////////////////////
	/// assisting functions ///
	///////////////////////////
	private boolean isWhitespace ( char c ) {
		if (c == ' ' || c == '\t' || c == '\n' || c == '\r') return true;
		return false;
	}
	private boolean isNewlineChar ( char c ) {
		if (c == '\n' || c == '\r') return true;
		return false;
	}
	private boolean testContig (int n ) {
		if (contigs != null && n < contigs.length) return true;
		else return false;
	}
	private boolean testContigRead( int n, int read) {
		if (testContig(n) && contigs[n].reads != null && read < contigs[n].reads.length && //
				contigs[n] != null && contigs[n].reads[read] != null) return true;
		else return false;
	}
	///////////////////////////
	/// interface functions ///
	///////////////////////////
	public int nContigs() {
		if (contigs != null) return contigs.length;
		else return 0;
	}
	public String getContigName( int n ) {
		if (testContig(n)) return contigs[n].name;
		else return "";
	}
	public String getContigSeq( int n ) {
		if(testContig(n)) return contigs[n].sequence;
		else return "";
	}
	
	public int[] getContigQual( int n ) {
		return contigs[n].qualities;
	}
	
	public int getNbasesInContig( int n) {
		if (testContig(n)) return contigs[n].qualities.length;
		else return 0;
	}
	public int getPaddedLengthContig( int n) {
		if (testContig(n)) return contigs[n].sequence.length();
		else return 0;
	}
	public char getPaddedBase (int n, int pos) {
		if (testContig(n) && pos < contigs[n].sequence.length()) return contigs[n].sequence.charAt(pos);
		else return '*';
	}
	public int getNreadsForContig (int n) {
		if (testContig(n)) return contigs[n].reads.length;
		else return 0;
	}
	public String getReadNameForContig (int n, int read) {
		if (testContigRead(n,read)) return contigs[n].reads[read].name;
		else return "";
	}
	public int getStartInContigForRead( int n, int read) {
		if (testContigRead(n,read) && contigs[n].starts != null && read < contigs[n].starts.length) return contigs[n].starts[read];
		else return 0;
	}
	public String getReadSeqForContig (int n, int read) {
		if (testContigRead(n,read)) return contigs[n].reads[read].sequence;
		else return "";
	}
	public int getReadQualStartForContig (int n, int read) {
		if (testContigRead(n,read)) return contigs[n].reads[read].qualStart;
		else return 0;
	}
	public int getReadQualEndForContig (int n, int read) {
		if (testContigRead(n,read)) return contigs[n].reads[read].qualEnd;
		else return 0;
	}
	
	public boolean getReadRevcompForContig (int n, int read) {
		return contigs[n].readsRevcomp[read];
	}
	
	public int getReadAlignStartForContig (int n, int read) {
		if (testContigRead(n,read)) return contigs[n].starts[read];
		else return 0;
	}
	public int getQualScore (int n, int pos) {
		if (testContig(n) && pos < contigs[n].qualities.length && contigs[n] != null) return contigs[n].qualities[pos];
		else return 0;
	}
	
	public String getDescriptionOfReadForContig(int n, int read) {
		if (testContigRead(n,read)) return contigs[n].reads[read].description;
		else return "";
	}
	
	public int getContigReadsMinLeftStart(int contigIndex){
		int min = Integer.MAX_VALUE;
		for(int readIndex = 0; readIndex < getNreadsForContig(contigIndex); readIndex++) {
			min = Math.min(min, getReadAlignStartForContig(contigIndex, readIndex));
		}
		return min;
	}

	////////////////////////////////////
	/// Private classes to hold data ///
	////////////////////////////////////
	private class Contig {
		private String name;
		private Read[] reads;
		private int [] starts;
		private boolean [] readsRevcomp;
		private boolean revcomp;
		private String sequence;
		private int[] qualities;
		Contig() {
			this.revcomp = false;
			this.sequence = "";
			this.name = "";
		}
		
		public void debug() {
			for(Read read: reads) {
				read.debug();
			}
		}
	}

	private class Read {
		private String name;
		private String sequence;
		private int qualStart;
		private int qualEnd;
		private int alignStart;
		private int alignEnd;
		private String description;
		Read() {
			name = "";
			sequence = "";
			qualStart = -1;
			qualEnd = -1;
			alignStart = -1;
			alignEnd = -1;
			description = "";
		}
		Read(String name) {
			this();
			this.name=name;
		}
		
		public void debug() {
			logger.info(name);
			logger.info(sequence);
		}
		
	}
}