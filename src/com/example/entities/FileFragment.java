package com.example.entities;

public class FileFragment implements Comparable<FileFragment>{
	
	private int fragLength;
	private int startIndex;
	private int stopIndex;
	private int segmentID;
	private byte[] data;
	private boolean written = false;
	
	public FileFragment(int start, int stop, int segID){
		
		this.startIndex = start;
		this.stopIndex = stop;
		this.segmentID = segID;
		this.fragLength = stopIndex - startIndex + 1;
		this.data = new byte[fragLength];
		
	}
	
	public FileFragment(FileFragment fm){
		this.startIndex = fm.getStartIndex();
		this.stopIndex = fm.getStopIndex();
		this.segmentID = fm.getSegmentID();
		this.fragLength = fm.getFragLength();
		this.data = new byte[fragLength];
		this.setData(fm.getData());
	}

	public void setFragLength(int fragLength) {
		this.fragLength = fragLength;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public void setStopIndex(int stopIndex) {
		this.stopIndex = stopIndex;
	}

	public void setSegmentID(int segmentID) {
		this.segmentID = segmentID;
	}

	public void setWritten(boolean written) {
		this.written = written;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data1, byte[] data2, int offset, int newLength){
		this.data = new byte[newLength];
		System.arraycopy(data1, 0, data, 0, data1.length);
		System.arraycopy(data2, offset, data, data1.length, data2.length-offset);
		this.written = true;
	}

	public void setData(byte[] data) {
		System.arraycopy(data, 0, this.data, 0, fragLength);
		this.written = true;
	}

	public boolean isWritten() {
		return written;
	}

	public int getFragLength() {
		return fragLength;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getStopIndex() {
		return stopIndex;
	}

	public int getSegmentID() {
		return segmentID;
	}

	@Override
	public int compareTo(FileFragment another) {
		// TODO Auto-generated method stub
		return new Integer(this.getStartIndex()).compareTo(new Integer(another.getStartIndex()));
	}


		

}
