package com.example.WiFi;

import android.content.Context;

import com.example.WiFi.WiFiPulic;

public class WiFiEmpty extends WiFiPulic {
	private static final String TAG = WiFiEmpty.class.getSimpleName();

	public WiFiEmpty(Context contect) {
		super(contect);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void notify(int seg, int start) {
	}
}
