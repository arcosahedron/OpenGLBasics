package com.badlogic.androidgames.framework;

import com.badlogic.androidgames.framework.Graphics.PixmapFormat;

public interface Pixmap {
	
	public int getWidth(); //Return width of pixmap in pixels
	
	public int getHeight(); //Return height of pixmap in pixels
	
	public PixmapFormat getFormat(); //Return format the Pixmap is stored in RAM
	
	public void dispose(); //Dispose pixmap
}
