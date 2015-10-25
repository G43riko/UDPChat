package org.chat.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.chat.Config;
import org.chat.UDPChat;
import org.chat.utils.Log;

public final class Client implements Connectionable{
	private UDPChat 		parent;
	private DatagramSocket 	socket;
	private Thread 			listen;
	private boolean 		running 	= true;
	public long 			lastContact = System.currentTimeMillis();
	
	public Client(UDPChat parent) {
		Log.write("zaËal konötruktor objektu Client", Log.CONSTRUCTORS);
		this.parent = parent;
		try {
			socket = new DatagramSocket(parent.getPort() + 1);
			listen();
		} catch (SocketException e) {
			Log.write("Nepodarilo sa vytvoriù client socket", e, Log.EXCEPTIONS);
		}
		Log.write("skonËil konötruktor objektu Client", Log.CONSTRUCTORS);
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
						Log.write("Client socket bol zatvoren˝",e,  Log.EXCEPTIONS);
					}
				}
			}
		});
		
		listen.start();
	}
	
	private void proccessMessage(String message){
		Log.write("client prijal spr·vu " + message, Log.CONNECTION);
		parent.getMessageManager().proccessAllRecievedMessages(message);
	}
	
	public void stop() {
		running = false;
		socket.close();
		socket = null;
	}


	@Override
	public void write(String message){
		try {
			DatagramPacket outpacket = getPacket(message, 
												 InetAddress.getByName("localhost"), 
												 parent.getPort()) ;
			
			socket.send(outpacket);
			Log.write("client odoslal spr·vu: " + message, Log.CONNECTION);
		} catch (IOException e) {
			Log.write("s klienta sa epodarilo odoslaù spr·vu: " + message, e, Log.EXCEPTIONS);
		}
	}
	
	@Override
	public void setLastContact(long lastContact) {this.lastContact = lastContact;}

	@Override
	public boolean isServer() {return false;}
	public long getLastContact() {return lastContact;}

}
