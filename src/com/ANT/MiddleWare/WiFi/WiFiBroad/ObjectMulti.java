package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.PipedInputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.WiFi.WiFiPulic;

public class ObjectMulti extends Thread {

	private Context activity;
	private ObjectInputStream oi;

	public ObjectMulti(PipedInputStream pi, Context activity) {
		this.activity = activity;
		try {
			oi = new ObjectInputStream(pi);
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				final FileFragment ff = (FileFragment) oi.readObject();
				if (ff!=null) {
					if (ff.getSegmentID() == -3) {
						//handle fragment request
						WiFiPulic.insertF(ff);
					}
				}
				
				((Activity) activity).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity, ff.toString(),
								Toast.LENGTH_SHORT).show();
					}
				});
			} catch (OptionalDataException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
