package com.ANT.MiddleWare.WiFi.WiFiTCP;

import android.content.Context;

import com.ANT.MiddleWare.WiFi.WiFiPulic;

public class WiFiTCP extends WiFiPulic {
	private static final String TAG = WiFiTCP.class.getSimpleName();

	public WiFiTCP(Context contect) {
		super(contect);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notify(int seg, int start) {
		// TODO Auto-generated method stub

	}

	@Override
	public void EmergencySend(byte[] data) {
		// TODO Auto-generated method stub
		
	}
}
