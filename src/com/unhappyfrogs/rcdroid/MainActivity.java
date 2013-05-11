package com.unhappyfrogs.rcdroid;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends Activity {
	
	/*
	private static final String HOST = "192.168.1.112";
	private static final int PORT = 31415;
	private static final int IMG_PORT = 31416;
	*/
	
	private static final String BD_ADDRESS = "00:1B:DC:00:33:E9";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//
		// Activity's views
		//
		SeekBar leftSeek = (SeekBar) findViewById(R.id.left_seek);
		SeekBar rightSeek = (SeekBar) findViewById(R.id.right_seek);
		ImageView imageView = (ImageView) findViewById(R.id.image_view);
		
		//
		// Set seek bar values
		//
		leftSeek.setMax(SeekListener.SEEK_MAX);
		leftSeek.setProgress(SeekListener.SEEK_MAX / 2);
		rightSeek.setMax(SeekListener.SEEK_MAX);
		rightSeek.setProgress(SeekListener.SEEK_MAX / 2);
		
		//
		// Rotate initial image
		//
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.nophoto);
		placeholder = Bitmap.createBitmap(placeholder, 0, 0, placeholder.getWidth(),
				placeholder.getHeight(), matrix, true);
		imageView.setImageBitmap(placeholder);
		
		try {
			// MessageHandler seekMessageHandler = new TCPMessageHandler(HOST, PORT);
			MessageHandler seekMessageHandler = new BluetoothMessageHandler(BD_ADDRESS);
			leftSeek.setOnSeekBarChangeListener(new SeekListener("LMS", seekMessageHandler));
			rightSeek.setOnSeekBarChangeListener(new SeekListener("RMS", seekMessageHandler));
			
			try {Thread.sleep(1000);} catch (Exception e) {}
			
			// MessageHandler imageMessageHandler = new TCPMessageHandler(HOST, IMG_PORT);
			MessageHandler imageMessageHandler = new BluetoothMessageHandler(BD_ADDRESS);
			new ImageTask(imageView, imageMessageHandler).start();
		} catch (IOException ioe) {
			System.out.println("IOException thrown trying to create message handlers: " + ioe.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
