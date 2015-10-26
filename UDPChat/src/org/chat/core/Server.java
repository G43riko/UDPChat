package org.chat.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.chat.Config;
import org.chat.UDPChat;
import org.chat.utils.Log;

public final class Server implements Connectionable{
	public static final byte CLIENT_SEND_MSG = 0;
	public static final byte CLIENT_DISCONNECT = 1;
	public static final byte CLIENT_CONNECT = 2;
	public static final byte CLIENT_PING = 3;
	public static final byte SERVER_PING = 4;
	public static final byte CLIENT_RECIEVE_MSG_OK = 5;
	public static final byte CLIENT_RECIEVE_MSG_ERROR = 6;

	private DatagramSocket 	socket;
	private UDPChat 		parent;
	private Thread 			listen;
	private boolean 		running 	= true;
	private long 			requestTime = 0;
	private long 			lastContact;
	
	
	public Server(UDPChat parent) {
		Log.write("zaËal konötruktor objektu Server", Log.CONSTRUCTORS);
		this.parent = parent;
		
		try {
			socket = new DatagramSocket(parent.getPort());
			listen();
		} catch (SocketException e) {
			Log.write("Nepodarilo sa vytvoriù server socket", e, Log.EXCEPTIONS);
		}
		Log.write("skonËil konötruktor objektu Server", Log.CONSTRUCTORS);
	}

	public void checkConnection(){
		if(System.currentTimeMillis() - lastContact > Config.PING_CHECKING_TIME){
			if(requestTime == 0){
				requestTime = System.currentTimeMillis();
				parent.getMessageManager().createPingMessage();
			}
			else if(System.currentTimeMillis() - requestTime > Config.PING_WAITING_TIME){
				parent.oponenetDisconect();
				requestTime = 0;
			}
		}
		else
			if(requestTime > 0)
				requestTime = 0;
	}

	public void stop() {
		running = false;
		socket.close();
	}
	
	public void write(String message){
		try {
			DatagramPacket outpacket = getPacket(message, 
												 InetAddress.getByName("localhost"), 
												 parent.getPort() + 1) ;
			
			socket.send(outpacket);
			Log.write("server odoslal spr·vu: " + message, Log.CONNECTION);
		} catch (IOException e) {
			Log.write("zo servera sa epodarilo odoslaù spr·vu: " + message, e, Log.EXCEPTIONS);
		}
	}
	
	private void listen(){
		listen = new Thread(new Runnable(){
			@Override
			public void run() {
				while(running){
					try {
						byte[] block = new byte[Config.CHAT_TOTAL_MAX_MSG_SIZE];
						DatagramPacket inpacket = new DatagramPacket(block, Config.CHAT_TOTAL_MAX_MSG_SIZE);
						socket.receive(inpacket);
						
						proccessMessage(new String(inpacket.getData(), 0, inpacket.getLength()));
					} catch (IOException e) {
						Log.write("Server socket bol zatvoren˝", e ,Log.EXCEPTIONS);
					}
				}
			}
		});
		
		listen.start();
	}
	
	private void proccessMessage(String message){
		Log.write("server prijal spr·vu " + message, Log.CONNECTION);
		
		parent.getMessageManager().proccessAllRecievedMessages(message);
	}

	//GETTERS
	
	@Override
	public boolean isServer() {return true;}
	public long getLastContact() {return lastContact;}
	
	//SETTERS
	
	@Override
	public void setLastContact(long lastContact) {
		this.lastContact = lastContact;
	}
}
