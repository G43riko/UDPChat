package org.chat.core;

public class User {
	private String login;
	private long lastPing = System.currentTimeMillis();
	
	public User(String login) {
		this.login = login;
	}
	
}
