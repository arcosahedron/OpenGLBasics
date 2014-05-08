package com.badlogic.androidgames.framework.impl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.FileIO;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public abstract class GLGame extends Activity implements Game, Renderer {
	
	//This will keep track of the GL game instance
	enum GLGameState {
		Initialized,
		Running,
		Paused,
		Finished,
		Idle
	}
	
	GLSurfaceView glView;
	GLGraphics glGraphics;
	Audio audio;
	Input input;
	FileIO fileIO;
	Screen screen;
	GLGameState state = GLGameState.Initialized;
	Object stateChanged = new Object(); //This will be used to synchronized with  the UI and rendering threads.
	long startTime = System.nanoTime();
	WakeLock wakeLock;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//Full Screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		glView = new GLSurfaceView(this); //Create a new Surface View in Oncreate
		glView.setRenderer(this);
		setContentView(glView);
		
		
		glGraphics = new GLGraphics(glView);
		fileIO= new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		
		/*
		 * We no longer let the AndroidInput class scale the touch coordinates to a target resolution
		 * (Like in android game). Since the scale values are 1, we will get REAL touch coorindates
		*/
		input = new AndroidInput (this, glView, 1 ,1); //the 1,1 will scale x and y appropriately
		
		//Create WakeLock instance
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
	}
	
	@Override 
	public void onResume(){
		super.onResume(); //Inherit
		glView.onResume(); //Resume GLVIEW
		wakeLock.acquire(); //Acquire wakeLock
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config){
		glGraphics.setGL(gl);
		
		/*
		 * If the app is started for the first time the state will be initialized, in which
		 * case the start screen will be called.
		 * 
		 * synchronized must be used so that we prevent all the members inside from being 
		 * affected by the onPause method.
		 * */
		synchronized(stateChanged){
			if (state == GLGameState.Initialized)
				screen = getStartScreen();
			state = GLGameState.Running;
			screen.resume();
			startTime = System.nanoTime();
		}
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height){
		//Nothing to do here
	}
	
	/*Called by Rendering thread as often as possible
	 * We Basically chck the Game State and react accordingly
	*/
	public void onDrawFrame(GL10 gl){
		GLGameState state = null;
		
		synchronized(stateChanged){
			state=this.state;
		}
		
		/*
		 * If game is running we calc the delta time and tell the current Screen to update and present itself
		 * */
		if(state== GLGameState.Running){
			float deltaTime = (System.nanoTime() - startTime)/1000000000.0f;
			startTime = System.nanoTime();
			
			screen.update(deltaTime);
			screen.present(deltaTime);
		}
		
		/*
		 * If state is paused, we pause the screen
		 * Change game state to IDLE
		 * Synchornized is necesarry as onPause can also affect the gamestate
		 * Notify the UI thread that it can now truly pause the application
		 * The notificaiton is necesarry in case our Activity is paused or closed on the UI thread
		 * */
		if(state==GLGameState.Paused){
			screen.pause();
			synchronized(stateChanged){
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}
		
		/*
		 * If activity is closing
		 * Tell current screen to pause and dispose of itself
		 * Send notification to UI to shut down things properly
		 * */
		if(state == GLGameState.Finished){
			screen.pause();
			screen.dispose();
			synchronized(stateChanged){
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}
	}
	
	@Override
	public void onPause(){
		synchronized (stateChanged){
			if (isFinishing())
				state = GLGameState.Finished;
			else
				state = GLGameState.Paused;
			while(true){
				try{
					stateChanged.wait(); //wait for thread to process new thread
					break;
				} catch(InterruptedException e){
					//Nothing to do here
				}
			}
			
		}
		
		wakeLock.release();
		glView.onPause(); //This will destory OpenGL ES surface - which triggers context loss
		super.onPause();
	}
	
	//Return instance of GLGraphics so that we can gain access to it later on.
	public GLGraphics getGLGraphics(){
		return glGraphics;
	}
	
	public Input getInput(){
		return input;
	}
	
	public FileIO getFileIO(){
		return fileIO;
	}
	
	public Graphics getGraphics(){
		throw new IllegalStateException("We are using openGL!");
	}
	
	public Audio getAudio(){
		return audio;
	}
	
	public void setScreen(Screen newScreen){
		if (screen ==null)
			throw new IllegalArgumentException("Screen must not be null");
		
		this.screen.pause();
		this.screen.dispose();
		newScreen.resume();
		newScreen.update(0);
		this.screen = newScreen;
	}
	
	public Screen getCurrentScreen(){
		return screen;
	}
}
