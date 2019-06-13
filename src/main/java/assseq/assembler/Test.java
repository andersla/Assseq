package assseq.assembler;

import java.io.*;
import java.util.*;
import java.util.ArrayList;


public class Test {
    private static void help () {
	System.out.println("ASSSeq assemble assembels sequences given in FastQ or PHD.1 format.");
	System.out.println();
	System.out.println("--batch / -b will assemble contigs from different batches of sequences separately");
	System.out.println("--files / -f give batch of sequence files, each as separate argument");
	System.out.println("--format / -o give ouput format as ACE, FASTA, or FASTQ");
	System.out.println("--help / -h print this help");
	System.out.println("--lowestalignmentscore / -a lowest score for reporting contig, default 100");
	System.out.println("--trimqual / -q trim edges to given qual value, default 15");
	System.out.println();
    }
    public static void main (String[] args) throws Exception {
	int lowestAlScore = 100;
	//int lowestDip = 75;
	int trim_qual = 15;
	String output_format = "ACE";
	int n_seq = 0;
	ArrayList<ArrayList<String>> inputFiles = new ArrayList<ArrayList<String>>();
	boolean batch = false;
	////////////////////
	// read arguments //
	////////////////////
	for (int i=0; i < args.length; ++i ) {
	    //System.err.println(args[i]);
	    if (args[i].equals("--format") || args[i].equals("-o")) {
		if (i+1 < args.length && args[++i].charAt(0) != '-') {
		    if (args[i].toUpperCase().equals("ACE")) {
			output_format = "ACE";
		    }
		    else if (args[i].toUpperCase().equals("FASTA")) {
			output_format = "FASTA";
		    }
		    else if (args[i].toUpperCase().equals("FASTQ")) {
                        output_format = "FASTQ";
                    }
		    else if (args[i].toUpperCase().equals("MFASTA")) {
                        output_format = "MFASTA";
                    }
		    else if (args[i].toUpperCase().equals("MFASTQ")) {
                        output_format = "MFASTQ";
                    }
		    else {
			System.err.println(args[i] + " is not a supported output file format.");
			System.exit(1);
		    }
		}
		else {
		    System.err.println("--format / -o require a supported file format as additional argument. ");
		    System.exit(1);
		}
	    }
	    else if (args[i].equals("--batch") || args[i].equals("-b")) {
		batch = true;
	    }
	    else if (args[i].equals("--trimqual") || args[i].equals("-q")) {
		if (i+1 < args.length && args[++i].charAt(0) != '-') {
		    trim_qual = Integer.parseInt(args[i]);
		}
		else {
		    System.err.println("--trimqual / -q require an integer as next argument.");
		    System.exit(1);
		}
	    }
	    else if (args[i].equals("--lowestalignmentscore") || args[i].equals("-a")) {
                if (i+1 < args.length && args[++i].charAt(0) != '-') {
                    lowestAlScore = Integer.parseInt(args[i]);
                }
                else {
                    System.err.println("--lowestalignmentscore / -a require an integer as next argument.");
                    System.exit(1);
                }
            }
	    else if (args[i].equals("--files") || args[i].equals("-f")) {
		ArrayList<String> files = new ArrayList<String>();
		while (i+1 < args.length && args[i+1].charAt(0) != '-') {
		    ++i;
		    files.add(args[i]);
		}
		inputFiles.add(files);
	    }
	    else if (args[i].equals("--help") || args[i].equals("-h")) {
		help();
		System.exit(0);
	    }
	    else if (i == args.length -1) {
		inputFiles.add(new ArrayList<String>(Arrays.asList(args[i])));
	    }
	    else {
		System.err.println("Unrecognized argument '" + args[i] + "'.");
		System.exit(1);
	    }
	}
	//////////////////////////////////////////////
	// Read sequences and order them in batches //
	//////////////////////////////////////////////
	AlignedQ[] contigs = null;
	SequenceQ[][] sequenceBatches;
       	if (batch) sequenceBatches = new SequenceQ[inputFiles.size()][];
	else sequenceBatches = new SequenceQ[1][];
	for (int i=0; i < inputFiles.size(); ++i) {
	    SequenceQ[] sequences = new SequenceQ[inputFiles.get(i).size()];
	    int added = 0;
	    for (String fileName : inputFiles.get(i)) {
		InputStream fr = new FileInputStream(fileName);
		char inputFormat = 'U';
		int c;
		while ((c=fr.read()) !=-1) {
		    if ((char) c == '@') { // FastQ format
			inputFormat = 'Q';
			break;
		    }
		    else if ((char) c == 'B' || (char) c == 'b') { //PHD.1 format
			inputFormat = 'p';
			break;
		    }
		}
		fr.close();
		if (inputFormat == 'Q') {
		    fr = new FileInputStream(fileName);
		    int j = 1;
		    while ((c=fr.read()) !=-1) {
			SequenceQ seq;
			String name = fileName;
			if ((char) c == '@') {
			    seq = new SequenceQ(fr, name, 'i');
			    ++j;
			}
			else seq = new SequenceQ(fr, name);
			if (seq.length()>0) sequences[added++] = seq;
		    }
		}
		else if (inputFormat == 'p') {
		    SequenceQ seq = new SequenceQ(fileName);
		    if (seq.length()>0) sequences[added++] = seq;
		}
		else {
		    System.err.println("Unrecognized file format of file " + fileName + ".");
		    System.exit(1);
		}
	    }
	    if (batch) {
		sequenceBatches[i] = sequences;
	    }
	    else {
		if (sequenceBatches[0] == null) {
		    sequenceBatches[0] = sequences;
		}
		else {
		    SequenceQ[] temp = new SequenceQ[sequenceBatches[0].length+sequences.length];
		    System.arraycopy(sequenceBatches[0],0,temp,0,sequenceBatches[0].length);
		    System.arraycopy(sequences,0,temp,sequenceBatches[0].length,sequences.length);
		    sequenceBatches[0] = temp;
		}
	    }
	}
	//////////////////////////////////
	// Align sequences into contigs //
	//////////////////////////////////
	for (int b=0; b < sequenceBatches.length; ++b) {
	    AlignedQ[] addContigs = MSAQ.align(sequenceBatches[b],trim_qual); // Do Multiple Sequence Alignment considering quality scores
	    if (contigs == null) {
		contigs = addContigs;
	    }
	    else {
		AlignedQ[] newContigs = new AlignedQ[contigs.length + addContigs.length];
		System.arraycopy(contigs,0,newContigs,0,contigs.length);
		System.arraycopy(addContigs,0,newContigs,contigs.length,addContigs.length);
		contigs = newContigs;
	    }
	}
	//////////////////
	// Print output //
	//////////////////
     	if (output_format.equals("ACE")) { ContigWriter.writeACE(contigs,System.out); }
	else if (output_format.equals("FASTA")) { ContigWriter.writeFASTA(contigs,System.out); }
	else if (output_format.equals("FASTQ")) { ContigWriter.writeFASTQ(contigs,System.out); }
	else if (output_format.equals("MFASTA")) { ContigWriter.writeMFASTA(contigs,System.out); }
	else if (output_format.equals("MFASTQ")) { ContigWriter.writeMFASTQ(contigs,System.out); }
    }
}
