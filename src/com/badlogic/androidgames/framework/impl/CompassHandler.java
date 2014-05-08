package com.badlogic.androidgames.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassHandler implements SensorEventListener {
	float yaw;
	float pitch;
	float roll;
	
	//Take in a Context
	@SuppressWarnings("deprecation")
	public CompassHandler(Context context){
		SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); //Instantiate SensorManager
		if (manager.getSensorList(Sensor.TYPE_ORIENTATION).size() != 0){
			Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ORIENTATION).get(0);
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		}
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		//nothing to do here
	}
	
	
	public void onSensorChanged(SensorEvent event){
		yaw = event.values[0];
		pitch = event.values[1];
		roll = event.values[2];
	}
	
	public float getAccelX(){
		return yaw;
	}
	
	public float getAccelY(){
		return pitch;
	}
	
	public float getAccelZ(){
		return roll;
	}
}

//This is almost exactly the same as the Accelerometer Handler
//The only difference is the SENSORE.TYPE_ORIENTATION
