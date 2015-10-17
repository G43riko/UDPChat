package org.chat.core;

import org.chat.UDPChat;
import org.chat.utils.Log;

public final class Client implements Connectionable{
	private UDPChat parent;
	
	public Client(UDPChat parent) {
		Log.write("začal konštruktor objektu Client", Log.CONSTRUCTORS);
		this.parent = parent;
		Log.write("skončil konštruktor objektu Client", Log.CONSTRUCTORS);
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isServer() {return false;}

}
