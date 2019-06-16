package assseq.aligner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import utils.OSNativeUtils;


public class Cap3Wrapper {

	public static void main(String[] args) {
		getBinPath();
	}

	private static final Logger logger = Logger.getLogger(Cap3Wrapper.class);

	public static File getBinPath(){

		// get predefined path

		// if no predefined path create one for distributed program
		File localBinFile = new File(getAliViewUserDataDirectory(), "/binaries" + File.separator + getBinDependingOnOS());

		//logger.info("localMuscleBinFile.lastModified()" + localMuscleBinFile.lastModified());
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		//System.out.println("After Format : " + sdf.format(localMuscleBinFile.lastModified()));

		// check if file exist if not extract it from jar
		if(! localBinFile.exists() || localBinFile.length() == 0 || localBinFile.lastModified() < 1395868152000L){ // modified date = 20140326 22:09:12 (this is the date when saving file problem finally was resolved)
			ClassLoader cl = Cap3Wrapper.class.getClassLoader();	
			try {
				boolean fileISOK = false;
				int nTries = 0;
				while(! fileISOK && nTries <=3){
					nTries ++;
					InputStream binStreamFromJar = cl.getResourceAsStream(localBinFile.getName());
					logger.info(binStreamFromJar);
					copyBinFileToLocalDir(binStreamFromJar, localBinFile);
					// reopen stream
					binStreamFromJar = cl.getResourceAsStream(localBinFile.getName());
					fileISOK = verifyMD5(binStreamFromJar, localBinFile);
					if(! fileISOK){
						FileUtils.deleteQuietly(localBinFile);
					}else{
						localBinFile.setExecutable(true);
					}
				}

				// on win also copy dll	
				if(OSNativeUtils.isWindows()) {
					fileISOK = false;
					nTries = 0;
					File localDLLFile = new File(getAliViewUserDataDirectory(), "/binaries" + File.separator + getWindowsDllBinName());
					while(! fileISOK && nTries <=3){
						nTries ++;
						InputStream binStreamFromJar = cl.getResourceAsStream(localDLLFile.getName());
						logger.info(binStreamFromJar);
						copyBinFileToLocalDir(binStreamFromJar, localDLLFile);
						// reopen stream
						binStreamFromJar = cl.getResourceAsStream(localDLLFile.getName());
						fileISOK = verifyMD5(binStreamFromJar, localDLLFile);
						if(! fileISOK){
							FileUtils.deleteQuietly(localDLLFile);
						}else{
							localDLLFile.setExecutable(true);
						}
					}
				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// TODO Maybe notify something went wrong with installing muscle
			}

		}

		logger.info(localBinFile);		
		return localBinFile;

	}


	private static boolean verifyMD5(InputStream streamFromJar,File localBinFile) throws FileNotFoundException {
		boolean checksumOK = false;
		String checksum1 = checkSum(streamFromJar);
		String checksum2 = checkSum(new FileInputStream(localBinFile));
		logger.info(checksum1);
		logger.info(checksum2);
		if(checksum1 != null && checksum2 != null && checksum1.equals(checksum2)){
			checksumOK = true;
		}
		return checksumOK;
	}


	private static void copyBinFileToLocalDir(InputStream binStreamFromJar, File localBinFile) throws IOException {
		FileUtils.forceMkdir(localBinFile.getParentFile());
		FileUtils.copyInputStreamToFile(binStreamFromJar, localBinFile);		
	}


	public static final String getAliViewUserDataDirectory() {
		return System.getProperty("user.home") + File.separator + ".AliView";
	}



	public static final String getBinDependingOnOS() {

		String binName = "";
		// 64-bit
		if(OSNativeUtils.is64BitOS()){
			if(OSNativeUtils.isMac()){
				binName = "cap3.macosx.intel64";
			}
			else if(OSNativeUtils.isLinuxOrUnix()){
				binName = "cap3.linux.x86_64";
			}
			// default
			else{
				binName = "cap3.exe";
			}
			// 32-bit
		}else{
			if(OSNativeUtils.isMac()){
				if(OSNativeUtils.isPowerPC()){
					binName = "not-going-to-work";
				}
				else{
					binName = "cap3.macosx.intel32";
				}
			}
			else if(OSNativeUtils.isLinuxOrUnix()){
				binName = "cap3.linux.32bit";
			}
			// default
			else{
				binName = "cap3.exe";
			}
		}

		return binName;

	}
	
	public static final String getWindowsDllBinName() {
		return "cygwin1.dll";
	}

	/*
	 * Calculate checksum of a File using MD5 algorithm
	 */
	public static String checkSum(InputStream instream){
		String checksum = null;

		try {
			BufferedInputStream bis = new BufferedInputStream(instream);
			MessageDigest md = MessageDigest.getInstance("MD5");

			//Using MessageDigest update() method to provide input
			byte[] buffer = new byte[8192];
			int numOfBytesRead;
			while( (numOfBytesRead = bis.read(buffer)) > 0){
				md.update(buffer, 0, numOfBytesRead);
			}
			byte[] hash = md.digest();
			checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return checksum;
	}


}


