package com.badlogic.androidgames.framework;

public interface Music {
	
	public void play(); //Play Music
	
	public void stop(); //Stop Music
	
	public void pause(); //Pause Music
	
	public void setLooping(boolean looping); //Set looping
	
	public void setVolume(float volume); //Volume between 0 and 1
	
	public boolean isPlaying(); // Music is Playing?
	
	public boolean isStopped(); //Music is stopped?
	
	public boolean isLooping();//Music is Looping?
	
	public void dispose(); //Dispose of music once it is no longer needed
	
}
