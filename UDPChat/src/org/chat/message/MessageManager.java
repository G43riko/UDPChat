package org.chat.message;

import java.util.Arrays;
import java.util.HashMap;

import org.chat.UDPChat;
import org.chat.utils.IDGenerator;
import org.chat.utils.Log;
import org.chat.utils.Utils;

public class MessageManager {
	public final static byte MESSAGE_TEXT = 0;
	public final static byte MESSAGE_PING_RESPONSE = 1;
	public final static byte MESSAGE_PING_REQUEST = 2;
	public final static byte MESSAGE_FILE = 3;
	
	private HashMap<Integer, Message> messages = new HashMap<Integer, Message>();
	private UDPChat parent;
	//id spravy, pocet sprav,cislo spravy, velkos správy, typ spravy, [velkos nazvu suboru, nazov suboru]

	public MessageManager(UDPChat parent) {
		Log.write("zaèal konštruktor objektu MessageManager", Log.CONSTRUCTORS);
		this.parent = parent;
		Log.write("skonèil konštruktor objektu MessageManager", Log.CONSTRUCTORS);
	}
	
	/**
	 * spracuje správu a vytvorí s nej MessagePart
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
	 * vytvorí novú správu 
	 * @param message
	 */
	public void createMessage(String message) {
		int id = IDGenerator.getId();
		messages.put(id, new Message(message, this, id, MESSAGE_TEXT));
	}

	/**
	 * spracuje prijatú správu
	 * @param message
	 */
	public void proccessMessage(String message) {
		MessagePart msg = createMessagePart(message.getBytes());

		//skontroluje spravu vdaka crc 
		if(messages.containsKey(msg.getId()))
			messages.get(msg.getId()).recievePart(msg);
		else
			messages.put(msg.getId(), new Message(this, msg));
		
		//ak je text
		
			//ak existuje sprava s id pridá ju
		
			//ak neexistuje vytvorı ju
		
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
