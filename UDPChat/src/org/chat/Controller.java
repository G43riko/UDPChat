package org.chat;

import org.chat.core.Server;
import org.chat.utils.Utils;

public class Controller {
	private Thread controller;
	private UDPChat parent;
	
	private long lastServerLoop = System.currentTimeMillis();
	private long lastMessagesLoop = System.currentTimeMillis();
	private boolean running = true;
	
	public Controller(UDPChat parent){
		this.parent = parent;
	}
	
	
	public void start(){
		controller = new Thread(new Runnable(){
			public void run() {
				while(running){
					if(parent.isConnected()){
						if(parent.isServer() && System.currentTimeMillis() - lastServerLoop > Config.PING_LOOP_FREQUENCY){
							lastServerLoop = System.currentTimeMillis();
							((Server)parent.getConnection()).checkConnection();
						}
						
						if(System.currentTimeMillis() - lastMessagesLoop > Config.MESSAGES_LOOP_FREQUENCY){
							lastMessagesLoop = System.currentTimeMillis();
							parent.getMessageManager().checkMessages();
						}
					}
					Utils.sleep(Math.min(Config.MESSAGES_LOOP_FREQUENCY, Config.PING_LOOP_FREQUENCY));
				}
			}
		});
		controller.start();
	}


	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
