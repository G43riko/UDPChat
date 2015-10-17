package org.chat.message;

import java.util.HashMap;

import org.chat.UDPChat;
import org.chat.utils.IDGenerator;
import org.chat.utils.Log;

public class MessageManager {
	public final static byte MESSAGE_TEXT = 0;
	
	private HashMap<Integer, Message> messages = new HashMap<Integer, Message>();
	private UDPChat parent;
	//id spravy, pocet sprav,cislo spravy, velkos� spr�vy, typ spravy, [velkos� nazvu suboru, nazov suboru]

	public MessageManager(UDPChat parent) {
		Log.write("za�al kon�truktor objektu MessageManager", Log.CONSTRUCTORS);
		this.parent = parent;
		Log.write("skon�il kon�truktor objektu MessageManager", Log.CONSTRUCTORS);
	}

	public void checkFailedMessages(){
		
	}

	public void createMessage(String message) {
		int id = IDGenerator.getId();
		messages.put(id, new Message(message, this, id, MESSAGE_TEXT));
	}

	public UDPChat getParent() {
		return parent;
	}
	
	//check client status
	
	//
}
