package aliview.sequences;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import aliview.NucleotideUtilities;
import aliview.utils.ArrayUtilities;

public class DefaultQualCalledBases implements Bases {
	private static final Logger logger = Logger.getLogger(DefaultQualCalledBases.class);
	private static final String TEXT_FILE_BYTE_ENCODING = "ASCII";
	byte[] backend;
	byte[] qualCalls;
	byte NONE_QUALCALL = 0;

	public DefaultQualCalledBases(byte[] bytes, byte[] qualCalls) {
		this.backend = bytes;
		this.qualCalls = qualCalls;
	}

	public DefaultQualCalledBases getCopy(){
		return new DefaultQualCalledBases(ArrayUtils.clone(backend), ArrayUtils.clone(qualCalls));
	}

	public int getLength(){
		return backend.length;
	}

	public byte get(int n) {
		return backend[n];
	}

	public char charAt(int n) {
		// or translated
		return (char) backend[n];
	}

	public byte[] toByteArray() {
		return backend;
	}

	public byte[] toByteArray(int startIndexInclusive, int endIndexInclusive) {
		byte[] subArray = ArrayUtils.subarray(backend, startIndexInclusive, endIndexInclusive + 1);
		return subArray;	
	}

	@Override
	public String toString() {
		String asString = null;
		// or translated
		try {
			asString = new String(backend, TEXT_FILE_BYTE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return asString;
	}

	public void writeBasesBetween(int start, int end, Writer out) throws IOException {
		String outString = new String(toByteArray(start, end), TEXT_FILE_BYTE_ENCODING);
		out.write(outString);
	}

	public void set(int n, byte newBase) {
		assureSize(n);
		backend[n] = newBase;

		logger.warn("Not perfectly implemented");
		qualCalls[n] = NONE_QUALCALL;
		
	}

	private void assureSize(int n) {
		if(n >= backend.length){
			resize(n + 1);
		}
	}

	private void resize(int n) {
		logger.debug("resize=" + n);
		int additionalCount = n - backend.length;
		
		// Backend
		byte[] additional = new byte[additionalCount];
		Arrays.fill(additional, SequenceUtils.GAP_SYMBOL);
		backend = ArrayUtils.addAll(backend, additional);
		
		// Qualcalls
		byte[] additionalQual = new byte[additionalCount];
		Arrays.fill(additionalQual, NONE_QUALCALL);
		qualCalls = ArrayUtils.addAll(qualCalls, additionalQual);
		
		logger.debug("backend.length=" + backend.length);		
	}

	public void append(byte[] newBytes) {
		backend = ArrayUtils.addAll(backend, newBytes);
		
		// QualCalls
		byte[] additionalQual = new byte[newBytes.length];
		Arrays.fill(additionalQual, NONE_QUALCALL);
		qualCalls = ArrayUtils.addAll(qualCalls, additionalQual);
	}

	public void moveBaseLeft(int n) {
		set(n - 1, get(n));	
	}

	public void moveBaseRight(int n) {
		set(n + 1, get(n));	
	}


	public void insertAt(int n, byte[] newBytes) {
		assureSize(n - 1);	
		byte[] newArray = ArrayUtilities.insertAt(backend, n, newBytes);
		backend = newArray;
		
		// QualCalls
		byte[] additionalQual = new byte[newBytes.length];
		Arrays.fill(additionalQual, NONE_QUALCALL);
		byte[] newQual = ArrayUtilities.insertAt(qualCalls, n, additionalQual);
		qualCalls = newQual;
		
	}

	public void replace(int startReplaceIndex, int stopReplaceIndex, byte[] insertBases) {

		int newLength = backend.length - (stopReplaceIndex + 1 - startReplaceIndex) + insertBases.length;

		// TODO could check if length is less - then just clear and insert
		byte[] newBackend = new byte[newLength];

		// copy first untouched part of sequence
		System.arraycopy(backend, 0, newBackend, 0, startReplaceIndex);

		// copy insert bases
		System.arraycopy(insertBases, 0, newBackend, startReplaceIndex, insertBases.length);

		// copy last untouched part of sequence - if there is one
		if(stopReplaceIndex < backend.length - 1){
			System.arraycopy(backend, stopReplaceIndex + 1, newBackend, startReplaceIndex + insertBases.length, backend.length - (stopReplaceIndex + 1));
		}

		backend = newBackend;
		
		// QualCalls
		byte[] newCalls = new byte[newLength];
		
		// Create empty new ones
		byte[] insertQual = new byte[insertBases.length];
		Arrays.fill(insertQual, NONE_QUALCALL);

		// copy first untouched part of sequence
		System.arraycopy(qualCalls, 0, newCalls, 0, startReplaceIndex);
		
		// copy insert bases
		System.arraycopy(insertQual, 0, newCalls, startReplaceIndex, insertQual.length);

		// copy last untouched part of sequence - if there is one
		if(stopReplaceIndex < backend.length - 1){
			System.arraycopy(qualCalls, stopReplaceIndex + 1, newCalls, startReplaceIndex + insertQual.length, qualCalls.length - (stopReplaceIndex + 1));
		}
		
		qualCalls = newCalls;
	}

	public void deleteAll(byte target) {

		// how many to delete so we can create a new array right size
		int count = 0;
		for(int n = 0; n < backend.length; n++){
			if(backend[n] == target){
				count ++;
			}
		}

		// copy all bytes not to delete into new array
		if(count > 0){
			byte[] newBackend = new byte[backend.length - count];

			int index = 0;
			for(byte next : backend){
				if(next != target){
					newBackend[index] = next;
					index ++;
				}
			}
			backend = newBackend;
		}
		
		logger.warn("Not fixed for qual-called bases");
		qualCalls = new byte[backend.length];

	}

	public void delete(int[] toDelete) {
		if(toDelete == null || toDelete.length == 0){
			return;
		}

		Arrays.sort(toDelete);

		// translate toDelete
		int nOutOfBounds = 0;
		for(int n = 0; n < toDelete.length; n++){
			if(toDelete[n] < 0 || toDelete[n] >= backend.length){
				nOutOfBounds ++;
			}
		}

		// create new array size removed selected bases
		byte[] newBases = new byte[backend.length - toDelete.length + nOutOfBounds];

		int newIndex = 0;

		int deleteCount = 0;
		int nextToDelete = toDelete[deleteCount];

		for(int n = 0;n < backend.length ;n++){

			if(n == nextToDelete){
				// dont copy this one
				deleteCount ++;
				if(deleteCount < toDelete.length){
					nextToDelete = toDelete[deleteCount];
				}else{
					nextToDelete = -1;
				}

			}
			else{
				newBases[newIndex] = backend[n];
				newIndex ++;
			}
		}

		backend = newBases;
		
		logger.warn("Not fixed for qual-called bases");
		qualCalls = new byte[backend.length];
		
	}

	// ?????
	public void complement() {
		NucleotideUtilities.complement(backend);
		
		// Nothing to do for qualCalls
	}

	// ?????
	public void reverse() {
		ArrayUtils.reverse(backend);
		ArrayUtils.reverse(qualCalls);
	}

	// convenience method
	public void set(int n, char c) {
		set(n, (byte) c);
	}

	// convenience
	public void delete(int pos) {
		delete(new int[]{pos});
	}

	// convenience
	public void insertAt(int n, byte newByte) {
		insertAt(n, new byte[]{newByte});
	}

}
