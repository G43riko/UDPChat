package org.chat.message;

import java.util.ArrayList;
import java.util.HashMap;

import org.chat.utils.IDGenerator;

public class Message {
	private HashMap<Integer, MessagePart> messages = new HashMap<Integer, MessagePart>();
	private MessageManager parent;
	private int id;
	
	public int getId() {
		return id;
	}

	public Message(String message, MessageManager parent, int id, byte messageType){
		this.parent = parent;
		this.id = id;
		ArrayList<String> msgs = divideMessage(message);
		for(int i=0 ; i<msgs.size() ; i++){
			int ide = IDGenerator.getId();
			messages.put(ide, new MessagePart(msgs.get(i), ide, msgs.size(), i, messageType));
		}
	}
	
	private ArrayList<String> divideMessage(String message){
		ArrayList<String> result = new ArrayList<String>();
		while(message.length() > parent.getParent().getMaxMsgLenght()){
			result.add(message.substring(0, parent.getParent().getMaxMsgLenght()));
			message = message.substring(parent.getParent().getMaxMsgLenght(), message.length());
		}
		result.add(message);
		return result;
	}
	@Override
	public String toString() {
		return messages.toString();
	}
}
