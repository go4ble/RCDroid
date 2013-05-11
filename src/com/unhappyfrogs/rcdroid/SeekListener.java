package com.unhappyfrogs.rcdroid;

import java.io.IOException;

import android.widget.SeekBar;

public class SeekListener implements SeekBar.OnSeekBarChangeListener {
	
	public static final int SEEK_MAX = 510;
	
	private String prefix;
	private MessageHandler messageHandler;
	
	public SeekListener(String prefix, MessageHandler messageHandler) {
		this.prefix = prefix;
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		String message = prefix + progress;
		try {
			messageHandler.send(message);
		} catch (IOException ioe) {
			System.out.println("IOException thrown trying to send seek bar value: " + ioe.getMessage());
			seekBar.setEnabled(false);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// NOT USED
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//
		// Set SeekBar back to center
		//
		seekBar.setProgress(SEEK_MAX / 2);
	}
	
}