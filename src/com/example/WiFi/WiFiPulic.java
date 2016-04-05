package com.example.WiFi;

import java.util.Stack;

import com.example.entities.FileFragment;

public abstract class WiFiPulic {
	protected Stack<FileFragment> taskList;

	public final void insertF(FileFragment fm) {
		synchronized (taskList) {
			taskList.add(fm);
		}
	}

	public abstract void init();

	public abstract void notify(int start);
}
