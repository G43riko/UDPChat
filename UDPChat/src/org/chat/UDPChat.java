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
//	private Server server;
//	private Client client;
	private boolean connected;
	
	private MessageManager messages = new MessageManager(this);
	
	private String login;
	private String port;
	private String ip;
	private int maxMsgLenght = 7;

	public UDPChat(){
		Log.write("zaèal konštruktor objektu UDPChat", Log.CONSTRUCTORS);
		gui.showLoginView();
		messages.createMessage("abcdefghe");
		Log.write("skonèil konštruktor objektu UDPChat", Log.CONSTRUCTORS);
	}

	public void stop(boolean sayToServer) {
		connected = false;
		
		if(sayToServer)
			sendMessage("", Server.CLIENT_DISCONNECT);
		
		gui.showLoginView();

		connection.stop();
//		if(isServer())
//			server.stop();
//
//		client.stop();
	}

	public void start(String login, String ip, String port, boolean isHost) {
		this.login = login;
		this.ip = ip;
		this.port = port;
		if(isHost){
			//server = new Server(this);
			connection = new Server(this);
			ip = "localhost";
		}
		else
			connection = new Client(this);
		//client = new Client(this);
		
		sendMessage("", Server.CLIENT_CONNECT);
		
		gui.showChatView(login);
	}

	public void sendMessage(String text, byte clientSendMsg) {
		// TODO Auto-generated method stub
	}
	
	public boolean isConnected() {return connected;}
	//public boolean isServer(){return server != null;}
	public boolean isServer(){return connection.isServer();}

	public int getMaxMsgLenght() {return maxMsgLenght;}
	public int getPort() {return Integer.valueOf(port);}
	public String getLogin() {return login;}
	public String getIp() {return ip;}
}
