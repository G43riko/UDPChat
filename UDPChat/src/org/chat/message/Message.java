package org.chat.message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import org.chat.Config;
import org.chat.utils.IDGenerator;
import org.chat.utils.Log;
import org.chat.utils.StringXORer;
import org.chat.utils.Utils;

public class Message {
	private HashMap<Integer, MessagePart> messages = new HashMap<Integer, MessagePart>();
	private MessageManager parent;
	private int id;
	private int parts;
	private String fileName;
	private int maxFileOrder = 0;
	
	//CONSTRUCTORS
	
	public Message(MessageManager parent, MessagePart msg){
		Log.write("zaèal konštruktor objektu Message", Log.CONSTRUCTORS);
		this.parent = parent;
		parts = msg.getNumber();
		id = msg.getId();
		
		
		recievePart(msg);
		Log.write("skonèil konštruktor objektu Message", Log.CONSTRUCTORS);
	}

	public Message(String message, MessageManager parent, int id, byte messageType){
		this.parent = parent;
		this.id = id;
		
		ArrayList<String> msgs = divideMessage(message, parent.getParent().getMaxMsgLenght());
		parts = msgs.size();
		for(int i=0 ; i<parts ; i++){
			MessagePart msg = new MessagePart(msgs.get(i), null, id, msgs.size(), i, messageType);
			messages.put(i, msg);
			encodeAndSend(msg);
		}
	}
	
	public Message(File file, MessageManager parent, int id) {
		this.parent = parent;
		this.id = id;
		
		Log.write("Message.Message(File,MessageManager,int): id je:" + id, Log.DEBUG);
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
//				Utils.sleep(100);
			}
			encodeAndSend(messages.get(0));
			System.out.println("odosila sa prva cast zo suboru s id: "+ messages.get(0).getId());
//			Thread t = new Thread(new Runnable(){
//				public void run() {
//					new HashMap<Integer, MessagePart>(messages).entrySet().stream().forEach(a->encodeAndSend(a.getValue()));	
//				}
//			});
//			t.start();
		} catch (IOException e) {
			Log.write("nepodarilo sa vytvoriš správu pre odoslanie súboru", e, Log.EXCEPTIONS);
		} 
	}

	private void encodeAndSend(MessagePart part){
		part.increaseSendTimes();
		
		if(part.getSendTimes() >= Config.CHAT_MAX_SEND_NUMBER)
			return;
		
		int msgID = IDGenerator.getId();
		String finalMessage = StringXORer.encode(new String(part.getData()), msgID);
		finalMessage += new String(Utils.getByteArray(msgID));
		parent.getParent().getConnection().write(finalMessage);
	};
	
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
				break;
			case MessageManager.MESSAGE_REPAIR :
				parent.proccessRepairMessage(getText());
				break;
			case MessageManager.MESSAGE_FINISH :
				parent.proccessFinishMessage(getText());
				break;
		}
	}
	
	private ArrayList<String> divideMessage(String message, int maxLength){
//		Log.write("Message.divideMessage: delí sa správa: " + message, Log.DEBUG);
		ArrayList<String> result = new ArrayList<String>();
		while(message.length() > maxLength){
			result.add(message.substring(0, maxLength));
			message = message.substring(maxLength, message.length());
		}
		result.add(message);
		return result;
	}
	
	public void recievePart(MessagePart msg) {
		if(msg.getType() == MessageManager.MESSAGE_FILE){
//			System.out.println("bola prijatá " + messages.size() + "ta správa s " + parts + " s order: " + msg.getOrder());
//			for(int i=msg.getOrder() ; i>=0 ; i--)
//				if(!messages.containsKey(i))
//					parent.createRepairMessage(i, msg.getId());
			
			
			if(maxFileOrder+1 < parts)
				parent.createRepairMessage(++maxFileOrder, msg.getId(), msg.getType());
		}
		
		
		
		if(messages.containsKey(msg.getOrder()))
			return;
					
		messages.put(msg.getOrder(), msg);
		
		if(msg.getType() == MessageManager.MESSAGE_FILE && msg.getOrder() == 0)
			this.fileName = msg.getFileName();
		
		if(isComplete())
			messageProccess();
	}

	private String getText() {
		String res = messages.get(0).getText();
		for(int i=1 ; i<parts ; i++)
			res += messages.get(i).getText();
		return res;
	}
	
	private boolean isComplete() {
		return messages.size() == parts;
	}
	public int getId() {return id;}

	public void resend(int order) {
		encodeAndSend(messages.get(order));
	}
	@Override
	public String toString() {
		return "id: " + id +", parts: " + parts;
	}

	public boolean hasMessage(MessagePart msg) {
		return messages.containsKey(msg.getOrder());
	}

	public int getSize() {
		return messages.size();
	}

	public void wasSendSuccesfully(Integer num) {
		if(messages.containsKey(num))
			messages.get(num).setOkey();
		
		if(num == parts-1)
			for(int i=0 ; i<messages.size() ; i++)
				if(!messages.get(i).isOkey()){
					System.out.println("doposielasa správa s id: " + messages.get(i).getOrder());
					encodeAndSend(messages.get(i));
				}
	}
}
