package com.badlogic.androidgames.framework.impl;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.androidgames.framework.Input.KeyEvent;
import com.badlogic.androidgames.framework.Pool;
import com.badlogic.androidgames.framework.Pool.PoolObjectFactory;

import android.view.View;
import android.view.View.OnKeyListener;

//OnKeyListener implemented so that it can receive key event from a View
public class KeyboardHandler implements OnKeyListener {
	boolean [] pressedKeys = new boolean[128];//Create an array of 128 booleans
	Pool<KeyEvent>keyEventPool; //Hold instances of KeyEvent Class. Recycle all KeyEvent objects we create
	List<KeyEvent>keyEventsBuffer = new ArrayList<KeyEvent>(); //Store keyEvent instances that have not yet been consumed by our games
	List<KeyEvent>keyEvents = new ArrayList <KeyEvent>(); //Stores the KeyEvents that we return by calling the KeyboardHandler.getKeyEvents()

	//The constructor has a single parameter VIEW from which we want to recieve key events
	public KeyboardHandler(View view){
		PoolObjectFactory<KeyEvent>factory = new PoolObjectFactory<KeyEvent>(){
			public KeyEvent createObject(){
				return new KeyEvent();
			}
		};
		keyEventPool = new Pool<KeyEvent>(factory,100);
		view.setOnKeyListener(this);
		//Make sure View receives KeyEvents by putting it into focus
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}
	
	public boolean onKey(View v, int keyCode, android.view.KeyEvent event){
		//Ignore any (Android) key evens that encode a KeyEvent.ACTION_MULTIPLE event
		if(event.getAction()==android.view.KeyEvent.ACTION_MULTIPLE)
			return false;
		
		synchronized(this){ //Ensure no members are accessed in parallel
			KeyEvent keyEvent = keyEventPool.newObject(); //Fetch a keyEvent instance
			keyEvent.keyCode=keyCode; //Set keyCode
			keyEvent.keyChar = (char)event.getUnicodeChar(); //Set Unicode character
			//Set KeyDown if the event registers and android view ACTION DOWN
			if(event.getAction() == android.view.KeyEvent.ACTION_DOWN){
				keyEvent.type=KeyEvent.KEY_DOWN;
				if( keyCode > 0 && keyCode <127){
					pressedKeys[keyCode] = true;
				}
			}
			
			if(event.getAction()==android.view.KeyEvent.ACTION_UP){
				keyEvent.type=KeyEvent.KEY_UP;
				if(keyCode > 0 && keyCode < 127)
					pressedKeys[keyCode] = false;
			}
			keyEventsBuffer.add(keyEvent);//Add key event to event buffer list
		}
		return false;
	}
	
	/*
	 * First we pass in an integer that specifies whether that key is pressed or not.
	 * The state of key is looked up after some range checking is performed
	 * */
	public boolean isKeyPressed(int keyCode){
		if( keyCode<0 || keyCode > 127)
			return false;
		return pressedKeys[keyCode];
					
	}
	
	public List <KeyEvent> getKeyEvents(){
		synchronized(this){
			int len = keyEvents.size();
			//Loop through through keyEvents array and insert all of it KeyEvents into our Pool
			for(int i=0; i < len; i++){
				keyEventPool.free(keyEvents.get(i));
			}
			keyEvents.clear();
			keyEvents.addAll(keyEventsBuffer);
			keyEventsBuffer.clear();
			return keyEvents;
		}
	}
}
