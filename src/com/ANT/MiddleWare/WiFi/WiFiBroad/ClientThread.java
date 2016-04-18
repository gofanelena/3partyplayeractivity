package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread extends Thread {
	
	private Socket client;
	private int ip;
	private int port;

	public ClientThread(int mIp, int mPort) {
		super();
		this.ip = mIp;
		this.port = mPort;
		this.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			client = new Socket("192.168.1."+ip, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		

}
