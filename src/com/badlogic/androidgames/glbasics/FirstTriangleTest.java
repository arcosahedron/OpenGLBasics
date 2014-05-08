package com.badlogic.androidgames.glbasics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public class FirstTriangleTest extends GLGame {
	public Screen getStartScreen(){
		return new FirstTriangleScreen(this);
		}
	
	class FirstTriangleScreen extends Screen {
		GLGraphics glGraphics;
		FloatBuffer vertices; // This will store the 2D positions of the 3 vertices of our triangle
		private static final String TAG = "FirstTriangleTest";
		
		public FirstTriangleScreen(Game game){
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
		
			/*Because each coordinate is a float it will take up 4 bytes
			 * We have 3 vertices (TRIANGLE) and 2 coordinates per vertex
			 * (3 vertices/Triangle)*(2 coords/vertex)*(4 bytes/coordinate) = (3*2*4) bytes / Triangle = 24 bytes/triangle
			 * */
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 2 * 3);
			byteBuffer.order(ByteOrder.nativeOrder()); //Ensure that the byte order is equal to the byte order used by the uderlying CPU
			
			Log.d(TAG, Integer.toString(byteBuffer.limit()));
			
			//The position here is 6. The limit and capacity will also 6.
			vertices = byteBuffer.asFloatBuffer(); //Convert to floats so that we can work with floats
			//Mark down coordinates
			vertices.put(new float[]{0.0f,   0.0f,
									 1079.0f, 0.0f,
									 1080.0f/2, 1919.0f,
									 });
			
			//Log.d(TAG, vertices.toString());
			
			vertices.flip(); //The position here will change to 0. The limit and capacity will not change.
			
			//Log.d(TAG, vertices.toString());
		}

		

		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			
			//Set tje Viewport to start at 0,0 and match specs of screen of device
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			//Log.d(TAG, Integer.toString(glGraphics.getWidth()));
			
			/*Set the state of NIO buffer*/
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION); // Set up projection matrix
			gl.glLoadIdentity(); //Load identitiy matrix so porjection matrix doesn't multiply against itself
			gl.glOrthof(0, 1080, 0, 1920, 1, -1);// Produce projection matrixs
			
			/*This will set a default color to be used with vertices that don't have a color specified
			 * The Format for this is RGBA*/
			gl.glColor4f(1, 0, 0, 1);
			
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); //Tells GL that the vertices we are drawing with have a position
			
			/*
			 * This will tell OpenGLES where it can find the vertex positions and give some additional information
			 * First parameter tells us that each position consists of two coordinates
			 * Second parameter tells the data type to store each coordinate
			 * Third parameter (stride) indicates how far apart our vertex position are form each other in bytes
			 * We use 0 in this case because the vertices are tightly packed [v1(x,y),v2(x,y),...]
			 * The FOURTH parameter is our FloatBuffer
			 * Once this is issues, OpenGL Es will transfer the vertex positiion to GPU and store them there for all subdequent rendering commands.
			 * 
			 * */
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertices);
			
			/*
			 * The first parameter specifies what type of primitive we want to draw (in this case a list of triangles
			 * SECOND parameter is an offset (measured in vertices) - we could use this to render specific triangles if more than one is set.
			 * THIRD parameter indicates how many vertices should be used for rendering - for triangles, this number MUST be a multiple of three.
			 * */
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
			
			
		}
		
		@Override
		public void update(float deltaTime) {
			game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();
			
			
		}

		@Override
		public void pause() {
			// This does nothing
			
		}

		@Override
		public void resume() {
			// This does nothing
			
		}

		@Override
		public void dispose() {
			// This does nothing
			
		}
	}
}
