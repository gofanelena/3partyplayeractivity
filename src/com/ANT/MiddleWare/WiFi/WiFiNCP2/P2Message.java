package com.ANT.MiddleWare.WiFi.WiFiNCP2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.R.integer;

public class P2Message {
	int segmentID;
	int startOffset;
	int stopOffset;
	String message;
	
	public P2Message(int segmentID,int startOffset,int stopOffset, String message) {
		// TODO Auto-generated constructor stub
		this.segmentID=segmentID;
		this.startOffset=startOffset;
		this.stopOffset=stopOffset;
		this.message=message;
	}
	public int getSegmentID() {
		return segmentID;
	}
	public void setSegmentID(int segmentID) {
		this.segmentID = segmentID;
	}
	public int getStartOffset() {
		return startOffset;
	}
	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	public int getStopOffset() {
		return stopOffset;
	}
	public void setStopOffset(int stopOffset) {
		this.stopOffset = stopOffset;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static byte[] toByte (Object obj) {      
        byte[] bytes = null;      
        ByteArrayOutputStream bos = new ByteArrayOutputStream();      
        try {        
            ObjectOutputStream oos = new ObjectOutputStream(bos);         
            oos.writeObject(obj);        
            oos.flush();         
            bytes = bos.toByteArray ();      
            oos.close();         
            bos.close();        
        } catch (IOException ex) {        
            ex.printStackTrace();   
        }      
        return bytes;    
    }   
}
