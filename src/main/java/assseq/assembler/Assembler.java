package assseq.assembler;

import java.io.*;
import java.util.*;
import java.util.ArrayList;


public class Assembler {
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
	
	public void assemble(int lowestAlScore,
			               int trim_qual,
			               String output_format,
			               ArrayList<File> inputFiles,
			               boolean batch
			               ) throws Exception {
		
		//////////////////////////////////////////////
		// Read sequences and order them in batches //
		//////////////////////////////////////////////
		AlignedQ[] contigs = null;
		SequenceQ[][] sequenceBatches;
		if (batch) sequenceBatches = new SequenceQ[inputFiles.size()][];
		else sequenceBatches = new SequenceQ[1][];
		
		
		for (int i=0; i < inputFiles.size(); ++i) {
			SequenceQ[] sequences = new SequenceQ[inputFiles.size()];
			int added = 0;
			for (File file : inputFiles){
				InputStream fr = new FileInputStream(file);
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
					fr = new FileInputStream(file);
					int j = 1;
					while ((c=fr.read()) !=-1) {
						SequenceQ seq;
						String name = file.getName();
						if ((char) c == '@') {
							seq = new SequenceQ(fr, name, 'i');
							++j;
						}
						else seq = new SequenceQ(fr, name);
						if (seq.length()>0) sequences[added++] = seq;
					}
				}
				else if (inputFormat == 'p') {
					SequenceQ seq = new SequenceQ(file.getAbsolutePath());
					if (seq.length()>0) sequences[added++] = seq;
				}
				else {
					System.err.println("Unrecognized file format of file " + file.getName() + ".");
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
