/*This will clear the screen with it's own random color
 * The Log.d will push messages to the logcat (indicating the state of the screen(
 * Log.d(TAG , text) -> These are the parameters of Log.d
 * */

package com.badlogic.androidgames.glbasics;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GLSurfaceViewTest extends Activity {
	
	GLSurfaceView glView; //Create an instance of GLSurfaceView
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//Make app go full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		glView = new GLSurfaceView(this);
		glView.setRenderer(new SimpleRenderer());
		
		setContentView(glView);
	}
	
	/*
	 * GL has it's own .onPause()/.onResume() methods which must be called
	 * in addition to the supermethods
	 * */
	@Override
	public void onResume(){
		super.onResume();
		glView.onResume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		glView.onPause();
	}
	
	static class SimpleRenderer implements Renderer{
		Random rand = new Random();
		
		/*
		 * This is called each time the GLSurfaceView surface is created.(This will happen
		 * when we fire up the activity for the first time or when we resume from a paused state).
		 * GL10 allows us to issue commands to OpenGL ES
		 * EGLConfig gives us attribute about the surface
		 * */
		public void onSurfaceCreated(GL10 gl, EGLConfig config){
			//When this method is called it will Log the following info
			Log.d("GLSurfaceViewTest", "surface created");
		}
		
		/*
		 * This is changed each time the surface is resized.
		 * We get the new width and height of the surface in pixels as parameters
		 * We CAN (and usually do) include a GL10 instance if we want to issue OpenGL ES commands
		 * */
		public void onSurfaceChanged(GL10 gl, int width, int height){
			//When this method is called it will Log the following info
			Log.d("GLSurfaceViewTest", "surface changed: "+width+"x"+height);
		}
		
		/*
		 * This renders everything
		 * */
		public void onDrawFrame(GL10 gl){
			
			//This will set the color to be used when we issue a command to clear the screen
			//The format is always RGBA
			gl.glClearColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1);
			
			/*
			 * glClear takes a single argument and selects which buffer to clear
			 * The "color buffer" is the buffer that hold all of our pixels
			 * To tell OpenGL ES we want to clear with THAT buffer - we call GL10.GL_COLOR_BUFFER_NIT
			 * */
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		}
	}

}
