package com.ANT.MiddleWare.WiFi.WiFiNCP2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.ANT.MiddleWare.Entities.FileFragment;
	
public class SendThread extends Thread {
	private static final String TAG = SendThread.class.getSimpleName();
	private Context context;
	private ServerSocketChannel ssChannel;
	private SocketChannel sChannel;
	private Stack<FileFragment> taskList;
	private ArrayList<FileFragment> segmentList;
	private boolean advertisement=false;
	private boolean sendData=false;
	private int segID;
	private int start;
	private int stop;
	public SendThread(Context context,Stack<FileFragment> taskList) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.taskList=taskList;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			ssChannel=ServerSocketChannel.open();
			ssChannel.socket().bind(new InetSocketAddress(WiFiNCP2.port));
			ssChannel.configureBlocking(false);
			while(true){
				sChannel=ssChannel.accept();
				if(sChannel!=null&&advertisement==true){
					//advertisement
					InetAddress dstIP = sChannel.socket().getInetAddress();
					P2Message initPush =new P2Message(segID, start,stop,"initial push");
					ByteBuffer buf = ByteBuffer.allocate(48);  
					buf.clear();  
					buf.put(P2Message.toByte(initPush)); 
					
					buf.flip();  
					  
					while(buf.hasRemaining()) {  
					    sChannel.write(buf);  
					}
					advertisement=false;
				}else if(sChannel!=null&&sendData==true){
				//向一个节点发送最大分片
					while(true&&sendData){
						synchronized (taskList) {
							while(!taskList.empty()){
								FileFragment ff =taskList.pop();
								if(ff.getSegmentID()==segID){
									segmentList.add(ff.clone());
								}
							}
							Collections.sort(segmentList);
						}
						FileFragment ff=segmentList.get(segmentList.size()-1);
						byte[] data=ff.toBytes();
						ByteBuffer wbuf = ByteBuffer.allocate(48);  
						wbuf.clear();  
						wbuf.put(data); 
						
						wbuf.flip();  
						  
						while(wbuf.hasRemaining()) {  
						    sChannel.write(wbuf);  
						}
					}
				}
				ByteBuffer buf = ByteBuffer.allocate(48);  
				int bytesRead = sChannel.read(buf); 
				while(bytesRead!=-1){
					buf.flip();
					while(buf.hasRemaining()){
						buf.get();
					}
					buf.clear();
					bytesRead=sChannel.read(buf);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				sChannel.close();
				ssChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
//		try {
//			sSocket = new ServerSocket(54321);
//			Socket socket=sSocket.accept();
//			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//	        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);  
//	        pw.println("I'm captain!");
//	        Log.d(TAG, "send to client");
//			String rmessage =br.readLine();
//			Log.d(TAG, rmessage);
//			Toast.makeText(context, rmessage, Toast.LENGTH_SHORT).show();
//			sSocket.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
