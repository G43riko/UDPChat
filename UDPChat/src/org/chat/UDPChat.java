package org.chat;

import org.chat.core.Client;
import org.chat.core.Connectionable;
import org.chat.core.Server;
import org.chat.gui.Gui;
import org.chat.message.MessageManager;
import org.chat.utils.Log;

public class UDPChat {
	private Gui gui = new Gui(this); 
	private Connectionable connection;
	private boolean connected;
	
	private MessageManager messages = new MessageManager(this);
	
	private String login;
	private String port;
	private String ip;
	private int maxMsgLenght = Config.CHAT_DEFAULT_MSG_SIZE;

	public UDPChat(){
		Log.write("začal konštruktor objektu UDPChat", Log.CONSTRUCTORS);
		
		gui.showLoginView();
		//messages.createMessage("abcdefghe");
		
		Log.write("skončil konštruktor objektu UDPChat", Log.CONSTRUCTORS);
	}

	public void stop(boolean sayToServer) {
		connected = false;
		
		if(sayToServer)
			sendMessage("", Server.CLIENT_DISCONNECT);
		
		gui.showLoginView();

		connection.stop();
	}

	public void start(String login, String ip, String port, boolean isHost) {
		this.login = login;
		this.ip = ip;
		this.port = port;
		
		connection = isHost ? new Server(this) : new Client(this); 
		
		sendMessage("", Server.CLIENT_CONNECT);
		
		gui.showChatView(login);
	}

	public void recieveMessage(String message){
		gui.appendText(message);
	}
	
	public void sendMessage(String text, byte type) {
		if(text.isEmpty())
			return;
		gui.appendText(text);
		messages.createMessage(text);
		//connection.write(text);
	}
	
	public MessageManager getMessageManager() {return messages;}

	public boolean isConnected() {return connected;}
	public boolean isServer(){return connection.isServer();}

	public int getMaxMsgLenght() {return maxMsgLenght;}
	public int getPort() {return Integer.valueOf(port);}
	public String getLogin() {return login;}
	public String getIp() {return ip;}
	public Connectionable getConnection() {return connection;}
}
