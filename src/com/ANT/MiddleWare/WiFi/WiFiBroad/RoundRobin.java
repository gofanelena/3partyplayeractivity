package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoundRobin {
	
	private static ArrayList<Integer> ipList = new ArrayList<Integer>();
	private static boolean isMyTurn = false;
	private static RoundRobin instance;
	private int myIP;
	private static final int SERVER_PORT = 4444;
	public static boolean tcpConnected = false;
	private Socket client;
	
	private RoundRobin() {
		super();
		myIP = WiFiBroad.myIP;
		ServerThread severThd = new ServerThread(SERVER_PORT);
	}
	
	public static synchronized RoundRobin getInstance() {
		if (instance == null) {
			instance = new RoundRobin();
		}
		return instance;
	}
	
	public boolean canITalk() {
		synchronized(this){
			return isMyTurn;
		}
	}
	
	public void setMyTurn(boolean myTurn) {
		synchronized(this) {
			this.isMyTurn = myTurn;
		}
	}
	
	public int nextPerson() {
		synchronized(this) {
			return ipList.get(
					(this.ipList.indexOf(Integer.valueOf(myIP))+1)%ipList.size());
		}		
	}
	
	
	public void passToken() {
		ClientThread clientThd = new ClientThread(nextPerson(), SERVER_PORT);
	}
	
	public void insertToIPList(int IP) {
		synchronized(this) {
			if (ipList.contains(Integer.valueOf(IP))){
				return;
			}
			ipList.add(Integer.valueOf(IP));
			Collections.sort(ipList, new Comparator<Integer>() {
				@Override
				public int compare(Integer lhs, Integer rhs) {
					// TODO Auto-generated method stub
					if(lhs.intValue()>rhs.intValue()){
						return 1;
					} else {
						return 0;
					}
				}				
			});
			if (ipList.get(0).intValue() == myIP) {
				setMyTurn(true);
			}
			else {
				setMyTurn(false);
			}
		}
	}
}
