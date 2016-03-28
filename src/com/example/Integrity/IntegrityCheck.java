package com.example.Integrity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import com.example.celluar.CellularDown;
import com.example.entities.Segment;

public class IntegrityCheck{
	//single instance mode
	private static IntegrityCheck instance;
	private static HashMap<URL, Segment> urlMap;
	private Iterator iter;

	private static int segmentCount = -1;
	private CellularDown celluDown;
	
	private IntegrityCheck(){
		celluDown = new CellularDown();
		urlMap = new HashMap<URL, Segment>();
		iter = urlMap.entrySet().iterator();
		celluDown = new CellularDown();
	}
	
	public static synchronized IntegrityCheck getInstance(){
		if(instance == null){
			instance = new IntegrityCheck();
		}
		return instance;
	}
	
	public void insertUrl(URL url){
		synchronized(this){
			if(urlMap.containsKey(url)){
				return;
			}
			urlMap.put(url, new Segment(++segmentCount, -1));
		}
		
	}
	
	public void insertUrl(String url){
		try {
			URL murl = new URL(url);
			synchronized(this){
				if(urlMap.containsKey(murl)){
					return;
				}
				urlMap.put(murl, new Segment(++segmentCount, -1));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void getSegments(){
		while(true){
			
			synchronized(this){
				while (iter.hasNext()) {
				    URL key = (URL)iter.next();
					Segment segment = urlMap.get(key);
					if(segment.checkIntegrity()){
						continue;
					}else{						
						celluDown.queryFragment(segment,key);
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
	}
	

}
