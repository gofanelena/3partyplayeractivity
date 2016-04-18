package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
import java.io.InputStream;
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
		while (true) {
			try {
				Socket socket = ss.accept();
				InputStream is = socket.getInputStream();
				if (0 == is.read()) {
					RoundRobin.getInstance().setMyTurn(true);
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
