package com.unhappyfrogs.rcdroid;

import java.io.IOException;

public interface MessageHandler {
	
	public void send(String message) throws IOException;
	
	public byte[] receive() throws IOException;
	
	public byte[] receive(String message) throws IOException;
	
}
