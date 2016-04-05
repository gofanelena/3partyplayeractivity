package com.example.Integrity;

import android.util.Log;
import android.util.SparseArray;

import com.example.celluar.CellularDown;
import com.example.entities.FileFragment;
import com.example.entities.Segment;

public class IntegrityCheck {
	// single instance mode
	private static IntegrityCheck instance;
	private SparseArray<Segment> urlMap;
	public static final String URL_TAG = 
			"http://buptant.cn/autoChart/du/video/ljw2016/zxyqwe/download.php";
	public static final String URI_TAG = "http://127.1.1.1:9999/";

	private CellularDown celluDown;

	private IntegrityCheck() {
		urlMap = new SparseArray<Segment>();
		celluDown = new CellularDown();
	}



	public static synchronized IntegrityCheck getInstance() {
		if (instance == null) {
			instance = new IntegrityCheck();
		}
		return instance;
	}

	public byte[] getSegments(int uri) {
		int id = uri2id(uri);
		// Log.v("uri", uri);
		synchronized (this) {
			if (urlMap.indexOfKey(id) < 0) {
				urlMap.put(id, new Segment(id, -1));
			}
			Segment segment = urlMap.get(id);
			if (segment.checkIntegrity()) {
				return segment.getData();
			} else {
				celluDown.queryFragment(id);
				Log.v("url", "" + id);
			}
		}
		while (true) {
			synchronized (this) {
				Segment segment = urlMap.get(id);
				if (segment.checkIntegrity()) {
					return segment.getData();
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				return null;// Empty Data
			}
		}

	}

	// uri->proxy
	private int uri2id(int uri) {
		// TODO Auto-generated method stub
		synchronized (this) {
			return uri;
		}
	}

	public void setSegLength(int id, int totalLength) {
		Segment s = urlMap.get(id);
		s.setSegLength(totalLength);
	}

	public void insert(int id, FileFragment fm) {
		Segment s = urlMap.get(id);
		s.insert(fm);
	}
}
