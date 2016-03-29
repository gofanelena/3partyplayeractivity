package com.example.entities;

import java.util.ArrayList;
import java.util.Collections;

public class Segment {

	private int segmentID;
	private ArrayList<FileFragment> segmentList;
	private int segLength = -1;
	private boolean Intergrity = false;

	public void setSegLength(int segLength) {
		if (this.segLength == -1) {
			synchronized (this) {
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
				// sortSegment();
				collectionSort();
				return true;
			}
			return false;
		}

	}

	private void merge() {

		if (segmentList == null || segmentList.size() == 1) {
			return;
		}
		int size = segmentList.size();
		for (int i = 0; i < size - 1; i++) {
			if (segmentList.get(i).getStartIndex() == segmentList.get(i + 1)
					.getStartIndex()) {
				if (segmentList.get(i).getFragLength() <= segmentList
						.get(i + 1).getFragLength()) {
					segmentList.remove(i);
					size--;
				} else {
					segmentList.remove(i + 1);
					size--;
				}
			}
			if (segmentList.get(i).getStopIndex() >= segmentList.get(i + 1)
					.getStartIndex()) {
				if (segmentList.get(i).getStopIndex() >= segmentList.get(i + 1)
						.getStopIndex()) {
					segmentList.remove(i + 1);
					size--;
				} else {
					segmentList.get(i).setStopIndex(
							segmentList.get(i + 1).getStopIndex());
					int newLength = segmentList.get(i + 1).getStopIndex()
							- segmentList.get(i).getStartIndex() + 1;
					segmentList.get(i).setFragLength(newLength);
					segmentList.get(i).setData(
							segmentList.get(i).getData(),
							segmentList.get(i + 1).getData(),
							segmentList.get(i).getStopIndex()
									- segmentList.get(i + 1).getStartIndex()
									+ 1, newLength);
					segmentList.remove(i + 1);
					size--;
				}
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
			merge();

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
		// TODO Auto-generated method stub
		synchronized(this){
			return segmentList.get(0).getData();
		}
		
	}

}
