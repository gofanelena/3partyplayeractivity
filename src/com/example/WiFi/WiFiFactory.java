package com.example.WiFi;

import android.util.Log;

import com.example.WiFi.WiFiBT.WiFiBT;
import com.example.WiFi.WiFiBroad.WiFiBroad;
import com.example.WiFi.WiFiNCP2.WiFiNCP2;
import com.example.WiFi.WiFiTCP.WiFiTCP;

public class WiFiFactory {
	private static final String TAG = WiFiFactory.class.getSimpleName();

	public static enum Integer {
		EMPTY, TCP_ALL, BROAD, NCP2, BT
	}

	private static WiFiPulic instance = new WiFiEmpty();
	private static Integer ins_type = WiFiFactory.Integer.EMPTY;

	private WiFiFactory() {
	}

	public static synchronized WiFiPulic getInstance(Integer type) {
		if (type != WiFiFactory.ins_type) {
			Log.d(TAG, "INS_TYPE " + WiFiFactory.ins_type + " " + type);
			instance.destroy();
			instance = null;
		}
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
