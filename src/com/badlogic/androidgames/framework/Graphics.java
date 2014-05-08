package com.badlogic.androidgames.framework;

public interface Graphics {
	public static enum PixmapFormat{
		ARGB8888, ARGB4444, RGB565
	}
	
	//Load an image in either jpeg or PNG
	//We can specify the format and have greater control
	public Pixmap newPixmap(String fileName, PixmapFormat format); 
	
	public void clear(int color); //Clear framebuffer with a given color
	
	public void drawPixel(int x, int y, int color); //Draw a pixel at a given coordinate with a set color
	
	public void drawLine(int x, int y, int x2, int y2, int color); //Draw a line at a given coordinate with a set color
	
	public void drawRect(int x, int y, int width, int height, int color); //Draw a rectangle at a given coordinate with a set color
	
	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight); //Draw a pixmap from a portion or full source
	
	public void drawPixmap (Pixmap pixmap, int x, int y); //Draw a pixmap from a source fully
	
	public int getWidth(); //Return Width of framebuffer
	
	public int getHeight(); //Return height of framebuffer
}
