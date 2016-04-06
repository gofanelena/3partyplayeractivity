package com.example.WiFi.WiFiBroad;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ReceiveThread extends Thread {

	Context activity;
	WifiManager wifi;
	MulticastSocket socket;
	public int signal; //2015demo w
	
	public ReceiveThread(WifiManager wifi, Context activity,
			MulticastSocket socket) {
		this.activity = activity;
		this.wifi = wifi;
		this.socket = socket;

	}

	@Override
	public void run() {

		byte[] buf = new byte[256];
		DatagramPacket packet;
		packet = new DatagramPacket(buf, buf.length);
		while (true) {
			try {
				socket.receive(packet);
				final String s = new String(packet.getData(), "UTF-8");
				Log.v("ReceiveThread", "received string:"+s);
				if(s.contains("I am Captain Device")){
				
				     String[] strarray=s.split(" ");//2015demo w
				       signal=Integer.parseInt(strarray[4]);   //2015demo w

//					handler.post(new Runnable() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							Toast.makeText(activity, "I am slave device",
//									Toast.LENGTH_LONG).show();
//						}
//
//					});
				}else if(s.contains("start play")){
					Log.v("ReceiveThread", "start play");
					
//					handler.sendEmptyMessage(123);
				}else{

					Log.v("ReceiveThread", "received:"+s+" and if equals? "+s.equals("I am Captain Device"));
//				handler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						Toast.makeText(activity, s,
//								Toast.LENGTH_LONG).show();
//					}
//
//				});
				
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
