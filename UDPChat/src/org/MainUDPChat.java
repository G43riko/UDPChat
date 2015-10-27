package org;

import org.chat.UDPChat;

public class MainUDPChat {
	public static void main(String[] args) {
//		String res = StringXORer.decode("a b", 123);
//		System.out.println(res);
		new UDPChat();
	}
	/*
	 * TODO
	 * +decode CRC
	 * +encode CRC
	 * +segmentation
	 * +decode header
	 * +encode header
	 * +login gui
	 * +chat gui
	 * +message sending
	 * +message recieving
	 * +file sending
	 * +file recieving
	 * +ping
	 * +logging system
	 * +logout
	 * +save recieved file
	 * +connection out of localhost
	 * +message checking
	 * +repeating wrong messages
	 * -confirm recieved messages
	 */
}
