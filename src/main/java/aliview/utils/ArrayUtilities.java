package aliview.utils;

import org.apache.log4j.Logger;

import aliview.alignment.Alignment;


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
	
	public static int[] insertAt(int[] array, int pos, int[] newInts) {
		if(array == null || newInts == null || newInts.length == 0){
			return array;
		}

		int[] newArray = new int[array.length + newInts.length];

		// copy first part of array
		System.arraycopy(array, 0, newArray, 0, pos); // insert at means first part is to pos - 1

		// copy inserts
		System.arraycopy(newInts, 0, newArray, pos, newInts.length);

		// copy last
		System.arraycopy(array, pos, newArray, pos + newInts.length, array.length - pos);

		return newArray;
	}




}
