package com.unhappyfrogs.rcdroid;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;

public class ImageTask extends Thread {
	
	private ImageView imageView;
	private MessageHandler messageHandler;
	
	public ImageTask(ImageView imageView, MessageHandler messageHandler) {
		this.imageView = imageView;
		this.messageHandler = messageHandler;
	}
	
	public void run() {
		while (true) {
			//
			// get image
			//
			byte[] imageBuffer;
			try {
				imageBuffer = messageHandler.receive();
			} catch (IOException ioe) {
				System.out.println("IOException thrown trying to get image: " + ioe.getMessage());
				break;
			}
			
			//
			// generate Bitmap from buffer and rotate
			//
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap image = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length);
			image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
			
			//
			// draw image to view
			//
			imageView.post(new ImagePoster(imageView, image));
		}
	}
	
	private class ImagePoster implements Runnable {

		private ImageView view;
		private Bitmap image;
		
		public ImagePoster(ImageView view, Bitmap image) {
			this.view = view;
			this.image = image;
		}
		
		@Override
		public void run() {
			view.setImageBitmap(image);
		}
		
	}
}
