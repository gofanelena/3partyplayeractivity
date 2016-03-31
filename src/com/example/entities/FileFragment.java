package com.example.entities;

public class FileFragment implements Comparable<FileFragment> {

	private int startIndex;
	private int stopIndex;
	private int segmentID;
	private int fragLength;
	private byte[] data;
	private boolean written = false;

	public FileFragment(int start, int stop, int segID) {

		this.startIndex = start;
		this.stopIndex = stop;
		this.segmentID = segID;
		this.fragLength = stopIndex - startIndex + 1;
		this.data = new byte[fragLength];

	}

	
	public FileFragment(FileFragment fm) throws Exception {
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


	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] d, byte[] e) {
		synchronized (this) {
			int len = d.length + e.length;
			byte[] tmp = new byte[len];
			System.arraycopy(d, 0, tmp, 0, d.length);
			System.arraycopy(e, 0, tmp, d.length, e.length);
			this.data = tmp;
			
		}
	}

	public void setStopIndex(int stopIndex) {
		this.stopIndex = stopIndex;
	}


	public void setData(byte[] d, int offset) {
		synchronized (this) {
			offset = offset - this.startIndex;
			int len = offset + d.length;
			byte[] tmpdata = new byte[len];
			System.arraycopy(this.data, 0, tmpdata, 0, this.data.length);
			System.arraycopy(d, 0, tmpdata, offset, d.length);
			this.data = tmpdata;
			this.stopIndex = this.startIndex + tmpdata.length;
			this.written = true;
		}
	}

	public void setData(byte[] d) throws Exception {
		if (data.length != d.length)
			throw new Exception("Fragment Length Wrong " + data.length + " "
					+ d.length);
		synchronized (this) {
			System.arraycopy(d, 0, this.data, 0, d.length);
			this.written = true;
		}
	}

	public boolean isWritten() {
		return written;
	}

	public int getFragLength() {
		//return data.length;
		return this.fragLength;
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
		return Integer.valueOf(this.getStartIndex()).compareTo(
				Integer.valueOf(another.getStartIndex()));
	}

	@Override
	public String toString() {
		return "Frag " + startIndex + " " + stopIndex + " " + data.length;
	}
}
