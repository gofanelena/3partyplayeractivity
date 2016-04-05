package com.example.WiFi;

import android.util.Log;

import com.example.WiFi.WiFiBT.WiFiBT;
import com.example.WiFi.WiFiBroad.WiFiBroad;
import com.example.WiFi.WiFiNCP2.WiFiNCP2;
import com.example.WiFi.WiFiTCP.WiFiTCP;

public class WiFiFactory {
	private static final String TAG = WiFiFactory.class.getSimpleName();
	private static WiFiPulic instance;

	public enum Integer {
		TCP_ALL, BROAD, NCP2, BT
	}

	private WiFiFactory() {
	}

	public static synchronized WiFiPulic getInstance(Integer type) {
		if (instance == null) {
			Log.d(TAG, "" + type);
			switch (type) {
			case TCP_ALL:
				instance = new WiFiTCP();
				break;
			case BROAD:
				instance = new WiFiBroad();
				break;
			case NCP2:
				instance = new WiFiNCP2();
				break;
			case BT:
				instance = new WiFiBT();
				break;
			default:
				instance = new WiFiEmpty();
				break;
			}
		}
		return instance;
	}
}
