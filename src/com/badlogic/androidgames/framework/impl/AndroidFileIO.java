package com.badlogic.androidgames.framework.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.badlogic.androidgames.framework.FileIO;

public class AndroidFileIO implements FileIO {
	Context context; // Store context instance
	AssetManager assets; //Store Asset Manager that is pulled from Context
	String externalStoragePath; // Instantiate External Storage Path
	
	//Constructor
	public AndroidFileIO(Context context){
		this.context = context;
		this.assets = context.getAssets();
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator;
	}
	
	//We implement the four methods based on external storage path
	public InputStream readAsset(String fileName) throws IOException{
		return assets.open(fileName);
	}
	
	public InputStream readFile(String fileName) throws IOException{
		return new FileInputStream(externalStoragePath+fileName);
	}
	
	public OutputStream writeFile(String fileName) throws IOException{
		return new FileOutputStream(externalStoragePath+fileName);
	}
	
	
	public SharedPreferences getPrefereneces(){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
