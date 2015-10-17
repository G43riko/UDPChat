package org.chat.message;

public class MessagePart {
	private int id;
	private int messagesNumber;
	private int messageOrder;
	private int messageSize;
	private byte messageType;
	private String text;
	private String qrc;
	private String fileName;
	private boolean okey = false;
	//boolean send;

	public MessagePart(String text, int id, int messagesNumber, int messageOrder, byte messageType) {
		this.messagesNumber = messagesNumber;
		this.messageOrder = messageOrder;
		this.messageType = messageType;
		this.text = text;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return messageOrder + "/" + messagesNumber + " : " + text;
	}
	//id spravy, pocet sprav,cislo spravy, velkosù spr·vy, typ spravy, [velkosù nazvu suboru, nazov suboru]
	
}
