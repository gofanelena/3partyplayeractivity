package com.example.Integrity;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.example.celluar.CellularDown;
import com.example.entities.FileFragment;
import com.example.entities.Segment;

public class IntegrityCheck {
	// single instance mode
	private static IntegrityCheck instance;
	private SparseArray<Segment> urlMap;
	private SparseArray<Integer> idURIMap;
	private static int idCount = -1;
	public static final String URL_TAG = "http://buptant.cn/autoChart/du/video/ljw2016/zxyqwe/download.php";
	public static final String URI_TAG = "http://127.1.1.1:9999/";
	private static final int TOTAL_SEGS = 5;

	private CellularDown celluDown;

	private IntegrityCheck() {
		urlMap = new SparseArray<Segment>();
		idURIMap = new SparseArray<Integer>();
		insertURLtoMaps(idURIMap);
		celluDown = new CellularDown();
	}

	private void insertURLtoMaps(SparseArray<Integer> idUri) {
		for (int i = 1; i <= TOTAL_SEGS; i++) {
			idUri.put(++idCount, Integer.valueOf(i));//URI_TAG + i + ".mp4");
			// Log.v("map", idUrl.get(0));
		}

	}

	public static synchronized IntegrityCheck getInstance() {
		if (instance == null) {
			instance = new IntegrityCheck();
		}
		return instance;
	}

	public byte[] getSegments(String uri) {
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
	private int uri2id(String uri) {
		// TODO Auto-generated method stub
		synchronized (this) {
			int count = getIndex(uri);
			boolean t = false;
			int i = 0;
			while (!t) {
				if (i == TOTAL_SEGS) {
					return -2;
				}
				t = idURIMap.get(i).intValue()==count;
				i++;

			}
			// return idURIMap.indexOfValue(uri);
			return (--i);
		}
	}
	private int getIndex(String uri){
		String fileName = "";
		for (String s : uri.split("/")) {
			if (s.contains(".mp4")) {
				fileName = s;
				break;
			}			
		}		
		return Integer.parseInt(fileName.substring(0, 1));
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
