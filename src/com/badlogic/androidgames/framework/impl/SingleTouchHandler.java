package com.badlogic.androidgames.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Pool;
import com.badlogic.androidgames.framework.Pool.PoolObjectFactory;

public class SingleTouchHandler implements TouchHandler {
	boolean isTouched; //Current state of touchscreen for a finer
	int touchX; //X coordinate of touch
	int touchY; //Y coordinate of touch
	
	//The following hold the TouchEvents, similar to the keyboard handler
	Pool<TouchEvent>touchEventPool;
	List<TouchEvent>touchEvents = new ArrayList<TouchEvent>();
	List<TouchEvent>touchEventsBuffer = new ArrayList<TouchEvent>();
	float scaleX; //This will be used to cope with different screen resolutions.
	float scaleY;
	
	//Constructor for SingleTouchHandler
	public SingleTouchHandler(View view, float scaleX, float scaleY){
		PoolObjectFactory<TouchEvent>factory = new PoolObjectFactory<TouchEvent>(){
			@Override
			public TouchEvent createObject(){
				return new TouchEvent();
			}
		};
		
		touchEventPool = new Pool<TouchEvent> (factory,100); //Set up pool used to recycle TouchEvents
		view.setOnTouchListener(this); //Register Handler as onTouch Listener
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public boolean onTouch(View v, MotionEvent event){	
		synchronized(this){
			TouchEvent touchEvent = touchEventPool.newObject();
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					touchEvent.type = TouchEvent.TOUCH_DOWN;
					isTouched=true;
					break;
				case MotionEvent.ACTION_MOVE:
					touchEvent.type = TouchEvent.TOUCH_DRAGGED;
					isTouched = true;
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					touchEvent.type = TouchEvent.TOUCH_UP;
					isTouched = false;
					break;
			}
			
			touchEvent.x = touchX = (int)(event.getX()*scaleX);
			touchEvent.y = touchY = (int)(event.getY()*scaleY);
			touchEventsBuffer.add(touchEvent);
			
			return true;
		}
	}
	
	//The Pointer ID will only return an ID of 0, since it is a single pointer ID
	public boolean isTouchDown(int pointer){
		synchronized(this){
			if (pointer ==0 )
				return isTouched;
			else return false;
		}
	}
	
	public int getTouchX(int pointer){
		synchronized(this){
			return touchX;
		}
	}
	
	public int getTouchY(int pointer){
		synchronized(this){
			return touchY;
		}
	}
	
	//We call this frequently so that the TouchEvents list doesn't fill up
	public List<TouchEvent>getTouchEvents(){
		synchronized(this){
			int len = touchEvents.size();
			for (int i = 0; i < len; i++)
				touchEventPool.free(touchEvents.get(i));
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}
}
