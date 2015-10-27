package org.chat.message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.chat.UDPChat;
import org.chat.core.Server;
import org.chat.utils.IDGenerator;
import org.chat.utils.Log;
import org.chat.utils.StringXORer;
import org.chat.utils.Utils;

public class MessageManager {
	public final static byte MESSAGE_TEXT 		= 0;
	public final static byte MESSAGE_PING 		= 1;
	public final static byte MESSAGE_FILE 		= 3;
	public final static byte MESSAGE_LOGOUT 	= 4;
	public final static byte MESSAGE_WELCOME	= 5;
	public final static byte MESSAGE_REPAIR		= 6;
	public final static byte MESSAGE_FOK		= 7;
	public final static byte MESSAGE_FRES		= 8;
	
	private HashMap<Integer, Message> messages = new HashMap<Integer, Message>();
	private UDPChat parent;

	//CONTRUCTORS
	
	public MessageManager(UDPChat parent) {
		Log.write("zaèal konštruktor objektu MessageManager", Log.CONSTRUCTORS);
		this.parent = parent;
		Log.write("skonèil konštruktor objektu MessageManager", Log.CONSTRUCTORS);
	}
	
	//OTHERS
	
	private MessagePart parseHeader(byte[] data){
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
	
	//CREATORS
	
	public void createRepairMessage(int order, int msgID) {
		System.out.println("vytvára sa správa na znovuposlanie id: " + msgID + " order: " + order);
		int id = IDGenerator.getId();
		messages.put(id, new Message(msgID + ":" + order, this, id, MESSAGE_REPAIR));
		
	}
	
	public void createTextMessage(String message) {
		int id = IDGenerator.getId();
		messages.put(id, new Message(message, this, id, MESSAGE_TEXT));
	}
	
	public void createFileMessage(File file) {
		int id = IDGenerator.getId();
		Log.write("MessageManager.createFileMessage: id: " + id, Log.DEBUG);
		messages.put(id, new Message(file, this, id));
//		System.out.println(messages);
	}
	
	public void createWelcomeMessage(){
		int id = IDGenerator.getId();
		messages.put(id, new Message(parent.getLogin() + ":" + Utils.getIP(), this,  id,  MESSAGE_WELCOME));
	}
	
	public void createLogoutMessage(){
		int id = IDGenerator.getId();
		messages.put(id, new Message("", this,  id,  MESSAGE_LOGOUT));
	}

	public void createPingMessage() {
		int id = IDGenerator.getId();
		messages.put(id, new Message("", this,  id,  MESSAGE_PING));
		Log.write("odosiela sa pingovacia správa", Log.PING_MESSAGE);
	}
	
	//PROCCESSORS
	
	public void proccessLogoutMessage() {
//		createLogoutMessage();
		if(parent.isServer())
			parent.recieveMessage("uživatel " + parent.getOponentName() + " sa odpojil");
		else
			parent.recieveMessage("server uživate " + parent.getOponentName() + " bol zrušený");
		parent.setOponenName(null);
	}
	
	public void proccessWelcomeMessage(String message) {
		String[] text = message.split(":");
		parent.setOponenName(text[0]);
		parent.setIp(text[1]);
		if(parent.isServer()){
			createWelcomeMessage();
			((Server)parent.getConnection()).startMessageChecking();
			parent.recieveMessage("pripojil sa uživaťel " + text[0]);
		}
	}
	
	public void proccessFileMessage(String text, String fileName) {
		text = text.replace(fileName, "").trim();
		parent.recieveMessage("prijali ste súbor: " + fileName);
		try {
			String message = "bol priajatý súbor " + fileName + ", želáte si ho uloži�??";
			JFileChooser fc = new JFileChooser();
			if(JOptionPane.showConfirmDialog(parent.getGui(), message) == JOptionPane.YES_OPTION){
				fc.showSaveDialog(parent.getGui());
				File file = fc.getSelectedFile();
				FileWriter fw = new FileWriter(file);
				fw.write(text);
				fw.close();
			}
				
		} catch (IOException e) {
			Log.write("nepodarilo sa spracova� prijatý súbor", e, Log.EXCEPTIONS);
		}
	}
	
	public void proccessAllRecievedMessages(String message) {
		int crc = Utils.getInt(message.substring(message.length() - 4, message.length()).getBytes());
		message = message.substring(0, message.length() - 4);
		String finalMSG = StringXORer.decode(message, crc);
		
		if(finalMSG == null){
			int order = Utils.getInt(Arrays.copyOfRange(message.getBytes(), 8, 12));
			int id = Utils.getInt(Arrays.copyOfRange(message.getBytes(), 0, 4));
			
			if(message.getBytes()[12] != MESSAGE_REPAIR)
				System.out.println("chyba v sprave");
				createRepairMessage(order, id);
			return;
		}
		
		MessagePart msg = parseHeader(finalMSG.getBytes());
		
		if(msg.getType() == MESSAGE_FILE)
			if(msg.getOrder() + 1 < msg.getNumber()){
				System.out.println("príjala sa sprava: " + msg.getOrder() + " a pýta sa dalšia");
				createRepairMessage(msg.getOrder()+1, msg.getId());
			}
		
		if(messages.containsKey(msg.getId()))
			messages.get(msg.getId()).recievePart(msg);
		else
			messages.put(msg.getId(), new Message(this, msg));
	}

	public void proccessRepairMessage(String message){
		String[] content = message.split(":");
//		System.out.println(message);
//		System.out.println(messages);
//		System.out.println("znova sa odosiela správa: " + message + " " + content[0] + " " + content[1]);
//		System.out.println(messages);
		try{
		messages.get(Integer.parseInt(content[0])).resend(Integer.parseInt(content[1]));
		}
		catch(NullPointerException e){
			System.out.println("nepodarilo sa reodoslať: " + message + " lebo sa nachadza: " + messages.containsKey(Integer.parseInt(content[0])));
		}
	}
	
	public void proccessPingMessage() {
		parent.getConnection().setLastContact(System.currentTimeMillis());
		Log.write("bola prijatá pingovacia správa", Log.PING_MESSAGE);
		
		if(!parent.isServer())
			createPingMessage();
	}

	//GETTERS
	
	public UDPChat getParent() {return parent;}
}
