package com.ANT.MiddleWare.WiFi;

import java.io.IOException;
import java.util.Stack;

import android.content.Context;
import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;

public abstract class WiFiPulic {
	protected final static Stack<FileFragment> taskList = new Stack<FileFragment>();
	protected Stack<FileFragment> convertStack= new Stack<FileFragment>();
	protected Context contect;

	public WiFiPulic(Context contect) {
		this.contect = contect;
	}

	@SuppressWarnings("unused")
	private void WiFiPublic() {
	}

	public final void insertF(FileFragment fm) {
		if (fm.isTooBig()) {
			FileFragment[] fragArray = null;
//			Log.d("wifiBroadinsert", fm.toString()+" "+fm.getSegmentID());
			try {
				fragArray = fm.split();
			} catch (FileFragmentException e) {
				e.printStackTrace();
			}
			synchronized (taskList) {
//				int i=0;
				for (FileFragment f : fragArray) {
					taskList.add(f);
//					Log.d("broadtasklistadd", f.toString()+" "+f.getSegmentID());
//					if (!taskList.empty()) {
//						Log.d("insertforeach",taskList.peek().toString()+" "+taskList.peek().getSegmentID());
//					}
//					if (taskList.empty()) {
//						Log.d("tasklistempty", "empty");
//						continue;
//					}else{
//					 while (true) { 
//	                       Log.d("insertforeach",taskList.pop().toString()+" "+taskList.pop().getSegmentID()); 
//	                } 
//					}
					
//					 for (FileFragment x : taskList) { 
//					  Log.d("tasklistforeach", x.toString()+" "+x.getSegmentID()+" "+i);  
//	                } 
//					 i++;
				}
				while (!taskList.empty()) {
//			         Log.d("convert", "before enqueue,taskList size: " + String.valueOf(taskList.size()));
			           FileFragment ff = taskList.pop();
			           convertStack.add(ff);
			    }
			}
		} else {
			synchronized (taskList) {
				taskList.add(fm);
			}
		}
	}

	public abstract void notify(int seg, int start);

	public abstract void EmergencySend(byte[] data)
			throws FileFragmentException, IOException;

	public abstract void destroy() throws InterruptedException;
}
