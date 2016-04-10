package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.DataOutputStream;
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
import com.ANT.MiddleWare.WiFi.WiFiPulic;

public class WiFiBroad extends WiFiPulic {
	private static final String TAG = WiFiBroad.class.getSimpleName();

	private Process proc;
	private WifiManager wifi;
	private MulticastSocket socket = null;
	private static String multicastHost = "224.0.0.1";
	private static int localPort = 9988;
	private TelephonyManager tm;
	private RecvMulti recvThd = null;
	private ObjectMulti objThd = null;
	private PipedInputStream pi = new PipedInputStream();
	private PipedOutputStream po = new PipedOutputStream();

	public final static int EmeSend = -2;

	public WiFiBroad(Context contect) throws Exception {
		super(contect);

		tm = (TelephonyManager) contect
				.getSystemService(Activity.TELEPHONY_SERVICE);

		pi.connect(po);

		String s = tm.getDeviceId();
		int len = s.length();
		int number = Integer.parseInt(s.substring(len - 2));
		String ip = "192.168.1." + number;
		Log.v(TAG, "ip " + ip);
		proc = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(proc.getOutputStream());
		os.writeBytes("netcfg wlan0 up\n");
		os.writeBytes("wpa_supplicant -iwlan0 -c/data/misc/wifi/wpa_supplicant.conf -B\n");
		os.writeBytes("ifconfig wlan0 " + ip + " netmask 255.255.255.0\n");
		os.writeBytes("ip route add 224.0.0.0/4 dev wlan0\n");
		os.writeBytes("exit\n");
		os.flush();
		proc.waitFor();

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

		objThd = new ObjectMulti(pi, contect);
		objThd.start();
	}

	@Override
	public void EmergencySend(byte[] data) throws Exception {
		FileFragment f = new FileFragment(0, data.length, EmeSend);
		f.setData(data);
		data = f.toBytes();
		DatagramPacket dp = new DatagramPacket(data, data.length,
				InetAddress.getByName(multicastHost), localPort);

		socket.send(dp);
	}

	@Override
	public void destroy() throws InterruptedException {
		if (recvThd != null) {
			recvThd.interrupt();
			recvThd.join();
		}
		if (objThd != null) {
			objThd.interrupt();
			objThd.join();
		}
	}

	@Override
	public void notify(int seg, int start) {
		// TODO Auto-generated method stub

	}
}
