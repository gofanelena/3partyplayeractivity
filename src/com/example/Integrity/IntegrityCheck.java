package com.example.Integrity;

import android.util.SparseArray;

import com.example.celluar.CellularDown;
import com.example.entities.FileFragment;
import com.example.entities.Segment;

public class IntegrityCheck {
	// single instance mode
	private static IntegrityCheck instance;
	private SparseArray<Segment> urlMap;
	private SparseArray<String> idMap;
	private static int idCount = -1;

	private CellularDown celluDown;

	private IntegrityCheck() {
		urlMap = new SparseArray<Segment>();
		idMap = new SparseArray<String>();
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
		synchronized(this){
			if(idMap.indexOfValue(uri)>-1){
				return idMap.indexOfValue(uri);
			}
			idMap.put(++idCount, uri);
			return idCount;
		}
		
	}

	// url->3g
	private String id2url(int id) {
		// TODO Auto-generated method stub
		synchronized(this){
			String url = "http://buptant.cn" + idMap.get(id); 
			return url;
		}
		
	}

	// url->3g
	public int url2id(String url) {
		// TODO Auto-generated method stub
		synchronized(this){
			String[] s = url.split("http://buptant.cn");
			int id = idMap.indexOfValue(s[0]);
			return id;
		}
		
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
