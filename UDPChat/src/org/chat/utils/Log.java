package org.chat.utils;

public class Log {
	public final static boolean CONSTRUCTORS 	= false;
	public final static boolean CONNECTION 		= false;
	public final static boolean EXCEPTIONS 		= false;
	public final static boolean FILE_MESSAGE 	= false;
	
	public static void write(String message, boolean val){
		if(val)
			System.out.println(message);
	}
}
