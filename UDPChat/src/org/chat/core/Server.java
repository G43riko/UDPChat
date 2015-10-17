package org.chat.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.chat.UDPChat;
import org.chat.utils.Log;

public final class Server implements Connectionable{
	public static final byte CLIENT_SEND_MSG = 0;
	public static final byte CLIENT_DISCONNECT = 1;
	public static final byte CLIENT_CONNECT = 2;

	private DatagramSocket socket;
	private UDPChat parent;
	Thread listen;
	
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
		// TODO Auto-generated method stub
		
	}
	
	private void listen(){
		listen = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					byte[] block = new byte[parent.getMaxMsgLenght()];
					DatagramPacket inpacket = new DatagramPacket(block, block.length);
					socket.receive(inpacket);
					proccessMessage(new String(inpacket.getData(), 0, inpacket.getLength()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		listen.start();
	}
	
	private void proccessMessage(String message){
	}
	
	public DatagramPacket getPacket(String message, int maxSize){
		try {
			byte[] block = new byte[maxSize];
			DatagramPacket inpacket = new DatagramPacket(block, block.length);
			socket.receive(inpacket);
			String inmessage = new String(inpacket.getData(), 0, inpacket.getLength());
			
			
			
			String outmessage = "prišla správa: " + inmessage;
			DatagramPacket outpacket = getPacket(outmessage, inpacket.getAddress(), inpacket.getPort());
			socket.send(outpacket);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isServer() {return true;}
}
