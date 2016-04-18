package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
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
