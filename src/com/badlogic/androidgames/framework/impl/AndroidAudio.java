package com.badlogic.androidgames.framework.impl;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;

public class AndroidAudio implements Audio {
	AssetManager assets;
	SoundPool soundPool;
	
	/*
	Constructor
	By passing activity, we are allowed to set the volume control of the media stream.
	We also get an AssetManager instance which we will store in the corresponding class member
	*/
	
	public AndroidAudio(Activity activity){
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC); //Set Volume Control
		this.assets = activity.getAssets();
		this.soundPool = new SoundPool(20,AudioManager.STREAM_MUSIC,0); //Instantiate Sound Pool with up to 20 simultaneous sounds
	}
	
	/*
	 * Here we create a new AndroidMusic Instance
	 * The constructor takes an AssetFileDescriptor which it uses to create an internal MediaPlayer
	 */
	public Music newMusic(String filename){
		try{
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			return new AndroidMusic(assetDescriptor);
		} catch(IOException e){ //This exception will be thrown in case something goes wrong.
			throw new RuntimeException("Couldn't load music '"+filename+"'"); //A runtime exception is thrown so that we don't clutter the calling code
		}
	}
	
	//Loads a sound effect from an asset into the SoundPool and return an AndroidSound instance
	public Sound newSound(String filename){
		try{
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			int soundId = soundPool.load(assetDescriptor,0);
			return new AndroidSound(soundPool, soundId);
		} catch (IOException e){ //Catch any exception and throw it as a runtimeexception
			throw new RuntimeException("Couldn't load sound '"+filename+"'");
		}
	}
}
/*
 * NOTICE ! We don't release the ScounPool in any of these methods
 * There will always be a single Game instance holding a single Audio instance that holds a single SounPool instance.
 * The SoundPool instance will thus be alive as long as the activity is alive.
 * It will be destoryed automatically as soon as the acitivty ends
 * */
