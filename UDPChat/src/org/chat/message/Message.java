package org.chat.message;

import java.util.ArrayList;
import java.util.HashMap;

import org.chat.Config;
import org.chat.utils.Utils;

public class Message {
	private HashMap<Integer, MessagePart> messages = new HashMap<Integer, MessagePart>();
	private MessageManager parent;
	private int id;
	private int parts;
	
	public int getId() {
		return id;
	}
	
	public Message(MessageManager parent, MessagePart part){
		this.parent = parent;
		messages.put(part.getOrder(), part);
		parts = part.getNumber();
		id = part.getId();
		
		if(isComplete())
			messageProccess();
		
	}
	
	/**
	 * spracuje kompletnú správu po prijatí
	 */
	private void messageProccess() {
		switch(messages.get(0).getType()){
			case MessageManager.MESSAGE_TEXT :
				parent.getParent().recieveMessage(getText());				
				break;
			case MessageManager.MESSAGE_WELCOME :
				parent.proccessWelcomeMessage(getText());
				break;
		}
	}

	public Message(String message, MessageManager parent, int id, byte messageType){
		this.parent = parent;
		this.id = id;
		
		ArrayList<String> msgs = divideMessage(message, parent.getParent().getMaxMsgLenght());
		parts = msgs.size();
		for(int i=0 ; i<parts ; i++){
			MessagePart msg = new MessagePart(msgs.get(i), id, msgs.size(), i, messageType);
			messages.put(i, msg);
			Utils.sleep(100);
			parent.getParent().getConnection().write(new String(msg.getData()));
		}
	}
	
	/**
	 * Rozdelí hlavièku podla velkosi uvedenej v argumente
	 * @param message
	 * @param maxLength
	 * @return
	 */
	private ArrayList<String> divideMessage(String message, int maxLength){
		ArrayList<String> result = new ArrayList<String>();
		while(message.length() > maxLength){
			result.add(message.substring(0, maxLength));
			message = message.substring(maxLength, message.length());
		}
		result.add(message);
		return result;
	}
	
	/**
	 * Spracuje prijatú správu
	 * @param msg
	 */
	public void recievePart(MessagePart msg) {
		if(!messages.containsKey(msg.getOrder()))
			messages.put(msg.getOrder(), msg);
		
		if(isComplete())
			messageProccess();
	}

	/**
	 * Vráti text správy zložením všetkých èastí správ 
	 * @return
	 */
	private String getText() {
		String res = messages.get(0).getText();
		for(int i=1 ; i<parts ; i++)
			res += messages.get(i).getText();
		return res;
	}

	/**
	 * skontroluje èi je správa prijatá
	 * @return
	 */
	private boolean isComplete() {return messages.size() == parts;}
}
