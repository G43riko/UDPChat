package org.chat.utils;

import java.nio.ByteBuffer;

public class Utils {
	public static byte[] getByteArray(int value) {
	     return  ByteBuffer.allocate(4).putInt(value).array();
	}
	
	public static int getInt(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}
}
