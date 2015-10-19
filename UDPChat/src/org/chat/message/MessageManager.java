package org.chat.message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

import org.chat.UDPChat;
import org.chat.core.User;
import org.chat.utils.IDGenerator;
import org.chat.utils.Log;
import org.chat.utils.Utils;

public class MessageManager {
	public final static byte MESSAGE_TEXT 		= 0;
	public final static byte MESSAGE_PING 		= 1;
	public final static byte MESSAGE_FILE 		= 3;
	public final static byte MESSAGE_LOGOUT 	= 4;
	public final static byte MESSAGE_WELCOME	= 5;
	
	private HashMap<Integer, Message> messages = new HashMap<Integer, Message>();
	private HashMap<String, User> users = new HashMap<String, User>(); 
	private UDPChat parent;
	//id spravy, pocet sprav,cislo spravy, velkos� spr�vy, typ spravy, [velkos� nazvu suboru, nazov suboru]

	public MessageManager(UDPChat parent) {
		Log.write("za�al kon�truktor objektu MessageManager", Log.CONSTRUCTORS);
		this.parent = parent;
		Log.write("skon�il kon�truktor objektu MessageManager", Log.CONSTRUCTORS);
	}
	
	/**
	 * spracuje �iastkov� spr�vu a vytvor� s nej MessagePart
	 * @param data
	 * @return
	 */
	private MessagePart createMessagePart(byte[] data){
		return new MessagePart(new String(data, 12, data.length - 12), 
							   Utils.getInt(Arrays.copyOfRange(data, 0, 4)), 
							   Utils.getInt(Arrays.copyOfRange(data, 4, 8)), 
							   Utils.getInt(Arrays.copyOfRange(data, 8, 12)), 
							   data[12]);
	}
	
	
	/**
	 * vytvor� nov� textuv� spr�vu 
	 * @param message
	 */
	public void createTextMessage(String message) {
		int id = IDGenerator.getId();
		messages.put(id, new Message(message, this, id, MESSAGE_TEXT));
	}
	
	/**
	 * vytvor� nov� uv�taciu spr�vu
	 */
	public void createWelcomeMessage(){
		int id = IDGenerator.getId();
		try {
			messages.put(id, 
						 new Message(parent.getLogin() + ":" + InetAddress.getLocalHost().getHostAddress(),
								 	 this, 
								 	 id, 
								 	 MESSAGE_WELCOME));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void proccessWelcomeMessage(String message) {
		String[] text = message.split(":");
		System.out.println("bola prijat� spr�va: " + message);
		users.put(text[0], new User(text[0]));
		parent.setOponenName(text[0]);
		
		if(parent.isServer())
			createWelcomeMessage();
	}
	
	/**
	 * spracuje prijat� spr�vu
	 * @param message
	 */
	public void proccessAllRecievedMessages(String message) {
		MessagePart msg = createMessagePart(message.getBytes());

		if(messages.containsKey(msg.getId()))
			messages.get(msg.getId()).recievePart(msg);
		else
			messages.put(msg.getId(), new Message(this, msg));
		
		//skontroluje spravu vdaka crc
		
		//ak je text
		
			//ak existuje sprava s id prid� ju
		
			//ak neexistuje vytvor� ju
		
		//ak je ping
	}
	
	public UDPChat getParent() {return parent;}
	
	//check client status
	//pings

	public void setPingMessage(){
		
	}
	
	public void checkFailedMessages(){
		
	}
}
