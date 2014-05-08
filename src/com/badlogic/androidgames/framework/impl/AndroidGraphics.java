package com.badlogic.androidgames.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager; //Provides access to an application's raw asset files
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;

public class AndroidGraphics implements Graphics {
	AssetManager assets; //We use this to load bitmap instances
	Bitmap frameBuffer; //Represents artificial frame buffer
	Canvas canvas; //We use to draw to the artificial frame buffer
	Paint paint; //Use for Drawing
	
	//These rects will be used for AndroidGraphics.drawPixmap()
	Rect srcRect = new Rect(); 
	Rect dstRect = new Rect();
	
	public AndroidGraphics (AssetManager assets, Bitmap frameBuffer){
		this.assets =assets;
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);
		this.paint = new Paint();
	}
	
	//Load Bitmap from an asset file using a specified PixmapFormat
	public Pixmap newPixmap (String fileName, PixmapFormat format){
		Config config = null;
		if (format == PixmapFormat.RGB565)
			config = Config.RGB_565;
		else if (format == PixmapFormat.ARGB4444)
			config = Config.ARGB_4444;
		else
			config = Config.ARGB_8888;
		
		Options options = new Options(); //Create options instance
		options.inPreferredConfig = config; //Set preferred color format
		
		InputStream in = null;
		Bitmap bitmap = null;
		
		//Try to load bitmap from asset via Bitmap factory
		//Throw Runtime Exception if something goes wrong
		try{
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		} catch(IOException e){
			throw new RuntimeException("Couldn't load bitmap from asset '"+ fileName + "'");
		} finally{
			if( in != null){
				try{
					in.close();
				} catch(IOException e){
					
				}
			}
		}
		
		//If no exceptions check the format the BitmapFacotry used to laod the Bitmap
		//Translate it into a PixmapFormat enumaration value
		if (bitmap.getConfig() == Config.RGB_565)
			format = PixmapFormat.RGB565;
		else if (bitmap.getConfig()==Config.ARGB_4444)
			format = PixmapFormat.ARGB4444;
		else
			format = PixmapFormat.ARGB8888;
		return new AndroidPixmap(bitmap, format);
	}
	
	public void clear(int color){
		canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,(color & 0xff));
	}
	
	public void drawPixel(int x, int y, int color){
		paint.setColor(color);
		canvas.drawPoint(x, y, paint);
	}
	
	public void drawLine(int x, int y, int x2, int y2, int color){
		paint.setColor(color);
		canvas.drawPoint(x, y, paint);
	}
	
	public void drawRect(int x, int y, int width, int height, int color){
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawRect(x, y, x+width-1, y+width-1, paint);
	}
	
	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight){
		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth -1 ;
		srcRect.bottom = srcY + srcHeight -1;
		
		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + srcWidth - 1;
		dstRect.bottom = y +srcHeight - 1;
		
		canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, null);
	}
	
	public void drawPixmap(Pixmap pixmap, int x, int y){
	canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, null);
	
	}
	
	public int getWidth(){
		return frameBuffer.getWidth();
	}
	
	public int getHeight(){
		return frameBuffer.getHeight();
	}
}
