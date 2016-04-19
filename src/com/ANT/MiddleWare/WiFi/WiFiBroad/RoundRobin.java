package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class RoundRobin extends Thread {

	private ArrayList<String> ipList = new ArrayList<String>();
	private boolean isMyTurn = false;
	private static RoundRobin instance;
	private static final int SERVER_PORT = 4444;
	private Socket prev = null, next = null;
	private static final int TOKEN = 1;
	private static final int ACK = 2;

	private RoundRobin() {
		super();
		this.start();
	}

	public static synchronized RoundRobin getInstance() {
		if (instance == null) {
			instance = new RoundRobin();
		}
		return instance;
	}

	public boolean canITalk() {
		synchronized (this) {
			return isMyTurn;
		}
	}

	public void passToken() {
		synchronized (this) {
			if (!isMyTurn)
				return;
			if (ipList.size() <= 1)
				return;
			isMyTurn = false;
		}
		String nextIP = ipList.get((this.ipList.indexOf(WiFiBroad.myIP) + 1)
				% ipList.size());
		if (next == null || next.isClosed()) {
			try {
				next = new Socket(nextIP, SERVER_PORT);
			} catch (Exception e) {
				e.printStackTrace();
				synchronized (this) {
					ipList.remove(nextIP);
				}
			}
		}
		String isa = ((InetSocketAddress) next.getRemoteSocketAddress())
				.getAddress().getHostAddress();
		if (isa.compareTo(nextIP) != 0) {
			try {
				next.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				next = new Socket(nextIP, SERVER_PORT);
			} catch (Exception e) {
				e.printStackTrace();
				synchronized (this) {
					ipList.remove(nextIP);
				}
			}
		}
		// TODO
		// write and wait ack
		try {
			OutputStream os = next.getOutputStream();
			os.write(TOKEN);
			os.flush();
			os.close();
			InputStream is = next.getInputStream();
			while (is.read() != ACK) {
				Thread.sleep(10);
			}
			is.close();
			next.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertToIPList(String IP) {
		synchronized (this) {
			if (ipList.contains(IP)) {
				return;
			}
			ipList.add(IP);
			Collections.sort(ipList);
		}
	}

	@Override
	public void run() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(SERVER_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				if (prev == null || prev.isClosed()) {
					prev = ss.accept();
				}
				String isa = ((InetSocketAddress) prev.getRemoteSocketAddress())
						.getAddress().getHostAddress();
				insertToIPList(isa);
				// TODO
				// 1. block and read pass token and ismyturn and write ack
				// 2. add ip ObjectMulti Line 77 and close
				InputStream is = prev.getInputStream();
				if (is.read() == this.TOKEN) {
					synchronized(this) {
						this.isMyTurn = true;
					}					
				}
				is.close();
				OutputStream os = prev.getOutputStream();
				os.write(ACK);
				os.flush();
				os.close();
				prev.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				break;
			}
		}
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
