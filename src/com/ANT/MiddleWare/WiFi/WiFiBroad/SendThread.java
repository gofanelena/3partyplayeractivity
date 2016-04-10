package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Stack;

import com.ANT.MiddleWare.Entities.FileFragment;

public class SendThread extends Thread {
	
	private MulticastSocket socket;
	private Stack<FileFragment> taskList;
	private FileFragment ffToSend;
	private ByteArrayOutputStream bOs;
	private ObjectOutputStream oOs;
	
	public SendThread(MulticastSocket mSocket, Stack<FileFragment> mTaskList) {		
		this.socket = mSocket;		
		this.taskList = mTaskList;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			synchronized(taskList){
				if(taskList.peek()!= null) {
				    ffToSend = taskList.pop();
				    send(ffToSend);
				}
			}
		}
	}
	
	private void send (FileFragment ff) {
		try {
			bOs = new ByteArrayOutputStream();
			oOs = new ObjectOutputStream(bOs);
			oOs.writeObject(ff);
			DatagramPacket dp = new DatagramPacket(bOs.toByteArray(), 
					bOs.toByteArray().length,
					InetAddress.getByName(WiFiBroad.multicastHost), 
					WiFiBroad.localPort);
			socket.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				oOs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bOs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
