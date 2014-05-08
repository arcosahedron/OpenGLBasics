package com.badlogic.androidgames.framework;

public abstract class Screen {
	protected final Game game;
	
	//Set up a constructor 
	//This constructor receives a game instance
	//Now the screen has access to all data related to the game
	public Screen(Game game) {
		this.game = game;
	}
	
	public abstract void update(float deltaTime); //Update screen state
	
	public abstract void present(float deltaTime); //Present these updates
	
	public abstract void pause(); //Pause Screen
	
	public abstract void resume(); //Resume screen
	
	public abstract void dispose(); //Free up memory once screen is done.
}
