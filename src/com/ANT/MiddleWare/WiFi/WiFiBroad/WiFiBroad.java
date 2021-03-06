package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;
import com.ANT.MiddleWare.WiFi.WiFiFactory;
import com.ANT.MiddleWare.WiFi.WiFiPulic;

public class WiFiBroad extends WiFiPulic {
	private static final String TAG = WiFiBroad.class.getSimpleName();

	private Process proc;
	private WifiManager wifi;
	private MulticastSocket socket = null;
	public static final String multicastHost = "224.0.0.1";
	public static final int localPort = 9988;
	private TelephonyManager tm;
	private RecvMulti recvThd = null;
	private ObjectMulti objThd = null;
	private final PipedOutputStream po = new PipedOutputStream();
	private final PipedInputStream pi = new PipedInputStream(po);
	private SendMulti sendThd;
	public static final int EMERGEN_SEND_TAG = -2;
	public static final int FRAG_REQST_TAG = -3;
	public static String myIP;
	public static final String baseIP = "192.168.1.";
	private int numIP;

	public WiFiBroad(Context contect) throws IOException, InterruptedException {
		super(contect);

		tm = (TelephonyManager) contect
				.getSystemService(Activity.TELEPHONY_SERVICE);

		// pi.connect(po);
		// po.connect(pi);

		String s = tm.getDeviceId();
		int len = s.length();
		numIP = Integer.parseInt(s.substring(len - 2));
		this.myIP = baseIP + numIP;

		// RoundRobin.getInstance().insertToIPList(myIP);

		Log.v(TAG, "ip " + myIP);
		proc = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(proc.getOutputStream());
		final DataInputStream is =new DataInputStream(proc.getInputStream());
//		os.writeBytes("dmesg 1>/data/misc/wifi/dmesg.txt\n");
//		os.writeBytes("netcfg 1>/data/misc/wifi/1.txt 2>/data/misc/wifi/err.txt\n");
		os.writeBytes("netcfg wlan0 up 1>>/data/misc/wifi/out.txt 2>>/data/misc/wifi/err.txt\n");
//		os.writeBytes("netcfg 1>/data/misc/wifi/11.txt 2>/data/misc/wifi/err.txt\n");
		os.writeBytes("wpa_supplicant -iwlan0  -t -d -c/data/misc/wifi/wpa_supplicant.conf /data/misc/wifi/out.txt\n");
//		os.writeBytes("ifconfig wlan0 1>/data/misc/wifi/2.txt 2>>/data/misc/wifi/1err.txt\n");
		os.writeBytes("ifconfig wlan0 " + myIP + " netmask 255.255.255.0 1>>/data/misc/wifi/out.txt 2>>/data/misc/wifi/err.txt\n");
		os.writeBytes("ip route add 224.0.0.0/4 dev wlan0 1>>/data/misc/wifi/out.txt 2>>/data/misc/wifi/err.txt\n");
//		os.writeBytes("ifconfig wlan0 1>/data/misc/wifi/2.txt 2>>/data/misc/wifi/1err.txt\n");
//		os.writeBytes("dmesg 1>/data/misc/wifi/dmesgaf.txt\n");
		os.writeBytes("exit\n");
		os.flush();
		proc.waitFor();
		
		new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String out = null;
			String result="";
			try {
				while((out=is.readLine())!=null) {
					result+=out;
				}
				Log.d(TAG, "proc-out:"+result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}).start();	

		wifi = (WifiManager) contect.getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			WifiManager.MulticastLock lock = wifi
					.createMulticastLock("Log_Tag");
			lock.acquire();
		}

		socket = new MulticastSocket(WiFiBroad.localPort);
		InetAddress group = InetAddress.getByName(WiFiBroad.multicastHost);
		socket.joinGroup(group);
		socket.setLoopbackMode(true);

		recvThd = new RecvMulti(po, contect, socket);
		recvThd.start();
		Log.d("wifisend", "send");
		sendThd = new SendMulti(socket, taskList,convertStack);
		sendThd.start();

		objThd = new ObjectMulti(pi, contect, sendThd);
		objThd.start();

	}

	@Override
	public void EmergencySend(byte[] data) throws FileFragmentException,
			IOException {
		FileFragment f = new FileFragment(numIP, numIP + data.length,
				EMERGEN_SEND_TAG, -1);
		f.setData(data);
		data = f.toBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length,
				InetAddress.getByName(multicastHost), localPort);
		synchronized (taskList) {
			socket.send(dp);
			Log.d("send51",String.valueOf(System.currentTimeMillis()));
		}
	}

	@Override
	public void destroy() throws InterruptedException {
		if (recvThd != null) {
			recvThd.interrupt();
			recvThd.join();
		}
		if (sendThd != null) {
			sendThd.interrupt();
			sendThd.join();
		}
		if (objThd != null) {
			objThd.interrupt();
			objThd.join();
		}
	}

	@Override
	public void notify(int seg, int start) {
		FileFragment ff = new FileFragment(seg, start, FRAG_REQST_TAG, -1);
		WiFiFactory.insertF(ff);
	}

}
