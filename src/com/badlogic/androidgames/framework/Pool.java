package com.badlogic.androidgames.framework;

import java.util.ArrayList;
import java.util.List;

public class Pool <T> {
	
	public interface PoolObjectFactory <T>{
		public T createObject(); //Will return a new object with the generic type of the Pool/PoolObjectFactory instance
	}
	
	private final List <T> freeObjects; //An Array that will store pooled objects
	private final PoolObjectFactory <T>factory; //Used to generate new instances of the type help by the class
	private final int maxSize; //Member that stores the max number of objects the Pool can hold. This is needed so that the Pool grows indefinitely

	public Pool(PoolObjectFactory<T>factory, int maxSize){
		this.factory = factory; // Take a PoolObjectFactory
		this.maxSize = maxSize; //Define maxSize of Pool
		this.freeObjects = new ArrayList<T>(maxSize); //Instantiate an Array as large as max size 
	}
	
	//This will hand us a new instance of an Object of a certain type held by the Pool
	//or It will return a pooled instance in case one is in the freeObjectsArrayList
	//Using this method will give us recycled objects as long as the Pool has some stored in the freeObjects list
	
	public T newObject(){
		T object = null;
		if (freeObjects.isEmpty())
			object = factory.createObject();
		else
			object = freeObjects.remove(freeObjects.size()-1);
		
		return object;
	}
	
	//We can reinsert objects that we no longer use
	//Insert an object into the freeObjects list if not yet filled to capacity
	//If list is filled, the object is not added
	public void free(T object){
		if(freeObjects.size()<maxSize)
			freeObjects.add(object);
	}
	
}

/*THE FOLLOWING IS HOW WE WOULD USE THE POOL CLASS 
 * 
 * 1. Define a pool object factory that creates TouchEvent instances
 * PoolObjectFactory <TouchEvent> factory = new PoolObjectFactory <TouchEvent> () {
 * @Override
 * public TouchEvent createObject() {
 * 	return new TouchEvent();
 * }
 * };
 * 
 * 2. Instantiate the pool by telling it to use our factory and that it should store up to 50 TouchEvnt
 * Pool <TouchEvent> touchEventPool = new Pool <TouchEvent> (factory, 50);
 * 
 * 3. Call newObject() to create a new object or Pull from the list
 * TouchEvent touchEvent = touchEventPool.newObject();
 * 
 * . . . do something here . . .
 * 
 * 4. Call the free method when we no longer need the object
 * touchEventPool.free(touchEvent); 
 * */
