package com.badlogic.androidgames.framework.impl;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public abstract class AndroidGame extends Activity implements Game {
	AndroidFastRenderView renderView; //We draw to this. It will also manage our main loop thread for us
	Graphics graphics; //
	Audio audio;
	Input input;
	FileIO fileIO;
	Screen screen; //This will hold the currently active screen
	WakeLock wakeLock; //This is for keeping the screen from dimming
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState); //Inheritance
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); // Make FullScreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//getResources().getConfiguration().orientation will return the screen orientation
		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE; //Will return either T or F
		
		//Set UP artificial frame buffer
		int frameBufferWidth = isLandscape ? 1920 : 1080; //frameBufferWidth = 480 IF landscape is True, else 320
		int frameBufferHeight = isLandscape ? 1080: 1920; //frameBufferWidth = 320 IF landscape is True, else 480
		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565); //RGB_565 won't waste memory and it will draw faster
		
		float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
		float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();
		
		renderView = new AndroidFastRenderView(this, frameBuffer);
		graphics = new AndroidGraphics(getAssets(), frameBuffer);
		fileIO = new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		input = new AndroidInput(this, renderView, scaleX, scaleY);
		screen = getStartScreen();
		setContentView(renderView);
		
		//Relevant WakeLock functions
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		wakeLock.acquire(); //Acquire wakeLock
		screen.resume(); //Inform screen that game is resumed
		renderView.resume(); //Tell AndroidFastRenderView to resume rendering thread
	}
	
	@Override
	public void onPause(){
		super.onPause();
		wakeLock.release(); //Release wakeLock
		renderView.pause(); //Tell AndroidFastRenderView to pause rendering thread
		screen.pause();
		
		//Ensure that that rendering thread is terminated
		if (isFinishing())
			screen.dispose();
	}
	
	public Input getInput(){
		return input;
	}
	
	public FileIO getFileIO(){
		return fileIO;
	}
	
	public Graphics getGraphics(){
		return graphics;
	}
	
	public Audio getAudio(){
		return audio;
	}
	
	public void setScreen (Screen screen){
		if(screen == null)
			throw new IllegalArgumentException("Screen must not be null");
		
		//Pause and dispose to make room for the new screen that we will set
		this.screen.pause();
		this.screen.dispose();
		
		//The new screen is asked to resume and update with a delta time of 0
		screen.resume();
		screen.update(0); //Update with a delta time of zero
		this.screen = screen; //Set the Screen member to the new Screen
	}
	
	public Screen getCurrentScreen(){
		return screen;
	}
}
