package com.example.Integrity;

import android.util.SparseArray;

import com.example.celluar.CellularDown;
import com.example.entities.FileFragment;
import com.example.entities.Segment;

public class IntegrityCheck {
	// single instance mode
	private static IntegrityCheck instance;
	private SparseArray<Segment> urlMap;

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

	public byte[] getSegments(String uri) {
		int id = uri2id(uri);
		while (true) {
			synchronized (this) {
				if (urlMap.indexOfKey(id) < 0) {
					urlMap.put(id, new Segment(id, -1));
				}
				Segment segment = urlMap.get(id);
				if (segment.checkIntegrity()) {
					return segment.getData();
				} else {
					celluDown.queryFragment(id2url(id));
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				return null;// Empty Data
			}
		}

	}

	//url->3g
	private String id2url(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	//uri->proxy
	private int uri2id(String uri) {
		// TODO Auto-generated method stub
		return 0;
	}

	//url->3g
	public int url2id(String url) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSegLength(String url, int totalLength) {
		int id = url2id(url);
		Segment s = urlMap.get(id);
		s.setSegLength(totalLength);
	}

	public void insert(String url, FileFragment fm) {
		int id = url2id(url);
		Segment s = urlMap.get(id);
		s.insert(fm);
	}

}
