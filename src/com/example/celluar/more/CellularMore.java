package com.example.celluar.more;

import android.util.Log;

import com.example.Integrity.IntegrityCheck;
import com.example.entities.Segment;

public class CellularMore extends Thread {
	private static final String TAG = CellularMore.class.getSimpleName();

	private int url;

	public CellularMore(int url) {
		this.url = url;
	}

	@Override
	public void run() {
		IntegrityCheck IC = IntegrityCheck.getInstance();
		Segment Seg = IC.getSeg(url);
		if (Seg != null) {
			while (!Seg.checkIntegrity()) {
				Log.d(TAG, "no");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			Log.d(TAG, "yes");
		} else {
			Log.e(TAG, "a");
		}
	}
}
