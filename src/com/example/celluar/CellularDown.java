package com.example.celluar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.example.celluar.SingleCell.SingleCell;
import com.example.celluar.cellmore.CellularMore;
import com.example.celluar.wifimore.WiFiMore;

public class CellularDown {
	private static final String TAG = CellularDown.class.getSimpleName();
	private static final ExecutorService cachedThreadPool = Executors
			.newCachedThreadPool();
	private static CellType m_type = CellType.Single;

	public static enum CellType {
		CellMore, WiFiMore, Single, DASH, GROUP
	}

	private CellularDown() {
	}

	public static void queryFragment(CellType type, int url) {
		Log.d(TAG, "" + type + " " + url);
		m_type = type;
		switch (type) {
		case Single:
			cachedThreadPool.execute(new SingleCell(url));
			break;
		case CellMore:
			cachedThreadPool.execute(new CellularMore(url));
			break;
		case WiFiMore:
			cachedThreadPool.execute(new WiFiMore(url));
			break;
		default:
			break;
		}
	}

	public static void queryFragment(int url) {
		queryFragment(m_type, url);
	}

}
