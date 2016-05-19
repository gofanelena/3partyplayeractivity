package com.ANT.MiddleWare.WiFi.WiFiNCP2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;
import com.ANT.MiddleWare.WiFi.WiFiFactory;
import com.ANT.MiddleWare.WiFi.WiFiPulic;

public class WiFiNCP2 extends WiFiPulic {
	private static final String TAG = WiFiNCP2.class.getSimpleName();
	private TelephonyManager tm;
	private Process proc;
//	private WifiManager wm;
//	private WifiInfo wi;
	public static final int port=54321;
	public static String host=null;
	private SendThread sendThd =null;
	private RecvThread recvThd =null;
//	private BufferedReader br ;
//	private PrintWriter pw ;
	public static final int EMERGEN_SEND_TAG = -2;
	public static final int FRAG_REQST_TAG = -3;

	public WiFiNCP2(Context contect) throws IOException, InterruptedException {
		super(contect);
		
		tm=(TelephonyManager) contect.getSystemService(Activity.TELEPHONY_SERVICE);
		String s =tm.getDeviceId();
		int len = s.length();
		int number = Integer.parseInt(s.substring(len - 2));
		String ip = "192.168.1." + number;
		Log.v(TAG, "ip: " + ip);
		proc = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(proc.getOutputStream());
		os.writeBytes("netcfg wlan0 up\n");
		os.writeBytes("wpa_supplicant -iwlan0 -c/data/misc/wifi/wpa_supplicant.conf -B\n");
		os.writeBytes("ifconfig wlan0 " + ip + " netmask 255.255.255.0\n");
		os.writeBytes("route add 192.168.1.0/24 dev wlan0\n");
		os.writeBytes("exit\n");
		os.flush();
		proc.waitFor();
		
//		wm=(WifiManager) contect.getSystemService(Context.WIFI_SERVICE);
//		wi = wm.getConnectionInfo();
//		int ipAddress=wi.getIpAddress();
//		Log.d(TAG, "dhcp ipAddress:"+ipAddress);
//		String ipString ="";
//		if (ipAddress != 0) {  
//		       ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."   
//		        + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));  
//		} 
//		Log.d(TAG, "ip:"+ipString);
		
		host="192.168.1.89";
		Log.d(TAG, "serverHost:"+host);
		
		if(ip.equals(host)){
			sendThd=new SendThread(contect,taskList);
			sendThd.start();
		}else{
			recvThd=new RecvThread(contect);
			recvThd.start();
		}
	}

	@Override
	public void destroy() throws InterruptedException {
		// TODO Auto-generated method stub
		if (recvThd != null) {
			recvThd.interrupt();
			recvThd.join();
		}
		
		if (sendThd != null) {
			sendThd.interrupt();
			sendThd.join();
		}
	}

	@Override
	public void notify(int seg, int start) {
		// TODO Auto-generated method stub
		FileFragment ff = new FileFragment(start,FRAG_REQST_TAG, seg,-1);
		WiFiFactory.insertF(ff);
	}

	@Override
	public void EmergencySend(byte[] data) throws FileFragmentException {
		// TODO Auto-generated method stub
		synchronized (taskList) {
			if(!taskList.empty()){
				taskList.pop();
			}
		}
		FileFragment f=new FileFragment(0, data.length,EMERGEN_SEND_TAG , -1);
		f.setData(data);
		data=f.toBytes();
		
	}
}
