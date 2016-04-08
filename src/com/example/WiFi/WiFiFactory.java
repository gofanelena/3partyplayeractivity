package com.example.WiFi;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.WiFi.WiFiBT.WiFiBT;
import com.example.WiFi.WiFiBroad.WiFiBroad;
import com.example.WiFi.WiFiNCP2.WiFiNCP2;
import com.example.WiFi.WiFiTCP.WiFiTCP;
import com.example.entities.FileFragment;

public class WiFiFactory {
	private static final String TAG = WiFiFactory.class.getSimpleName();

	public static enum WiFiType {
		EMPTY, TCP_ALL, BROAD, NCP2, BT
	}

	private static WiFiPulic instance = new WiFiEmpty(null);
	private static WiFiType ins_type = WiFiFactory.WiFiType.EMPTY;

	private WiFiFactory() {
	}

	public static void changeInstance(Context contect, WiFiType type) {
		synchronized (instance) {
			if (type != WiFiFactory.ins_type) {
				Log.d(TAG, "INS_TYPE " + WiFiFactory.ins_type + " " + type);
				instance.destroy();
				instance = null;
			}
			if (instance == null) {
				Log.d(TAG, "" + type);
				switch (type) {
				case TCP_ALL:
					instance = new WiFiTCP(contect);
					break;
				case BROAD:
					instance = new WiFiBroad(contect);
					break;
				case NCP2:
					instance = new WiFiNCP2(contect);
					break;
				case BT:
					instance = new WiFiBT(contect);
					break;
				default:
					instance = new WiFiEmpty(contect);
					break;
				}
			}
		}
	}

	public static void insertF(FileFragment fm) {
		synchronized (instance) {
			instance.insertF(fm);
		}
	}

	public static void notify(int seg, int start) {
		synchronized (instance) {
			instance.notify(seg, start);
		}
	}
}
