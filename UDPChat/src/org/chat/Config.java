package org.chat;

import org.chat.utils.Utils;

public class Config {
	public final static String GUI_TITLE			= "UDP Chat";
	public final static String GUI_DEFAULT_ADRESS 	= Utils.getIP();
	public final static String GUI_DEFAULT_PORT		= "1234";
	
	public final static int CHAT_TOTAL_MAX_MSG_SIZE = 65000; //65535
	public final static int CHAT_DEFAULT_MSG_SIZE 	= 3000;
	public final static int CHAT_CRC_LENGTH			= 8;
	
	public final static int PING_LOOP_FREQUENCY		= 5000;
	public final static int PING_WAITING_TIME		= 3000;
	public final static int PING_CHECKING_TIME		= 20000;
}
