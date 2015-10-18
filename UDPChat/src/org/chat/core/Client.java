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
	private UDPChat parent;
	private DatagramSocket socket;
	private Thread listen;
	
	public Client(UDPChat parent) {
		Log.write("zaèal konštruktor objektu Client", Log.CONSTRUCTORS);
		this.parent = parent;
		try {
			socket = new DatagramSocket(parent.getPort() + 1);
			listen();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Log.write("skonèil konštruktor objektu Client", Log.CONSTRUCTORS);
	}

	private void listen(){
		listen = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					try {
						byte[] block = new byte[Config.CHAT_TOTAL_MAX_MSG_SIZE];
						DatagramPacket inpacket = new DatagramPacket(block, block.length);
						socket.receive(inpacket);
						
						proccessMessage(new String(inpacket.getData(), 0, inpacket.getLength()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		listen.start();
	}
	
	private void proccessMessage(String message){
		Log.write("client prijal správu " + message, Log.CONNECTION);
		parent.getMessageManager().proccessMessage(message);
	}
	
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isServer() {return false;}

	@Override
	public void write(String message){
		try {
			DatagramPacket outpacket = getPacket(message, 
												 InetAddress.getByName("localhost"), 
												 parent.getPort()) ;
			
			socket.send(outpacket);
			Log.write("server odoslal správu: " + message, Log.CONNECTION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
