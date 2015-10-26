package org.chat.utils;

import java.util.Arrays;

import org.chat.Config;

public class StringXORer {
	
	public static String encode(String s, int key){
		byte[] bytes = Utils.getBytesOfInt(key).getBytes();
		byte[] slovo = s.getBytes();
		
		for(int i=0 ; i<bytes.length ; i++)
			bytes[i] -= 48;
		
		byte[] result = xorWithKey(slovo, bytes);
		
		byte[] postFix = new String(result).substring(result.length - Config.CHAT_CRC_LENGTH + 1, result.length).getBytes();
		
		if(!Arrays.equals(postFix, new byte[]{0,0,0,0,0,0,0}))
			return null;
		
		result = new String(result).substring(0, result.length - bytes.length + 1).getBytes();
		for(int i=0 ; i< result.length ; i++)
			result[i] += 48;
		
		
		int j = result.length / 8;
		bytes = new byte[j];
		
		for(int i=0 ; i<j ; i++)
			bytes[i] = Byte.parseByte(new String(Arrays.copyOfRange(result, i * 8, i * 8 + 8)), 2);
		
		
		return new String(bytes);
	}
	
	public static String decode(String s, int key) {
		byte[] slovo = Utils.concatenate(Utils.getBitsFromByteArray(new String(s).getBytes()).getBytes(), new byte[]{48,48,48,48,48,48,48});
		byte[] bytes = Utils.getBytesOfInt(key).getBytes();
		
		for(int i=0 ; i<slovo.length ; i++)
			slovo[i] -= 48;
		for(int i=0 ; i<bytes.length ; i++)
			bytes[i] -= 48;
		return new String(xorWithKey(slovo, bytes));
	}

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        
        for (int i = 0; i < a.length; i++)
            out[i] = (byte) ((a[i] ^ key[i % key.length]));
       
        return out;
    }

	public static boolean checkMessage(String message) {
		// TODO Auto-generated method stub
		return false;
	}
}