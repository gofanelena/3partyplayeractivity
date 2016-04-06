package com.example.celluar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.example.celluar.SingleCell.SingleCell;
import com.example.celluar.more.CellularMore;

public class CellularDown {
	private static final String TAG = CellularDown.class.getSimpleName();
	private static final ExecutorService cachedThreadPool = Executors
			.newCachedThreadPool();

	public static enum CellType {
		More, Single, DASH, GROUP
	}

	private CellularDown() {
	}

	public static void queryFragment(CellType type, int url) {
		Log.d(TAG, "" + type + " " + url);
		switch (type) {
		default:
			cachedThreadPool.execute(new SingleCell(url));
			break;
		case Single:
			cachedThreadPool.execute(new SingleCell(url));
			break;
		case More:
			cachedThreadPool.execute(new CellularMore(url));
			break;
		}
	}

	public static void queryFragment(int url) {
		queryFragment(CellularDown.CellType.Single, url);
	}

}
