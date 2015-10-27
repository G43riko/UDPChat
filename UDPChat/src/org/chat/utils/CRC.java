package org.chat.utils;

import java.util.Arrays;

public class CRC{
	public static int getCRC(){
		return IDGenerator.getId();
	}
	
	public static byte[] calcCRC(byte[] message, byte[] crc){
		int crcLenght = crc.length - 1;
		
		byte[]  zeros = new byte[crcLenght];
		Arrays.fill(zeros, (byte)0);
		
		message = Utils.concatenate(message, zeros);
		
		byte[] result = message;
		
		
		return null;
	}
}
