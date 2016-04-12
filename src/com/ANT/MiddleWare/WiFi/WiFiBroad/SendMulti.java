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

	public SendMulti(MulticastSocket mSocket, Stack<FileFragment> mTaskList) {
		this.socket = mSocket;
		this.taskList = mTaskList;
	}

	private boolean send(FileFragment f) {
		try {
			byte[] data = f.toBytes();
			DatagramPacket dp = new DatagramPacket(data, data.length,
					InetAddress.getByName(WiFiBroad.multicastHost),
					WiFiBroad.localPort);
			Log.d(TAG, "send");
			socket.send(dp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		while (true) {
			synchronized (taskList) {
				if (taskList.empty()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				FileFragment ff = taskList.pop();
				if (ff.isTooBig()) {
					FileFragment[] fragArray = null;
					try {
						fragArray = ff.split();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					for (FileFragment f : fragArray) {
						boolean is = send(f);
						if (!is) {
							taskList.add(f);
						}
					}
				} else {
					boolean is = send(ff);
					if (!is) {
						taskList.add(ff);
					}
				}
			}
		}
	}
}
