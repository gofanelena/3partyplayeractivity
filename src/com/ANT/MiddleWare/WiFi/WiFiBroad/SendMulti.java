package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Stack;

import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;
import com.ANT.MiddleWare.PartyPlayerActivity.MainFragment;
import com.ANT.MiddleWare.WiFi.WiFiFactory;

public class SendMulti extends Thread {
	private static final String TAG = SendMulti.class.getSimpleName();

	private MulticastSocket socket;
	private Stack<FileFragment> taskList;

	public HashSet<Integer> repeat = new HashSet<Integer>();

	public SendMulti(MulticastSocket mSocket, Stack<FileFragment> mTaskList) {
		this.socket = mSocket;
		this.taskList = mTaskList;
	}

	public boolean isRepeat(FileFragment f) {
		if (f.getSegmentID() > 0) {
			int hash = f.hashCode();
			synchronized (repeat) {
				if (repeat.contains(hash)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addRepeat(FileFragment f) {
		if (f.getSegmentID() > 0) {
			int hash = f.hashCode();
			synchronized (repeat) {
				repeat.add(hash);
			}
		}
	}

	public void removeRepeat(FileFragment f) {
		if (f.getSegmentID() > 0) {
			int hash = f.hashCode();
			synchronized (repeat) {
				repeat.remove(hash);
			}
		}
	}

	private synchronized boolean send(FileFragment f) {
		if (isRepeat(f)) {
			return true;
		}
		if (MainFragment.configureData.isNoWiFiSend())
			return true;
		try {
			byte[] data = f.toBytes();
			DatagramPacket dp = new DatagramPacket(data, data.length,
					InetAddress.getByName(WiFiBroad.multicastHost),
					WiFiBroad.localPort);
			Log.d(TAG, "send");
			socket.send(dp);
			addRepeat(f);
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				if (this.isInterrupted()) {
					return;
				}
			}
//			if (!RoundRobin.getInstance().canITalk()) {
//				continue;
//			}
			FileFragment ff = null;
			synchronized (taskList) {
				if (taskList.empty()) {
					//RoundRobin.getInstance().passToken();
					continue;
				}
				ff = taskList.pop();
			}
			if (ff == null)
				continue;
			if (ff.isTooBig()) {
				WiFiFactory.insertF(ff);
			} else {
				boolean is = send(ff);
				if (!is) {
					synchronized (taskList) {
						taskList.add(ff);
					}
				}
			}
		}
	}
}
