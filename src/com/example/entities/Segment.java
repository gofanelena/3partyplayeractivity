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
			
			//ljw 2016 3 31
			if (prev.getStopIndex() == next.getStartIndex()-1) {
				prev.setFragLength(prev.getFragLength()+next.getFragLength());
				prev.setData(prev.getData(), next.getData());
				prev.setStopIndex(next.getStopIndex());
				segmentList.remove(i + 1);
				size--;
				i--;
				continue;
			}
			
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

	private void collectionSort() {
		synchronized (this) {
			Collections.sort(segmentList);
		}
	}

	public byte[] getData() {
		if (segmentList.size() == 0)
			return null;
		synchronized (this) {
			return segmentList.get(0).getData();
		}

	}

}
