package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
	
	ServerSocket ss;
	
	public ServerThread (int port) {
		super();
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			Socket socket = ss.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
