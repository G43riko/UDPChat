package org.chat.message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.chat.UDPChat;
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
		int order = Utils.getInt(Arrays.copyOfRange(data, 8, 12));
		if(data[12] == MESSAGE_FILE && order == 0){
			int fileNameLengh = Utils.getInt(Arrays.copyOfRange(data, 13, 17));
			return new MessagePart(new String(data, 12, data.length - 12),
					   			   new String(Arrays.copyOfRange(data, 17, 17 + fileNameLengh)),
					   			   Utils.getInt(Arrays.copyOfRange(data, 0, 4)), 
					   			   Utils.getInt(Arrays.copyOfRange(data, 4, 8)), 
					   			   order,
					   			   data[12]);
		}
		else
			return new MessagePart(new String(data, 12, data.length - 12),
								   null,
								   Utils.getInt(Arrays.copyOfRange(data, 0, 4)), 
								   Utils.getInt(Arrays.copyOfRange(data, 4, 8)), 
								   order, 
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
	 * vytvor� spr�vu odosielajucu s�bor 
	 * @param file
	 */
	public void createFileMessage(File file) {
		int id = IDGenerator.getId();
		messages.put(id, new Message(file, this, id));
	}
	
	/**
	 * vytvor� nov� uv�taciu spr�vu
	 */
	public void createWelcomeMessage(){
		int id = IDGenerator.getId();
		messages.put(id, new Message(parent.getLogin() + ":" + Utils.getIP(), this,  id,  MESSAGE_WELCOME));
	}
	
	/**
	 * vytvor� rozl��kov� spr�vu
	 */
	public void createLogoutMessage(){
		int id = IDGenerator.getId();
		messages.put(id, new Message("", this,  id,  MESSAGE_LOGOUT));
	}

	
	public void proccessLogoutMessage() {
		if(parent.isServer())
			parent.recieveMessage("u�ivatel " + parent.getOponenName() + " sa odpojil");
		else
			parent.recieveMessage("server u�ivate " + parent.getOponenName() + " bol zru�en�");
		parent.setOponenName(null);
	}
	/**
	 * spracuje prijat� uv�taciu spr�vu
	 * @param message
	 */
	public void proccessWelcomeMessage(String message) {
		String[] text = message.split(":");
		parent.setOponenName(text[0]);
		
		if(parent.isServer()){
			createWelcomeMessage();
			parent.recieveMessage("pripojil sa u�iva�el " + text[0]);
		}
	}
	
	/**
	 * spracuje prijat� spr�vu obsahuj�cu s�bor
	 * @param text
	 * @param fileName
	 */
	public void proccessFileMessage(String text, String fileName) {
		text = text.replace(fileName, "").trim();
		parent.recieveMessage("prijali ste s�bor: " + fileName);
		try {
			File file = new File(fileName);
			FileWriter fw = new FileWriter(file);
			fw.write(text);
			parent.recieveMessage("obsah: " + text);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	}
	
	public UDPChat getParent() {return parent;}
	
	//check client status
	//pings

	public void setPingMessage(){
		
	}
	
	public void checkFailedMessages(){
		
	}
}
