package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.ObjectInputStream;
import java.io.PipedInputStream;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.WiFi.WiFiFactory;

public class ObjectMulti extends Thread {

	private Context activity;
	private ObjectInputStream oi;

	public ObjectMulti(PipedInputStream pi, Context activity) {
		this.activity = activity;
		try {
			oi = new ObjectInputStream(pi);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				final FileFragment ff = (FileFragment) oi.readObject();
				if (ff != null) {
					((Activity) activity).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, ff.toString(),
									Toast.LENGTH_SHORT).show();
						}
					});
					switch (ff.getSegmentID()) {
					case WiFiBroad.FRAG_REQST_TAG:
						WiFiFactory.insertF(ff);
						break;
					case WiFiBroad.EMERGEN_SEND_TAG:
						break;
					default:
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
