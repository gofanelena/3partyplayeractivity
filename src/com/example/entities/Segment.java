package com.example.entities;

import java.util.ArrayList;
import java.util.Collections;

public class Segment {

	private int segmentID;
	private ArrayList<FileFragment> segmentList;
	private int segLength = -1;
	private boolean Intergrity = false;

	public void setSegLength(int segLength) {
		synchronized (this) {
			if (this.segLength == -1) {
				this.segLength = segLength;
			}
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
				collectionSort();
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
					size--;
					i--;
				} else {
					segmentList.remove(i + 1);
					size--;
				}
				continue;
			} else if (prev.getStartIndex() < next.getStartIndex()) {
				prev.setData(next.getData(), next.getStartIndex());
				segmentList.remove(i + 1);
				size--;
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

	private void collectionSort() {
		synchronized (this) {
			Collections.sort(segmentList);
		}
	}

	public byte[] getData() {
		synchronized (this) {
			return segmentList.get(0).getData();
		}

	}

}
