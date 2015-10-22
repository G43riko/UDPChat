package org.chat.message;

import org.chat.utils.Utils;

public class MessagePart {
	private int 	id;
	private int 	messagesNumber;
	private int 	messageOrder;
	private int 	messageSize;
	private byte 	messageType;
	private byte[] 	data;
	private String 	text;
	private String 	qrc;
	private String 	fileName;
	private boolean okey = false;
	private long 	sendTime = System.currentTimeMillis();
	//boolean send;
	
	//CONSTRUCTORS

	public MessagePart(String text, String fileName, int id, int messagesNumber, int messageOrder, byte messageType) {
		this.messagesNumber = messagesNumber;
		this.messageOrder = messageOrder;
		this.messageType = messageType;
		this.fileName = fileName;
		this.text = text;
		this.id = id;
		
		System.out.println("n·zov s˙boru je: " + fileName);
		data = createHeader(id, messagesNumber, messageOrder, messageType, fileName);
		if(text != null)
			data = Utils.concatenate(data, text.getBytes());
	}

	//id spravy, pocet sprav,cislo spravy, velkosù spr·vy, typ spravy, [velkosù nazvu suboru, nazov suboru]

//	@Override
//	public String toString() {
//		return messageType + " - " + messageOrder + "/" + messagesNumber + " : " + text;
//	}
	
//	
//	public MessagePart(String text, String name, int id, int size, int order, byte messageFile) {
//		this.messagesNumber = messagesNumber;
//		this.messageOrder = messageOrder;
//		this.messageType = messageType;
//		this.text = text;
//		this.id = id;
//	}

	/**
	 * posklad· hlaviËku s ˙dajov v argumente
	 * @param id
	 * @param messagesNumber
	 * @param messageOrder
	 * @param type
	 * @param filename
	 * @return
	 */
	private  byte[] createHeader(int id, int messagesNumber, int messageOrder, byte type, String filename){
		//XIDXXMNXXMOXTXFLXfilenameDATA
		byte[]result = Utils.concatenate(Utils.getByteArray(id), Utils.getByteArray(messagesNumber));
			  result = Utils.concatenate(result, Utils.getByteArray(messageOrder));
			  result = Utils.concatenate(result, new byte[]{type});
		if(fileName != null){
			result = Utils.concatenate(result, Utils.getByteArray(filename.length()));
			result = Utils.concatenate(result, filename.getBytes());
		}
		
		return result;
	}
	
	//GETTERS

	public String getText() {return text.substring(1, text.length());}
	public String getFileName() {return fileName;}
	public int getId() {return id;}
	public int getOrder() {return messageOrder;}
	public int getNumber() {return messagesNumber;}
	public byte[] getData() {return data;}
	public byte getType() {return messageType;}

}

