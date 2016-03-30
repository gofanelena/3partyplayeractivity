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

	private CellularDown celluDown;

	private IntegrityCheck() {
		urlMap = new SparseArray<Segment>();
	    idURLMap = new SparseArray<String>();
		idURIMap = new SparseArray<String>();
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
			if(idURIMap.indexOfValue(uri)<0){
				idURIMap.put(++idCount, uri);
				return idCount;
			}else{
				return idURIMap.indexOfValue(uri);
			}
			
		}
			
	}

	// url->3g
	private String id2url(int id) {
		// TODO Auto-generated method stub
		synchronized(this){
			if(idURLMap.get(id)==null){
				int segNum = getFileNumFromName(idURIMap.get(id));
				String url = "http://buptant.cn/autoChart/du/video/1/phps/"+segNum+".php";
				idURLMap.put(id, url);
				return url;
			}else{
				return idURLMap.get(id);
			}
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
	
	public int getFileNumFromName(String uri){
		String ss = null;
		for (String s : uri.split("/")) {
			if (s.contains(".mp4")) {
				ss = s;
				break;
			}
		}
		String[] tmp = ss.split(".");
		return Integer.parseInt(tmp[0]);
	}
	

}


