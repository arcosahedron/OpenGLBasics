package com.badlogic.androidgames.framework.impl;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.badlogic.androidgames.framework.Music;

public class AndroidMusic implements Music, OnCompletionListener {
	MediaPlayer mediaPlayer;
	boolean isPrepared = false;
	
	
	public AndroidMusic(AssetFileDescriptor assetDescriptor){
		try {
			//Create and prepare MediaPlayer from the AssetFileDescriptor that is passed in
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),assetDescriptor.getStartOffset(),assetDescriptor.getLength());
			mediaPlayer.prepare();
			isPrepared = true; //set isPrepared flag on
			mediaPlayer.setOnCompletionListener(this); //Register music instance as an OnCompletionListener
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load music"); //Throw a runtime exception if anything goes wrong
		}
	}
	
	public void dispose(){
		if (mediaPlayer.isPlaying())
			mediaPlayer.stop();
		mediaPlayer.release();
	}
	
	public boolean isLooping(){
		return mediaPlayer.isLooping(); //Will return T or F
	}
	
	public boolean isPlaying(){
		return mediaPlayer.isPlaying(); //Will return T or F
	}
	
	public boolean isStopped(){
		return !isPrepared; //Will return T or F
	}
	
	public void pause(){
		if(mediaPlayer.isPlaying())
			mediaPlayer.pause();
	}
	
	public void play(){
		if(mediaPlayer.isPlaying())
			return; //If already playing...return the function
		//ELSE
		try{
			//We pass a synchronized block because is prepared could get on a separagte thread
			//Synchronized - only one thread passes
			synchronized(this){
				if(!isPrepared) //If MediaPlayer is not prepare , prepare
					mediaPlayer.prepare();
				mediaPlayer.start(); // Else Prepare
			}
		} catch (IllegalStateException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	//SetLooping and SetVolume can be called in any state of the MediaPlayer and delegated to respective MediaPlayer Methods
	public void setLooping(boolean isLooping){
		mediaPlayer.setLooping(isLooping);
	}
	
	public void setVolume (float volume){
		mediaPlayer.setVolume(volume, volume);
	}
	
	//Stops MediaPlayer and sets isPrepared to false
	public void stop(){
		mediaPlayer.stop();
		synchronized(this){
			isPrepared = false;
		}
	}
	
	public void onCompletion(MediaPlayer player){
		synchronized(this){
			isPrepared = false;
		}
	}

}
