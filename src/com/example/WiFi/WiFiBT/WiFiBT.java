package com.example.WiFi.WiFiBT;

import android.content.Context;

import com.example.WiFi.WiFiPulic;

public class WiFiBT extends WiFiPulic {
	private static final String TAG = WiFiBT.class.getSimpleName();

	public WiFiBT(Context contect) {
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
