package com.badlogic.androidgames.glbasics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.glbasics.ColoredTriangleTest.ColoredTriangleScreen;

public class TextureTriangleTest extends GLGame {
	public Screen getStartScreen(){
		return new TexturedTriangleScreen(this);
		}
	
	class TexturedTriangleScreen extends Screen{
		/*
		 * We have to add an extra two coordinates as our texture coords
		 * We will have vertex coords, and texture coords
		 * */
		final int VERTEX_SIZE = (2+2)*4;
		GLGraphics glGraphics;
		FloatBuffer vertices;
		int textureId;
		
		public TexturedTriangleScreen(Game game){
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(VERTEX_SIZE*3);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertices = byteBuffer.asFloatBuffer();
			vertices.put( new float[] { 0.0f, 0.0f, 0.0f, 1.0f,
			319.0f, 0.0f, 1.0f, 1.0f,
			160.0f, 479.0f, 0.5f, 0.0f});
			vertices.flip();
			textureId = loadTexture("bobrgb888.png");
		}
		
		public int loadTexture(String fileName){
			try{
				Bitmap bitmap = BitmapFactory.decodeStream(game.getFileIO().readAsset(fileName));
				GL10 gl = glGraphics.getGL();
				
				int textureIds[] = new int[1];
				gl.glGenTextures(1, textureIds, 0);
				int textureId = textureIds[0];
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D,  0, bitmap, 0);
				gl.glTexPar
			}
		}
	}

}
