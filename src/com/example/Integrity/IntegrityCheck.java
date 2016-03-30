package com.example.Integrity;

import android.util.SparseArray;

import com.example.celluar.CellularDown;
import com.example.entities.FileFragment;
import com.example.entities.Segment;

public class IntegrityCheck {
	// single instance mode
	private static IntegrityCheck instance;
	private SparseArray<Segment> urlMap;
	private SparseArray<String> idURLMap;
	private SparseArray<String> idURIMap;
	private static int idCount = -1;  
    public static final String URL_TAG = "http://buptant.cn/autoChart/du/video/ljw2016/phps/";
    public static final String URI_TAG = "http://127.1.1.1:9999/";
    private static final int TOTAL_SEGS = 5;

	private CellularDown celluDown;

	private IntegrityCheck() {
		urlMap = new SparseArray<Segment>();
		idURLMap = new SparseArray<String>();
		idURIMap = new SparseArray<String>();
		insertURLtoMaps(idURLMap, idURIMap);
		celluDown = new CellularDown();
	}
	
	private void insertURLtoMaps(SparseArray<String> idUrl, SparseArray<String> idUri){
		for(int i = 1; i<= TOTAL_SEGS; i++){
			idUri.put(++idCount, URI_TAG+i+".mp4");
			idUrl.put(idCount, URL_TAG+i+".php");
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
			
			return idURIMap.indexOfValue(uri);
		
			
		}
			
	}

	// url->3g
	private String id2url(int id) {
		// TODO Auto-generated method stub
		synchronized(this){
			
			return idURLMap.get(id);
			
		}
	}

	// url->3g
	public int url2id(String url) {
		// TODO Auto-generated method stub
		synchronized(this){
			return idURLMap.indexOfValue(url);
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


