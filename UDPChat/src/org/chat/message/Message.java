package org.chat.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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
			case MessageManager.MESSAGE_FILE :
				parent.proccessFileMessage(getText(), getFileName());
		}
	}

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
	
	private String getFileName(){
		return messages.get(0).getFileName();
	}

	/**
	 * skontroluje èi je správa prijatá
	 * @return
	 */
	private boolean isComplete() {return messages.size() == parts;}
}
