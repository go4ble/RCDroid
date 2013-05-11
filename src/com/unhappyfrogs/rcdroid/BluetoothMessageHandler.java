package com.unhappyfrogs.rcdroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothMessageHandler implements MessageHandler {
	private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
	
	private BluetoothSocket socket;
	private InputStream iStream;
	private OutputStream oStream;
	
	public BluetoothMessageHandler(String address) throws IOException {
		BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice bDevice = bAdapter.getRemoteDevice(address);
		socket = bDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_STRING));
		socket.connect();
		iStream = socket.getInputStream();
		oStream = socket.getOutputStream();
	}
	
	@Override
	public void send(String message) throws IOException {
		message = message.length() + ":" + message;
		System.out.println(message);
		oStream.write(message.getBytes());
	}
	
	@Override
	public byte[] receive() throws IOException {
		StringBuilder sb = new StringBuilder();
		char c = (char) iStream.read();
		while (c != ':') {
			sb.append(c);
			c = (char) iStream.read();
		}
		int size = Integer.parseInt(sb.toString());
		byte[] response = new byte[size];
		int offset = 0;
		do {
			offset += iStream.read(response, offset, size - offset);
		} while (offset < size);
		
		return response;
	}

	@Override
	public byte[] receive(String message) {
		// TODO Auto-generated method stub
		return null;
	}

}
