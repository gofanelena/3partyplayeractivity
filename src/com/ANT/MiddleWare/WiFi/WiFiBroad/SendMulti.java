package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Stack;

import com.ANT.MiddleWare.Entities.FileFragment;

public class SendMulti extends Thread {

	private MulticastSocket socket;
	private Stack<FileFragment> taskList;

	public SendMulti(MulticastSocket mSocket, Stack<FileFragment> mTaskList) {
		this.socket = mSocket;
		this.taskList = mTaskList;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (taskList) {
				if (taskList.peek() != null) {
					FileFragment ff = taskList.pop();
					byte[] data = ff.toBytes();
					try {
						DatagramPacket dp = new DatagramPacket(data,
								data.length,
								InetAddress.getByName(WiFiBroad.multicastHost),
								WiFiBroad.localPort);

						socket.send(dp);
					} catch (Exception e) {
						taskList.push(ff);
						e.printStackTrace();
					}
				}
			}
		}
	}
}
