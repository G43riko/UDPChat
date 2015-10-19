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

	private DatagramSocket socket;
	private UDPChat parent;
	private Thread listen;
	private boolean running = true;
	
	
	public Server(UDPChat parent) {
		Log.write("zaèal konštruktor objektu Server", Log.CONSTRUCTORS);
		this.parent = parent;
		
		try {
			socket = new DatagramSocket(parent.getPort());
			listen();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		Log.write("skonèil konštruktor objektu Server", Log.CONSTRUCTORS);
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
			Log.write("server odoslal správu: " + message, Log.CONNECTION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void listen(){
		listen = new Thread(new Runnable(){
			@Override
			public void run() {
				while(running){
					try {
						byte[] block = new byte[Config.CHAT_TOTAL_MAX_MSG_SIZE];
						DatagramPacket inpacket = new DatagramPacket(block, block.length);
						socket.receive(inpacket);
						
						proccessMessage(new String(inpacket.getData(), 0, inpacket.getLength()));
					} catch (IOException e) {
						Log.write("Server socket bol zatvorený", Log.EXCEPTIONS);
					}
				}
			}
		});
		
		listen.start();
	}
	
	private void proccessMessage(String message){
		Log.write("server prijal správu " + message, Log.CONNECTION);
		parent.getMessageManager().proccessAllRecievedMessages(message);
	}

	@Override
	public boolean isServer() {return true;}
}
