package org.chat.utils;

public class Log {
	public final static boolean CONSTRUCTORS = true;
	
	public static void write(String message, boolean val){
		if(val)
			System.out.println(message);
	}
}
