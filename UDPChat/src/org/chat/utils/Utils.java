package org.chat.utils;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

public class Utils {
	public static byte[] getByteArray(int value) {
	     return  ByteBuffer.allocate(4).putInt(value).array();
	}
	
	public static int getInt(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}
	
	public static byte[] concatenate (byte[] a, byte b[]) {
	    int aLen = a.length;
	    int bLen = b.length;

	    byte[] c =(byte[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}
	
	public static void sleep(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
//	public static<T> T[] concatenate (T[] a, T[] b) {
//	    int aLen = a.length;
//	    int bLen = b.length;
//
//	    @SuppressWarnings("unchecked")
//	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
//	    System.arraycopy(a, 0, c, 0, aLen);
//	    System.arraycopy(b, 0, c, aLen, bLen);
//
//	    return c;
//	}
}
