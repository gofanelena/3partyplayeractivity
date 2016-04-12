package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Stack;

import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;

public class SendMulti extends Thread {
	private static final String TAG = SendMulti.class.getSimpleName();

	private MulticastSocket socket;
	private Stack<FileFragment> taskList;
	private FileFragment[] fragArray;

	public SendMulti(MulticastSocket mSocket, Stack<FileFragment> mTaskList) {
		this.socket = mSocket;
		this.taskList = mTaskList;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (taskList) {				
				if(!taskList.empty()) {
					if (taskList.peek() != null) {
						FileFragment ff = taskList.pop();
						try {
							fragArray = ff.split();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						for (int i = 0; i < fragArray.length; i++) {
							byte[] data = fragArray[i].toBytes();
							try {
								DatagramPacket dp = new DatagramPacket(data,
										data.length,
										InetAddress.getByName(WiFiBroad.multicastHost),
										WiFiBroad.localPort);
								Log.d(TAG, "send");
								socket.send(dp);
							} catch (Exception e) {
								taskList.push(ff);
								e.printStackTrace();
								break;
							}
						}						
					}
				}
			}
		}
	}
}
