package com.ANT.MiddleWare.Entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Log;

public class FileFragment implements Comparable<FileFragment>, Serializable,
		Cloneable {
	private static final long serialVersionUID = -7869356592846635319L;

	private static final String TAG = FileFragment.class.getSimpleName();

	private int startIndex;
	private int stopIndex;
	private int segmentID;
	private byte[] data;
	private boolean written = false;

	public FileFragment(int start, int stop, int segID) {

		this.startIndex = start;
		this.stopIndex = stop;
		this.segmentID = segID;
		int fragLength = stopIndex - startIndex;
		this.data = new byte[fragLength];

	}

	public FileFragment(FileFragment fm) throws Exception {
		this.startIndex = fm.getStartIndex();
		this.stopIndex = fm.getStopIndex();
		this.segmentID = fm.getSegmentID();
		int fragLength = fm.getFragLength();
		this.data = new byte[fragLength];
		this.setData(fm.getData());
	}

	public byte[] getData() {
		synchronized (this) {
			return data.clone();
		}
	}

	public void setData(byte[] d, int offset) {
		synchronized (this) {
			Log.d(TAG, "" + startIndex + " " + stopIndex + " " + data.length
					+ " " + offset + " " + d.length);
			Log.w(TAG, "Waste " + (stopIndex - offset));
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
		return data.length;
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

	@Override
	public FileFragment clone() {
		FileFragment o = null;
		try {
			o = (FileFragment) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	public byte[] toBytes() {
		byte[] b = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(this);
			b = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public byte[] getData(int start) {
		if (start < startIndex || start >= stopIndex)
			return null;
		int len = Math.min(stopIndex - start, 16 * 1024);
		byte[] buf = new byte[len];
		synchronized (this) {
			System.arraycopy(this.data, start - startIndex, buf, 0, buf.length);
		}
		return buf;
	}
}