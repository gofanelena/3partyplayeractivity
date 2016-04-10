package com.ANT.MiddleWare.WiFi;

import java.util.Stack;

import android.content.Context;

import com.ANT.MiddleWare.Entities.FileFragment;

public abstract class WiFiPulic {
	protected Stack<FileFragment> taskList;
	protected Context contect;

	public WiFiPulic(Context contect) {
		this.contect = contect;
	}

	@SuppressWarnings("unused")
	private void WiFiPublic() {
	}

	public  final void insertF(FileFragment fm) {
		synchronized (taskList) {
			taskList.add(fm);
		}
	}

	public abstract void notify(int seg, int start);

	public abstract void EmergencySend(byte[] data) throws Exception;

	public abstract void destroy() throws InterruptedException;
}
