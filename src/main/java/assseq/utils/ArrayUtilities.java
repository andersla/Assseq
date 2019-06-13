package assseq.utils;

import java.util.BitSet;

import org.apache.log4j.Logger;


public class ArrayUtilities {
	private static final Logger logger = Logger.getLogger(ArrayUtilities.class);

	public static final int count(boolean[] array, boolean target){
		if(array == null){
			return 0;
		}
		int count = 0;
		for(boolean val: array){
			if(val == target){
				count ++;
			}
		}
		return count;
	}

	public static byte[] replaceAll(byte[] byteArray, char find, byte replace) {
		if(byteArray == null){
			return null;
		}

		for(int n = 0; n < byteArray.length; n++){
			if(byteArray[n] == find){
				byteArray[n] = replace;
			}
		}
		return byteArray;
	}

	public static byte[] replaceAll(byte[] byteArray, byte find, byte replace) {
		if(byteArray == null){
			return null;
		}

		for(int n = 0; n < byteArray.length; n++){
			if(byteArray[n] == find){
				byteArray[n] = replace;
			}
		}
		return byteArray;
	}


	public static byte[] insertAt(byte[] array, int pos, byte[] newBytes) {
		if(array == null || newBytes == null || newBytes.length == 0){
			return array;
		}

		byte[] newArray = new byte[array.length + newBytes.length];

		// copy first part of array
		System.arraycopy(array, 0, newArray, 0, pos); // insert at means first part is to pos - 1

		// copy inserts
		System.arraycopy(newBytes, 0, newArray, pos, newBytes.length);

		// copy last
		System.arraycopy(array, pos, newArray, pos + newBytes.length, array.length - pos);

		return newArray;
	}

	public static short[] insertAt(short[] array, int pos, short[] newBytes) {
		if(array == null || newBytes == null || newBytes.length == 0){
			return array;
		}

		short[] newArray = new short[array.length + newBytes.length];

		// copy first part of array
		System.arraycopy(array, 0, newArray, 0, pos); // insert at means first part is to pos - 1

		// copy inserts
		System.arraycopy(newBytes, 0, newArray, pos, newBytes.length);

		// copy last
		System.arraycopy(array, pos, newArray, pos + newBytes.length, array.length - pos);

		return newArray;
	}

	public static int[] insertAt(int[] array, int pos, int[] newVals) {
		if(array == null || newVals == null || newVals.length == 0){
			return array;
		}

		int[] newArray = new int[array.length + newVals.length];

		// copy first part of array
		System.arraycopy(array, 0, newArray, 0, pos); // insert at means first part is to pos - 1

		// copy inserts
		System.arraycopy(newVals, 0, newArray, pos, newVals.length);

		// copy last
		System.arraycopy(array, pos, newArray, pos + newVals.length, array.length - pos);

		return newArray;
	}
	
	public static boolean[] insertAt(boolean[] array, int pos, boolean[] newVals) {
		if(array == null || newVals == null || newVals.length == 0){
			return array;
		}

		boolean[] newArray = new boolean[array.length + newVals.length];

		// copy first part of array
		System.arraycopy(array, 0, newArray, 0, pos); // insert at means first part is to pos - 1

		// copy inserts
		System.arraycopy(newVals, 0, newArray, pos, newVals.length);

		// copy last
		System.arraycopy(array, pos, newArray, pos + newVals.length, array.length - pos);

		return newArray;
		
	}
	
	public static BitSet insertAt(BitSet orig, int pos, int length) {
		BitSet newBitSet = new BitSet(orig.length() + length);
		
		for(int n = 0; n <= pos; n++) {
			newBitSet.set(n, orig.get(n));
		}
		
		for(int n = pos; n < pos + length; n++) {
			// automatically not set
		}
		for(int n = pos; n < orig.length(); n++) {
			newBitSet.set(n + length, orig.get(n));
		}
		
		return newBitSet;
		
	}


	public static void debug(int[] array) {
		for(int n = 0; n < array.length; n++) {
			logger.info("n=" + n + " " + array[n]);
		}
	}

	public static void debug(byte[] array) {
		for(int n = 0; n < array.length; n++) {
			logger.info("n=" + n + " " + array[n]);
		}
	}

	public static void debug(short[] array) {
		for(int n = 0; n < array.length; n++) {
			logger.info("n=" + n + " " + array[n]);
		}
	}

	public static int getMax(int[] array) {
		int max = Integer.MIN_VALUE;
		for(int val: array) {
			max = Math.max(max, val);
		}
		return max;
	}

	public static void scaleVals(int[] array, double scaleFactor) {
		for (int i=0; i<array.length; i++) {
			// rounding to ind is always down
			array[i] = (int)((double)array[i] * scaleFactor);
		}
	}

	public static void addToArrayValues(int[] array, int addVal) {
		addToArrayValues(array, addVal, 0);
	}
	
	public static void addToArrayValuesShort(short[] array, short addVal) {
		for (int i=0; i<array.length; i++) {
			array[i] = (short) (array[i] + addVal);
		}
		
	}

	public static void addToArrayValues(int[] array, int addVal, int startPos) {
		for (int i=startPos; i<array.length; i++) {
			// rounding to ind is always down
			array[i] = array[i] + addVal;
		}
	}
	
	public static void subtractFromArrayValues(int[] array, int delVal, int startPos) {
		for (int i=startPos; i<array.length; i++) {
			// rounding to ind is always down
			array[i] = array[i] - delVal;
		}
	}
	
	

	public static int[] scaleLength(int[] input, int width) {

		if(input.length < 2) {
			return input;
		}

		int[] result = new int[width];

		double scale = (double)input.length / (double)(width - 1);
		// since n = 0 first pos always kept
		for(int n = 1; n < result.length - 1; n++) {
			int pos = (int) Math.round((double)scale * (double)n);
			result[n] = input[pos];
		}

		// always keep first and last pos
		result[0] = input[0];
		result[result.length - 1] = input[input.length - 1];

		return result;
	}

	public static void invert(int[] array, int roofVal) {
		for (int i=0; i<array.length; i++) {
			array[i] = Math.abs(array[i] - roofVal);
		}
	}

	public static int[] createEvenDistributedArray(int length, int minVal, int maxVal) {	
		if(length == 0) {
			return new int[0];
		}

		int[] array = new int[length];
		array[0] = minVal;
		array[length-1] = maxVal;
		
		double diff = ((double)maxVal - (double)minVal) / ((double)length -1);
		for(int n = 1; n < array.length -1; n++) {
			array[n] = (int)Math.round((double)n * diff);
		}
		return array;
	}

	public static int[] deletePos(int[] array, int delPos) {

		// If the array is empty 
		// or the index is not in array range 
		// return the original array 
		if (array == null
				|| delPos < 0
				|| delPos >= array.length) { 

			return array; 
		} 

		// Create another array of size one less 
		int[] newArray = new int[array.length - 1]; 

		// Copy the elements from starting till index 
		// from original array to the other array 
		System.arraycopy(array, 0, newArray, 0, delPos); 

		// Copy the elements from index + 1 till end 
		// from original array to the other array 
		System.arraycopy(array, delPos + 1, newArray, delPos, array.length - delPos- 1); 

		// return the resultant array 
		return newArray; 
	}
	
	
	// StartPos is inclusive, endPos is exclusive
	public static int[] deletePos(int[] array, int startPosInclusive, int endPosExclusive) { 
		
		// If the array is empty 
		// or the index is not in array range 
		// return the original array 
//		if (array == null
//				|| startPosInclusive < 0
//				|| startPosInclusive >= array.length
//				|| endPosExclusive < 0
//				|| endPosExclusive > array.length
//				|| endPosExclusive <= startPosInclusive
//				) { 
//
//			return array; 
//		} 
		
		
		int delLength = endPosExclusive - startPosInclusive;

		// Create another array of size one less 
		int[] newArray = new int[array.length - delLength]; 

		// Copy the elements from starting till index 
		// from original array to the other array 
		System.arraycopy(array, 0, newArray, 0, startPosInclusive); 

		// Copy the elements from index + 1 till end 
		// from original array to the other array 
		System.arraycopy(array, startPosInclusive + delLength, newArray, startPosInclusive, array.length - startPosInclusive- delLength); 

		// return the resultant array 
		return newArray; 
	}

	public static long sum(int[] array) {
		long sum = 0;
		for (int i : array) {
		    sum += i;
		}
		return sum;
	}

	public static boolean allSame(int[] array) {
		if(array.length <= 1) {
			return true;
		}

		for (int n = 1; n < array.length; n++) {
		   if(array[n - 1] != array[n]) {
			   return false;
		   }
		}
		
		return true;
	}

	public static BitSet reverse(BitSet inputSet, int targetLength) {
		BitSet newSet = new BitSet(inputSet.length());
		for (int i = inputSet.nextSetBit(0); i >= 0; i = inputSet.nextSetBit(i+1)) {

			newSet.set(targetLength - i);
			
		 }
	
		return newSet;
		
	}

	public byte[] deleteAllWithVal(byte[] array, byte target) {
		
		// how many to delete so we can create a new array right size
		int count = 0;
		for(int n = 0; n < array.length; n++){
			if(array[n] == target){
				count ++;
			}
		}
		
		
		byte[] newArray;
		
        // Just clone it if nothing deleted
		if(count == 0) {
			newArray = array.clone();
		}
		else{
			newArray = new byte[array.length - count];

			int index = 0;
			for(byte next : array){
				if(next != target){
					newArray[index] = next;
					index ++;
				}
			}
		}
		
		return newArray;	
	}

	public static short[] intArray2ShortArray(int[] input) {
		short[] output = new short[input.length];
		for(int n = 0; n < output.length; n++) {
			output[n] = (short)input[n];
		}
		return output;
	}

	public static byte[] intArray2ByteArray(int[] input) {
		byte[] output = new byte[input.length];
		for(int n = 0; n < output.length; n++) {
			output[n] = (byte)input[n];
		}
		return output;
	}




}
