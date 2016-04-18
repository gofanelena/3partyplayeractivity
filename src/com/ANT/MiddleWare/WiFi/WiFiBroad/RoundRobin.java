package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoundRobin {
	
	private static ArrayList<Integer> ipList = new ArrayList<Integer>();
	private static boolean isMyTurn = false;
	private static RoundRobin instance;
	private int myIP;
	
	private RoundRobin() {
		myIP = WiFiBroad.myIP;
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
			return (this.ipList.indexOf(Integer.valueOf(myIP))+1)%ipList.size();
		}		
	}
	
	public void insertToIPList(int IP) {
		synchronized(this) {
			if (ipList.contains(Integer.valueOf(IP))){
				if (IP == myIP){
					setMyTurn(true);
				} else {
					setMyTurn(false);
				}
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
		}
	}
}
