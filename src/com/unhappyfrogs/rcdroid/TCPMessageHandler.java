package com.unhappyfrogs.rcdroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPMessageHandler implements MessageHandler {
	
	private Socket sock;
	private InputStream iStream;
	private OutputStream oStream;
	
	public TCPMessageHandler(final String host, final int port) {
		Thread startThread = new Thread() {
			public void run() {
				try {
					sock = new Socket(host, port);
					iStream = sock.getInputStream();
					oStream = sock.getOutputStream();
				} catch (IOException ioe) {
					System.out.print("IOException thrown in TCPMessageHandler constructor: ");
					System.out.println(ioe.getMessage());
				}
			}
		};
		startThread.start();
		try {
			startThread.join();
		} catch (InterruptedException ie) {
			System.out.println("InterruptedException thrown in TCPMessageHandler constructor: " + ie.getMessage());
		}
	}
	
	@Override
	public void send(String message) {
		synchronized (this) {
			new MessageThread(message).start();
		}
	}
	
	@Override
	public byte[] receive() {
		return this.receive("");
	}

	@Override
	public byte[] receive(String message) {
		synchronized (this) {
			MessageThread mThread = new MessageThread(message);
			mThread.start();
			try {
				mThread.join();
				return mThread.getResponse();
			} catch (InterruptedException ie) {
				System.out.println("InterruptedException thrown in TCPMessageHandler.receive: " + ie.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Thread for handling communications
	 */
	private class MessageThread extends Thread {
		private String message;
		private byte[] response;
		
		public MessageThread(String message) {
			//
			// prepend message's length to itself
			//
			this.message = message.length() + ":" + message;
		}
		
		@Override
		public void run() {
			try {
				//
				// send message
				//
				if (message.charAt(0) != '0')
					oStream.write(this.message.getBytes());
				
				//
				// get incoming message size
				//
				StringBuilder sb = new StringBuilder();
				char c = (char) iStream.read();
				while (c != ':') {
					sb.append(c);
					c = (char) iStream.read();
				}
				int size = Integer.parseInt(sb.toString());
				
				//
				// get incoming message
				//
				response = new byte[size];
				int pos = 0;
				do {
					pos += iStream.read(response, pos, size - pos);
				} while (pos < size);
				
			} catch (IOException ioe) {
				System.out.println("IOException thrown in TCPMessageHandler.MessageThread: " + ioe.getMessage());
			}
		}
		
		public byte[] getResponse() {
			return response;
		}
	}

}
