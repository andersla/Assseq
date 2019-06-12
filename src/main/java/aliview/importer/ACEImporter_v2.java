package aliview.importer;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
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

import aliview.MemoryUtils;
import aliview.sequences.MSFSequence;
import aliview.sequences.PhylipSequence;
import aliview.sequences.Sequence;

public class ACEImporter_v2 {
	private static final Logger logger = Logger.getLogger(ACEImporter_v2.class);

	private Reader reader;
	public static final int UNKNOWN = -1;
	public static int INTERLEAVED_OR_SINGLELINE_SEQUENTIAL = 0;
	public int formatType;

	public static void main(String[] args) throws FileNotFoundException, AlignmentImportException {
		File alignmentFile = new File("/home/anders/projekt/alignments/ace/9documentsAssembly.ace");
		ACEImporter_v2 importer = new ACEImporter_v2(new FileReader(alignmentFile));
		importer.importSequences();
	}
	

	public ACEImporter_v2(Reader reader) {
		this.reader = reader;
	}


	public List<Sequence> importSequences() throws AlignmentImportException {

		long startTime = System.currentTimeMillis();
		List<Sequence> sequences = new ArrayList<Sequence>();
		try {
			String sequenceString = "";
			BufferedReader r = new BufferedReader(this.reader);
			ReaderHelper helper = new ReaderHelper(r);

			helper.readNextLine();
			String firstLine = helper.getNextLine();
			boolean isRightFormat = isStringValidFirstLine(firstLine);
			if(! isRightFormat){
				throw new AlignmentImportException("Could not read file as ACE format");
			}
			
			String[] splittedFirst = StringUtils.split(firstLine, ' ');
			int nContigs = Integer.parseInt(splittedFirst[1]);
			int nReads = Integer.parseInt(splittedFirst[2]);
/*			
			Contigs = 
			while(helper.readUntilNextLineContains("CO ")) {
				String contigHeader = helper.getNextLine();
				String[] splittedCO = StringUtils.split(firstLine, ' ');
				CO <contig name> <# of bases> <# of reads in contig> <# of base segments in contig> <U or C>
				
				
			}
				
				
				
					String line = helper.getNextLine();
					//		logger.info("line" + line);
					// remove blanks in beginning of name
					line = line.trim();
					int index = ReaderHelper.indexOfFirstNonWhiteCharAfterWhiteChar(line);
					String name = line.substring(0, index).trim();
					seqNames.add(name);

					String seqChars = line.substring(index);

					// remove any blank and replace MSF . and ~ characters
					seqChars = ReaderHelper.removeSpaceAndTab(seqChars);
					seqChars = replaceMSFGapCharacters(seqChars);

					int capacity = guessedLength; // we dont know (i guess it could be read in header)
					ByteBufferAutogrow seqBuff = new ByteBufferAutogrow(capacity);	
					seqBuff.append(seqChars);
					seqBuffers.add(seqBuff);
					seqCount ++;

					helper.readNextLine();
				}




				// if sequences are interleaved then there are more data to read
				while(helper.readUntilNextLineContains(firstName)){

					// loop through all sequences in order
					int lineCount = 0;

					while(lineCount < seqCount){
						// read lines of seq data
						String line = helper.getNextLine();
						// remove blanks in beginning of name
						line = line.trim();
						int index = ReaderHelper.indexOfFirstNonWhiteCharAfterWhiteChar(line);

						String moreChars = line.substring(index);

						// remove any blank and replace MSF . and ~ characters
						moreChars = ReaderHelper.removeSpaceAndTab(moreChars);
						moreChars = replaceMSFGapCharacters(moreChars);

						ByteBufferAutogrow seqBuff = seqBuffers.get(lineCount);
						seqBuff.append(moreChars);

						lineCount ++;
						helper.readNextLine();
					}

					//	MemoryUtils.logMem();

				}
				
				

				for(int n = 0; n <seqCount; n++){	
					//sequences.add(new PhylipSequence(seqNames.get(n), ""));
					sequences.add(new MSFSequence(seqNames.get(n), seqBuffers.get(n).getBytes()));
					// and clear memory
					seqNames.set(n,null);
					seqBuffers.set(n,null);
				}

				// Only logging
				//				for(Sequence seq: sequences){
				//					logger.info(seq.getName() + " " + seq.getBasesAsString());
				//				}
			}
			*/


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("reading sequences took " + (endTime - startTime) + " milliseconds");

		return sequences;
	}

	private String replaceMSFGapCharacters(String seqChars){
		if(seqChars.indexOf('.') > -1){
			seqChars = seqChars.replace('.', '-');
		}
		if(seqChars.indexOf('~') > -1){
			seqChars = seqChars.replace('~', '-');
		}
		return seqChars;
	}


	public static boolean isStringValidFirstLine(String firstLine) {
		if(StringUtils.startsWith(firstLine, "AS ")){
			return true;
		}else{
			return false;
		}
	}

	/*
	 * 
	 * This method is copied and modified from iubio.readseq
	 * 
	 */

	public static int GCGchecksum(Sequence seq){
		int check = 0;

		for (int n = 0; n < seq.getLength(); n++){
			byte byteVal = seq.getBaseAtPos(n);
			int val = Character.toLowerCase(byteVal);
			if (val >= 'a' && val <= 'z'){
				val -= 32;
			}

			int positionMultiplier = n % 57 + 1;
			check += val * positionMultiplier;

		}
		check %= 10000;
		return check;
	}

}

