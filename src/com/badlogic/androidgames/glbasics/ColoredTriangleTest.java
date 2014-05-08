package com.badlogic.androidgames.glbasics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public class ColoredTriangleTest extends GLGame {
	public Screen getStartScreen(){
		return new ColoredTriangleScreen(this);
		}
	
	class ColoredTriangleScreen extends Screen {
		/*
		 * Each coordinate takes up 4 bytes for a buffer
		 * Because we specify RGBA value, we must add 4 more bytes per info
		 * This will bring the total to 24 bytes per each VERTEX
		 * */
		final int VERTEX_SIZE = ((2 + 4) * 4);
		GLGraphics glGraphics;
		FloatBuffer vertices;
	
		public ColoredTriangleScreen (Game game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
		
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(3 * VERTEX_SIZE);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertices = byteBuffer.asFloatBuffer(); //Convert to float
			
			/*This is an 18 element array
			 * Each triangle info occupies 6 elements
			 * */
			
			vertices.put(new float[]{0.0f,0.0f, 1, 0, 0, 1, //Vertex at (0,0) with color red
									1079.0f, 0.0f, 0, 1, 0, 1, //Vertex at (1079,0) with color green
									1080.0f/2, 1919.0f, 0, 0, 1, 1 //Vertex at (1080/2,1920) with color blue
			});
			
			vertices.flip();
		}
		
		@Override
		public void present(float deltaTime){
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, 1080, 0, 1920, 1, -1);
			
			
			//We must tell OpenGL ES that our vertices not only have position, but also a color attribute
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			//gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			
			
			/*Now that open gl KNOWS the kind of info the triangle will have
			 * It must be instructed how to find thatinformation
			 * We start at position 0 which points to x coordinate of our first vertex
			 * We indicate 2 components to the vertex pointer
			 * Now we specify VERTEX_SIZE as the stride (remember in bytes!) that OpenGL ES knows when the next position arises
			 * Lastly we supply the array (vertices) from which it's reading
			 * */
			vertices.position(0);
			gl.glVertexPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			
			/*
			 * The color info for the first vertex doesn't begin until position 2, -> (x,y,COLOR INFO)
			 * Specify 4 components to the color info
			 * Indicate VERTEX_Size as the stride
			 * Supply Array
			 * */
			vertices.position(2);
			gl.glColorPointer(4, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			
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
