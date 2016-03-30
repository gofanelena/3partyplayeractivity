package com.example.entities;

public class FileFragment implements Comparable<FileFragment> {

	private int fragLength;
	private int startIndex;
	private int stopIndex;
	private int segmentID;
	private byte[] data;
	private boolean written = false;

	public FileFragment(int start, int stop, int segID) {

		this.startIndex = start;
		this.stopIndex = stop;
		this.segmentID = segID;
		this.fragLength = stopIndex - startIndex + 1;
		this.data = new byte[fragLength];

	}

	public FileFragment(FileFragment fm) {
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

	public void setData(byte[] data, int offset) {
		synchronized (this) {
			offset = offset - this.startIndex;
			int len = data.length;
			byte[] tmpdata = new byte[len];
			System.arraycopy(this.data, 0, tmpdata, 0, this.data.length);
			System.arraycopy(data, 0, tmpdata, offset, data.length);
			this.data = tmpdata;
			this.written = true;
		}
	}

	public void setData(byte[] data) {
		synchronized (this) {
			System.arraycopy(data, 0, this.data, 0, fragLength);
			this.written = true;
		}
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
		return Integer.valueOf(this.getStartIndex()).compareTo(
				Integer.valueOf(another.getStartIndex()));
	}

}
