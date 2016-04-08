package com.ANT.MiddleWare.Entities;

import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

public class Segment {
	private static final String TAG = Segment.class.getSimpleName();

	private int segmentID;
	private ArrayList<FileFragment> segmentList;
	private int segLength = -1;
	private boolean Intergrity = false;

	public synchronized void setSegLength(int segLength) {
		if (this.segLength == -1) {
			this.segLength = segLength;
		}
	}

	public Segment(int id, int length) {
		this.segmentID = id;
		this.segLength = length;
		segmentList = new ArrayList<FileFragment>();
	}

	public boolean insert(FileFragment fm) {
		synchronized (this) {
			if (fm.isWritten() && fm.getSegmentID() == segmentID) {
				segmentList.add(fm);
				Collections.sort(segmentList);
				return true;
			}
			return false;
		}
	}

	private void merge() throws Exception {
		if (segmentList == null || segmentList.size() == 1) {
			return;
		}
		int size = segmentList.size();
		for (int i = 0; i < size - 1; i++) {
			FileFragment prev = segmentList.get(i);
			FileFragment next = segmentList.get(i + 1);
			if (prev.getStopIndex() < next.getStartIndex()) {
				continue;
			}
			if (prev.getStartIndex() == next.getStartIndex()) {
				if (prev.getFragLength() <= next.getFragLength()) {
					segmentList.remove(i);
				} else {
					segmentList.remove(i + 1);
				}
				size--;
				i--;
				continue;
			} else if (prev.getStartIndex() < next.getStartIndex()) {
				if (prev.getStopIndex() < next.getStopIndex()) {
					Log.d(TAG,
							"" + next.getStartIndex() + " "
									+ prev.getStopIndex());
					prev.setData(next.getData(), next.getStartIndex());
				}
				segmentList.remove(i + 1);
				size--;
				i--;
			} else {
				throw new Exception("Not Sort");
			}
		}
	}

	public boolean checkIntegrity() {
		if (Intergrity)
			return true;
		synchronized (this) {
			if (segmentList == null) {
				return false;
			}
			try {
				merge();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (segmentList.size() == 1
					&& segmentList.get(0).getFragLength() == segLength) {
				Intergrity = true;
				return true;
			}
			return false;
		}

	}

	public byte[] getData() {
		synchronized (this) {
			if (segmentList.size() == 0)
				return null;
			return segmentList.get(0).getData();
		}

	}

	public byte[] getData(int start) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		synchronized (this) {
			if (segmentList == null || segmentList.size() == 0) {
				return null;
			}
			try {
				merge();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (FileFragment f : segmentList) {
				byte[] tmp = f.getData(start);
				if (tmp != null)
					return tmp;
			}
			return null;
		}
	}

	public FileFragment getFragment(int start) throws Exception {
		byte[] data = getData(start);
		if (data == null)
			return null;
		FileFragment f = new FileFragment(start, start + data.length, segmentID);
		f.setData(data);
		return f;
	}

	public int getMiss() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		synchronized (this) {
			if (segmentList == null || segmentList.size() == 0) {
				return 0;
			}
			try {
				merge();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return segmentList.get(0).getStopIndex();
		}
	}
}