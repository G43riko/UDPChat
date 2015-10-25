package org.chat.message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class Message {
	private HashMap<Integer, MessagePart> messages = new HashMap<Integer, MessagePart>();
	private MessageManager parent;
	private int id;
	private int parts;
	private String fileName;
	
	//CONSTRUCTORS
	
	/**
	 * vytvorenie pr�jmajucej spr�vy ktorej je jedno o ak� obsah sa 
	 * @param parent
	 * @param msg
	 */
	public Message(MessageManager parent, MessagePart msg){
		this.parent = parent;
		parts = msg.getNumber();
		id = msg.getId();
		
		recievePart(msg);
	}

	/**
	 * kon�truktor pre vytvorenie spr�vy odosielaj�cej text
	 * @param message
	 * @param parent
	 * @param id
	 * @param messageType
	 */
	public Message(String message, MessageManager parent, int id, byte messageType){
		this.parent = parent;
		this.id = id;
		
		ArrayList<String> msgs = divideMessage(message, parent.getParent().getMaxMsgLenght());
		parts = msgs.size();
		for(int i=0 ; i<parts ; i++){
			MessagePart msg = new MessagePart(msgs.get(i), null, id, msgs.size(), i, messageType);
			messages.put(i, msg);
			parent.getParent().getConnection().write(new String(msg.getData()));
		}
	}
	
	/**
	 * kon�truktor pre vytvorenie spr�vy odosielaj�cej s�bor
	 * @param file
	 * @param parent
	 * @param id
	 */
	public Message(File file, MessageManager parent, int id) {
		this.parent = parent;
		this.id = id;
		try {
			byte[] data = Files.readAllBytes(file.toPath());
			
			ArrayList<String> msgs = divideMessage(new String(data), parent.getParent().getMaxMsgLenght());
			parts = msgs.size();
			for(int i=0 ; i<parts ; i++){
				MessagePart msg = new MessagePart(msgs.get(i),
												  i == 0 ? file.getName() : null, 
												  id, 
												  msgs.size(), 
												  i, 
												  MessageManager.MESSAGE_FILE);
				messages.put(i, msg);
				parent.getParent().getConnection().write(new String(msg.getData()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	
	/**
	 * spracuje kompletn� spr�vu po prijat�
	 */
	private void messageProccess() {
		switch(messages.get(0).getType()){
			case MessageManager.MESSAGE_TEXT :
				parent.getParent().recieveMessage(getText());				
				break;
			case MessageManager.MESSAGE_WELCOME :
				parent.proccessWelcomeMessage(getText());
				break;
			case MessageManager.MESSAGE_FILE :
				parent.proccessFileMessage(getText(), fileName);
				break;
			case MessageManager.MESSAGE_LOGOUT :
				parent.proccessLogoutMessage();
				break;
			case MessageManager.MESSAGE_PING :
				parent.proccessPingMessage();
		}
	}
	
	/**
	 * Rozdel� hlavi�ku podla velkos�i uvedenej v argumente
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
	 * Spracuje prijat� spr�vu
	 * @param msg
	 */
	public void recievePart(MessagePart msg) {
		if(!messages.containsKey(msg.getOrder()))
			messages.put(msg.getOrder(), msg);
		
		if(msg.getType() == MessageManager.MESSAGE_FILE && msg.getOrder() == 0)
			this.fileName = msg.getFileName();
		
		if(isComplete())
			messageProccess();
	}

	/**
	 * Vr�ti text spr�vy zlo�en�m v�etk�ch �ast� spr�v 
	 * @return
	 */
	private String getText() {
		String res = messages.get(0).getText();
		for(int i=1 ; i<parts ; i++)
			res += messages.get(i).getText();
		return res;
	}

	/**
	 * skontroluje �i je spr�va prijat�
	 * @return
	 */
	private boolean isComplete() {return messages.size() == parts;}
	public int getId() {return id;}
}
