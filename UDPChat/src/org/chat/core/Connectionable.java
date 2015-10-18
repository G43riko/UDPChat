package org.chat.core;

import java.net.DatagramPacket;
import java.net.InetAddress;

public interface Connectionable {
	public void stop();
	public boolean isServer();
	public void write(String message);
//	public void write(byte[] message);
	
	public default DatagramPacket getPacket(String message, InetAddress address, int port){
		return new DatagramPacket(message.getBytes(), 
								 message.length(), 
								 address, 
								 port);
	}
	
}

