package com.badlogic.androidgames.framework.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
	AndroidGame game;
	Bitmap framebuffer;
	Thread renderThread = null;
	SurfaceHolder holder;
	volatile boolean running = false;
	
	public AndroidFastRenderView(AndroidGame game, Bitmap framebuffer){
		super(game);
		this.game = game;
		this.framebuffer = framebuffer;
		this.holder = getHolder();
	}
	
	public void resume() {
		running = true;
		renderThread = new Thread(this);
		renderThread.start();
	}
	
	public void run(){
		Rect dstRect = new Rect();
		long startTime = System.nanoTime();
		while(running)
			if(!holder.getSurface().isValid())
				continue;
		
		float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f; //Convert to seconds
		startTime = System.nanoTime(); //Save current timeStamp to help find next delta time
		
		game.getCurrentScreen().update(deltaTime); //Update screen state
		game.getCurrentScreen().present(deltaTime); //Present the updated screen state
		
		Canvas canvas = holder.lockCanvas();
		canvas.getClipBounds(dstRect); //Shortcut to get top left corner(0,0) of dstRect and bottom right (screen width, screen height)
		canvas.drawBitmap(framebuffer, null, dstRect, null); //Draw Artificial frame buffer
		holder.unlockCanvasAndPost(canvas);
	}
	
	//Ensure the thread stop when the acitivity is paused or destroyed
	public void pause(){
		running = false;
		while(true){
			try{
				renderThread.join();
				return;
			} catch (InterruptedException e){
				//retry
			}
		}
	}

}
