package com.badlogic.androidgames.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO {
	
	//Specify file name. Receive Stream in return
	//Put IOException in case something goes wrong
	public InputStream readAsset(String fileName) throws IOException;

	public InputStream readFile(String fileName) throws IOException;
	
	public OutputStream writeFile(String fileName) throws IOException;
	
	//We cannot forget to close these streams once we are done using them
}
