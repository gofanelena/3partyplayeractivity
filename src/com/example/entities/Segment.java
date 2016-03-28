package com.example.entities;

import java.util.ArrayList;
import java.util.Collections;

public class Segment {
	
	private int segmentID;
	private ArrayList<FileFragment> segmentList;
	private int segLength = -1;
	
	public int getSegLength() {
		synchronized(this){
			return segLength;
		}	
	}

	public void setSegLength(int segLength) {
		synchronized(this){
			this.segLength = segLength;
		}		
	}

	public int getSegmentID() {
		synchronized(this){
			return segmentID;
		}	
	}

	public Segment(int id, int length){
		this.segmentID = id;
		this.segLength = length;
		segmentList = new ArrayList<FileFragment>();
	}
	
	public boolean insert(FileFragment fm){
		synchronized(this){
			if (fm.isWritten()&&fm.getSegmentID()==segmentID){
				segmentList.add(fm);
				//sortSegment();
				collectionSort();
				return true;
			}
			return false;
		}
		
	}
	
	public void sortSegment(){
		int nEle = segmentList.size();
		recQuickSort(0, nEle-1);
	}
	
	public void recQuickSort(int left, int right){
		if(right-left <= 0){
			return;
		}else{
			int pivot = segmentList.get(right).getStartIndex();
			int partition = partitionIt(left, right, pivot);
			recQuickSort(left, partition-1);
			recQuickSort(partition, right);
		}
		
	}
	
	private int partitionIt(int left, int right, int pivot){
		int leftPtr = left-1;
		int rightPtr = right;
		while(true){
			while(segmentList.get(++leftPtr).getStartIndex() < pivot){				
			}
			while(rightPtr>0 && segmentList.get(--rightPtr).getStartIndex() > pivot){				
			}
			if(leftPtr >= rightPtr){
				break;
			}else{
				swap(leftPtr,rightPtr);
			}			
		}
		swap(leftPtr, right);
		return leftPtr;
	}
	
	private void swap(int left, int right){
		FileFragment fmTemp = new FileFragment(segmentList.get(left));
		segmentList.set(left, segmentList.get(right));
		segmentList.set(right, fmTemp);
		
	}
	
	private void merge(){
		
		if(segmentList==null||segmentList.size()==1){
			return;
		}
		int size = segmentList.size();
		for (int i=0; i<size-1; i++){
			if(segmentList.get(i).getStartIndex() ==
					segmentList.get(i+1).getStartIndex()){
				if(segmentList.get(i).getFragLength() <=
						segmentList.get(i+1).getFragLength()){
					segmentList.remove(i);
					size--;
				}else{
					segmentList.remove(i+1);
					size--;
				}
			}
			if(segmentList.get(i).getStopIndex() >= 
					segmentList.get(i+1).getStartIndex()){
				if(segmentList.get(i).getStopIndex() >=
						segmentList.get(i+1).getStopIndex()){
					segmentList.remove(i+1);
					size--;
				}else{
					segmentList.get(i).setStopIndex(segmentList.get(i+1).getStopIndex());
					int newLength = segmentList.get(i+1).getStopIndex()-segmentList.
							get(i).getStartIndex()+1;
					segmentList.get(i).setFragLength(newLength);
					segmentList.get(i).setData(segmentList.get(i).getData(), segmentList.get(i+1).getData(), 
							segmentList.get(i).getStopIndex()-segmentList.get(i+1).getStartIndex()+1,
							newLength);
					segmentList.remove(i+1);
					size--;
				}
			}
		}
	}
	
	public boolean checkIntegrity(){
		synchronized(this){
			
			if(segmentList==null){
				return false;
			}
			merge();
						
			if(segmentList.size()==1 && segmentList.get(0).getFragLength()==segLength){
				return true;
			}
			return false;
		}
		
		
	}

	public ArrayList<FileFragment> getSegmentList() {
		synchronized(this){
			return segmentList;
		}
		
	}
	
	private void collectionSort(){
		synchronized(this){
			Collections.sort(segmentList);
		}
	}
	
	
	
}
