package assseq.importer;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import assseq.MemoryUtils;
import assseq.sequences.ABISequence;
import assseq.sequences.ACEBaseSequence;
import assseq.sequences.BasicQualCalledSequence;
import assseq.sequences.BasicTraceSequence;
import assseq.sequences.DefaultQualCalledBases;
import assseq.sequences.MSFSequence;
import assseq.sequences.PhylipSequence;
import assseq.sequences.QualCalledSequence;
import assseq.sequences.Sequence;
import assseq.utils.ArrayUtilities;

public class ACEImporter {
	private static final Logger logger = Logger.getLogger(ACEImporter.class);

	public static final int UNKNOWN = -1;
	public static int INTERLEAVED_OR_SINGLELINE_SEQUENTIAL = 0;
	public int formatType;
	private File inputFile;

	/*
	public static void main(String[] args) throws AlignmentImportException, FileNotFoundException {
		File alignmentFile = new File("/home/anders/projekt/alignments/ace/9documentsAssembly.ace");
		ACEImporter importer = new ACEImporter(alignmentFile);
		List<Sequence> sequences = importer.importSequences();

		for(Sequence seq: sequences) {
			logger.info(seq);
		}	
	}
	 */

	public ACEImporter(File inputFile) {
		this.inputFile = inputFile;
	}

	public QualCalledSequence importConsensus() throws AlignmentImportException, FileNotFoundException {

		BasicQualCalledSequence seq = null;
		try {
			ACEparser parser = new ACEparser();
			parser.parsACE(new FileInputStream(inputFile));

			int contigIndex = 0;
			String contigSeq = parser.getContigSeq(contigIndex);
			int[] contigQual = parser.getContigQual(contigIndex);
			//int startPos = parser.getContigSeq(0);

			byte[] bases = contigSeq.getBytes();
			short[] shortQualCalls = ArrayUtilities.intArray2ShortArray(contigQual);

			DefaultQualCalledBases basesAndCalls = new DefaultQualCalledBases(bases, shortQualCalls);

			seq = new BasicQualCalledSequence(basesAndCalls);

			int minLeftStart = parser.getContigReadsMinLeftStart(contigIndex);

			int padSize = Math.abs(minLeftStart) + 1;

			logger.info("padSize" + padSize);

			seq.leftPadSequenceWithGaps( padSize + seq.getLength());


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return seq;


	}

	public List<Sequence> importSequences() throws AlignmentImportException, FileNotFoundException {

		logger.info("Inside ACE importer");
		ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		try {

			ACEparser parser = new ACEparser();
			parser.parsACE(new FileInputStream(inputFile));
			File aceDir = inputFile.getParentFile();

			for(int contigIndex = 0; contigIndex < parser.nContigs(); contigIndex ++) {

				int minLeftStart = parser.getContigReadsMinLeftStart(contigIndex);

				for(int readIndex = 0; readIndex < parser.getNreadsForContig(contigIndex); readIndex++) {

					String readName = parser.getReadNameForContig(contigIndex, readIndex);
					logger.info(readName);
					int readAlignStart = parser.getReadAlignStartForContig(contigIndex, readIndex);
					int startInContigForRead = parser.getStartInContigForRead(contigIndex, readIndex);
					logger.info("readAlignStart" + readAlignStart);
					logger.info("startInContigForRead" + startInContigForRead);


					File origReadFile = new File(aceDir, readName);
					logger.info("File: " + origReadFile + " exists: " + origReadFile.exists());

					if(origReadFile.exists() && FileFormat.ABI == FileFormat.isFileOfAlignmentFormat(origReadFile)) {
						logger.info("Found ABI-file");
						FileFormat origFileFormat = FileFormat.isFileOfAlignmentFormat(origReadFile);
						ABIImporter importer = new ABIImporter(origReadFile);
						List<Sequence> abiSeqs = importer.importSequences();	
						Sequence seq = abiSeqs.get(0);

						// Trim trace part that are not base-called
						if(seq instanceof ABISequence) {
							ABISequence abiSeq = (ABISequence) seq;
							abiSeq.trimTraces();
						}


						// Check if revcomp (this is done by string comparison instead of 
						String readSeq = parser.getReadSeqForContig(contigIndex, readIndex);
						
						String compare = StringUtils.substring(seq.getBasesAsString().toLowerCase(), 0, 20);
						boolean isUncomplemented = readSeq.toLowerCase().contains((compare));
						logger.info("" + seq + " is uncomplemented" + isUncomplemented);
						if(! isUncomplemented) {
							seq.reverseComplement();
						}

						logger.info("readSeq=" + readSeq);
						
						// Add qualClip to sequences
						if(seq instanceof BasicQualCalledSequence) {
							BasicQualCalledSequence basicQualCalledSeq = (BasicQualCalledSequence) seq;
							int qualStart = parser.getReadQualStartForContig(contigIndex, readIndex);
							// Adjust to internal 0 index sequence position
							qualStart = qualStart -1;
							basicQualCalledSeq.setQualClipStart(qualStart);
							int qualEnd = parser.getReadQualEndForContig(contigIndex, readIndex);
							// Adjust to internal 0 index sequence position
							basicQualCalledSeq.setQualClipEnd(qualEnd);
						}

						logger.debug(seq.getBasesAsString());

						// insert gaps
						logger.debug("readSeq:" + readSeq);
						for(int n = 0; n < readSeq.length(); n++) {
							if(readSeq.charAt(n) == '*' || readSeq.charAt(n) == '-') {
								logger.debug("insert gap at:" + n);
								seq.insertGapAt(n);
							}
						}

						int padSize = 0;

						padSize = Math.abs(minLeftStart) + readAlignStart;

						logger.info("LeftPad" + padSize);

						seq.leftPadSequenceWithGaps( padSize + seq.getLength());

						sequences.add(seq);
					}	
					else {
						logger.info("Is Basic ACE seq");
						String readSeqName = parser.getReadNameForContig(contigIndex, readIndex);
						String readSeq = parser.getReadSeqForContig(contigIndex, readIndex);
						byte[] bases = readSeq.getBytes();

						// There are no qual-qalls in ACE-base-sequences
						DefaultQualCalledBases basesAndCalls = new DefaultQualCalledBases(bases);

						ACEBaseSequence seq = new ACEBaseSequence(readSeqName, basesAndCalls);

						// Basesequences are already revcomped in ace-file
						//boolean isRevComp = parser.getReadRevcompForContig(contigIndex, readIndex);
						//logger.debug("isRevComp" + isRevComp);
						//if(isRevComp) {
						//	seq.reverseComplement();
						//}

						// Add qualClip to sequences
						int qualStart = parser.getReadQualStartForContig(contigIndex, readIndex);
						// Adjust to internal 0 index sequence position
						qualStart = qualStart -1;
						seq.setQualClipStart(qualStart);
						int qualEnd = parser.getReadQualEndForContig(contigIndex, readIndex);
						// Adjust to internal 0 index sequence position
						seq.setQualClipEnd(qualEnd);

						// pad seqs
						int padSize = 0;
						padSize = Math.abs(minLeftStart) + readAlignStart;
						//logger.debug("LeftPad" + padSize);
						seq.leftPadSequenceWithGaps( padSize + seq.getLength());

						sequences.add(seq);
					}

				}		
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sequences;
	}

	public static boolean isStringValidFirstLine(String firstLine) {
		if(StringUtils.startsWith(firstLine, "AS ")){
			return true;
		}else{
			return false;
		}
	}

}

