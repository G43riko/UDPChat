package org.chat;

import java.io.File;

import org.chat.core.Client;
import org.chat.core.Connectionable;
import org.chat.core.Server;
import org.chat.gui.Gui;
import org.chat.message.MessageManager;
import org.chat.utils.Log;

public class UDPChat {
	private Gui 			gui 		= new Gui(this); 
	private MessageManager 	messages 	= new MessageManager(this);
	private Connectionable 	connection;
	private Controller		controller	= new Controller(this);
	
	private String 	oponenName;
	private String 	login;
	private String 	port;
	private String 	ip;

	//CONTRUCTORS
	
	public UDPChat(){
		Log.write("zaèal konštruktor objektu UDPChat", Log.CONSTRUCTORS);
		Log.write("skonèil konštruktor objektu UDPChat", Log.CONSTRUCTORS);
	}

	//OTHERS
	
	public void stop() {
		messages.createLogoutMessage();
		
		gui.showLoginView();

		connection.stop();
		connection = null;
		controller.stop();
		Log.printLogs();
	}

	public void start(String login, String ip, String port, boolean isHost) {
		this.login = login;
		this.port  = port;
		this.ip    = ip;
		connection = isHost ? new Server(this) : new Client(this); 
		
		if(!isHost)
			messages.createWelcomeMessage();
		
		gui.showChatView(login);
		controller.start();
	}
	
	public void oponenetDisconect() {
		Log.write("uživatel bol odpojený kvoli neaktivity", Log.PING_MESSAGE);
		stop();
	}

	public void recieveMessage(String message){
		connection.setLastContact(System.currentTimeMillis());
		gui.appendText(message, true);
	}
	
	public void sendMessage(String text, byte type) {
		if(text.isEmpty())
			return;
		gui.appendText(text, false);
		messages.createTextMessage(text);
	}

	public void sendFile(File file) {
		gui.appendText("Odoslal sa súbor: " + file.getName(), false);
		messages.createFileMessage(file);
	}
	
	//GETTERS
	
	public boolean isConnected() {return oponenName != null;}
	public boolean isServer(){return connection.isServer();}

	public int getMaxMsgLenght() {return gui.getMaxSize();}
	public int getPort() {return Integer.valueOf(port);}
	public String getOponenName() {return oponenName;}
	public String getLogin() {return login;}
	public String getIp() {return ip;}
	public Connectionable getConnection() {return connection;}
	public MessageManager getMessageManager() {return messages;}
	public Gui getGui() {return gui;}

	//SETTERS
	
	public void setOponenName(String oponenName) {this.oponenName = oponenName;}
	
}
