package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Stack;

import android.os.Environment;
import android.provider.MediaStore.Files;
import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;
import com.ANT.MiddleWare.PartyPlayerActivity.MainFragment;
import com.ANT.MiddleWare.WiFi.WiFiFactory;

public class SendMulti extends Thread {
	private static final String TAG = SendMulti.class.getSimpleName();

	private MulticastSocket socket;
	private Stack<FileFragment> taskList;
	private Stack<FileFragment> convertStack= new Stack<FileFragment>();
	public HashSet<Integer> repeat = new HashSet<Integer>();

	public SendMulti(MulticastSocket mSocket, Stack<FileFragment> mTaskList,Stack<FileFragment> mConvertStack) {
		this.socket = mSocket;
		this.taskList = mTaskList;
		this.convertStack=mConvertStack;
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
		Log.d(TAG, "send");
		if (isRepeat(f)) {
			return true;
		}
		if (MainFragment.configureData.isNoWiFiSend())
			return true;
		try {
			byte[] data = f.toBytes();
			//���Է���ʱ��д���ļ�
			String startOffset=String.valueOf(f.getStartIndex());
			String stopOffset=String.valueOf(f.getStopIndex());
			String segId=String.valueOf(f.getSegmentID());
			String flen=String.valueOf(f.getStopIndex()-f.getStartIndex());
			SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss:SSS");
			Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
			String str = format.format(curDate);
			String send="sId:"+segId+"\t start:"+startOffset+"\t stop:"
			+stopOffset+"\t time:"+System.currentTimeMillis()+"\t "+str+"\n";
			String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/lbroadtest/";
			File filedir=new File(dir);
			filedir.mkdir();
			int num=MainFragment.configureData.getFileNum();
			File file=new File(dir, "lsend_63k_sp0_"+num+".txt");
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos =new FileOutputStream(file, true);
			fos.write(send.getBytes());
			fos.close();
			
			DatagramPacket dp = new DatagramPacket(data, data.length,
					InetAddress.getByName(WiFiBroad.multicastHost),
					WiFiBroad.localPort);
			//Log.d(TAG, "send ");
			socket.send(dp);
			addRepeat(f);
			//Log.d(TAG, "send");
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
//			try {
//				Thread.sleep(90);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				if (this.isInterrupted()) {
//					return;
//				}
//			}
//			if (!RoundRobin.getInstance().canITalk()) {
//				continue;
//			}
			
			FileFragment ff = null;
//			String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/lbroadsend/";
//			File filedir=new File(dir);
//			filedir.mkdir();
//			File file=new File(dir, "lsend_2k_sp0_.txt");
//			if(!file.exists()){
//				try {
//					file.createNewFile();
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			FileOutputStream fos;
//			try {
//				fos = new FileOutputStream(file, true);
//				//fos.write(("beforesyn:"+System.currentTimeMillis()+" \t").getBytes());
//				fos.close();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
			synchronized (convertStack) {
				
				if (convertStack.empty()) {
					//RoundRobin.getInstance().passToken();
					continue;
				}
				ff = convertStack.pop();
				//Log.d("localsend",ff.toString()+" "+ff.getSegmentID()+" "+System.currentTimeMillis());
			}
//			try {
//				fos = new FileOutputStream(file, true);
//				fos.write(("aftersyn:"+System.currentTimeMillis()+"\t\n").getBytes());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Log.d("aftersyn", " "+System.currentTimeMillis());
			if (ff == null)
				continue;
			if (ff.isTooBig()) {
				Log.d("sendtoobig", ff.toString()+" "+ff.getSegmentID());
				WiFiFactory.insertF(ff);
			} else {
				Log.d("beforesend",  " "+System.currentTimeMillis());
				boolean is = send(ff);
				Log.d("aftersend",  " "+System.currentTimeMillis());
				if (!is) {
					Log.d("sendfail", ff.toString()+" "+ff.getSegmentID());
					synchronized (convertStack) {
						convertStack.add(ff);
					}
				}
			}
		}
	}
}
