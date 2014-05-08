package com.badlogic.androidgames.framework;

import java.util.List;

public interface Input {
	public static class KeyEvent{

		//Define Constants that encode a key event's type
		public static final int KEY_DOWN = 0;
		public static final int KEY_UP = 1;

		public int type; //Record type
		public int keyCode; //Record code
		public char keyChar; //Record UniCode char

	}

	public static class TouchEvent{

		//Define constants that encode a touch event type
		public static final int TOUCH_DOWN = 0;
		public static final int TOUCH_UP = 1;
		public static final int TOUCH_DRAGGED = 2;

		public int type; //Record type
		public int x,y; //Record coordinates
		public int pointer; //Record pointer ID of finger
	}

	public boolean isKeyPressed(int keyCode);

	public boolean isTouchDown(int pointer);

	public int getTouchX(int pointer);

	public int getTouchY(int pointer);
	
	//Return respective acceleration values of each accelerometer axis
	public float getAccelX();

	public float getAccelY();

	public float getAccelZ();
	
	//Event based Handling
	public List <KeyEvent> getKeyEvents(); //Return key event instance last recorded

	public List <TouchEvent> getTouchEvents(); //Return touch event instance last recorded
}