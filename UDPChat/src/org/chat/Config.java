package org.chat;

public class Config {
	public final static String GUI_TITLE			= "UDP Chat";
	public final static String GUI_DEFAULT_ADRESS 	= "localhost";
	public final static String GUI_DEFAULT_PORT 	= "1234";
	
	public final static int CHAT_TOTAL_MAX_MSG_SIZE = 1000;
	public final static int CHAT_DEFAULT_MSG_SIZE 	= 300;
	public final static int CHAT_CRC_LENGTH			= 8;
	
	public final static int PING_LOOP_FREQUENCY		= 500;
	public final static int PING_WAITING_TIME		= 300;
	public final static int PING_CHECKING_TIME		= 2000;
	
	public final static int MESSAGES_LOOP_FREQUENCY	= 5000;
}
