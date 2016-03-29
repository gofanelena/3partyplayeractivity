package com.example.Integrity;

import android.util.SparseArray;

import com.example.celluar.CellularDown;
import com.example.entities.FileFragment;
import com.example.entities.Segment;

public class IntegrityCheck {
	// single instance mode
	private static IntegrityCheck instance;
	private SparseArray<Segment> urlMap;
<<<<<<< HEAD
	private SparseArray<String> idMap;
	private static int idCount = -1;
=======
>>>>>>> 0e38726c132f4c59b49122a8f05af468b1ac18f5

	private CellularDown celluDown;

	private IntegrityCheck() {
		urlMap = new SparseArray<Segment>();
<<<<<<< HEAD
		idMap = new SparseArray<String>();
=======
>>>>>>> 0e38726c132f4c59b49122a8f05af468b1ac18f5
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
<<<<<<< HEAD
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
=======
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
>>>>>>> 0e38726c132f4c59b49122a8f05af468b1ac18f5
		}

	}

	// uri->proxy
	private int uri2id(String uri) {
		// TODO Auto-generated method stub
		return 0;
	}

	// url->3g
	private String id2url(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	// url->3g
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
<<<<<<< HEAD

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
=======
>>>>>>> 0e38726c132f4c59b49122a8f05af468b1ac18f5

}
