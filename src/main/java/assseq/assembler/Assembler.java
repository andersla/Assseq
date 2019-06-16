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
			SequenceQ[] sequences
			) throws Exception {


		//////////////////////////////////
		// Align sequences into contigs //
		//////////////////////////////////
		AlignedQ[] contigs = MSAQ.align(sequences,trim_qual); // Do Multiple Sequence Alignment considering quality scores
		

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
