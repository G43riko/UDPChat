package org.chat.core;

import org.chat.UDPChat;
import org.chat.utils.Log;

public final class Client implements Connectionable{
	private UDPChat parent;
	
	public Client(UDPChat parent) {
		Log.write("zaèal konštruktor objektu Client", Log.CONSTRUCTORS);
		this.parent = parent;
		Log.write("skonèil konštruktor objektu Client", Log.CONSTRUCTORS);
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isServer() {return false;}

}
