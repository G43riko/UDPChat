package org.chat.utils;

public class Log {
	public final static boolean CONSTRUCTORS 	= false;
	public final static boolean CONNECTION 		= true;
	public final static boolean EXCEPTIONS 		= false;
	
	public static void write(String message, boolean val){
		if(val)
			System.out.println(message);
	}
}
