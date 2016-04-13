package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.ObjectInputStream;
import java.io.PipedInputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.Segment;
import com.ANT.MiddleWare.Integrity.IntegrityCheck;
import com.ANT.MiddleWare.WiFi.WiFiFactory;

public class ObjectMulti extends Thread {
	private static final String TAG = ObjectMulti.class.getSimpleName();

	private Context activity;
	private PipedInputStream pi;

	public ObjectMulti(PipedInputStream pi, Context activity) {
		this.activity = activity;
		this.pi = pi;
	}

	@Override
	public void run() {
		ObjectInputStream oi;
		try {
			/*
			 * 
			 * http://docs.oracle.com/javase/6/docs/api/java/io/ObjectInputStream
			 * .html Creates an ObjectInputStream that reads from the specified
			 * InputStream. A serialization stream header is read from the
			 * stream and verified. This constructor will *** block *** until
			 * the corresponding ObjectOutputStream has written and flushed the
			 * header.
			 */
			oi = new ObjectInputStream(pi);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		while (true) {
			try {
				final FileFragment ff = (FileFragment) oi.readObject();
				if (ff != null) {
					Log.d(TAG, ff.toString());
					((Activity) activity).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, ff.toString(),
									Toast.LENGTH_SHORT).show();
						}
					});
					switch (ff.getSegmentID()) {
					case WiFiBroad.FRAG_REQST_TAG:
						Segment s = IntegrityCheck.getInstance().getSeg(
								ff.getStartIndex());
						FileFragment f = s.getFragment(ff.getStopIndex());
						WiFiFactory.insertF(f);
						break;
					case WiFiBroad.EMERGEN_SEND_TAG:
						((Activity) activity).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(activity, "I am slave",
										Toast.LENGTH_SHORT).show();
							}
						});
						break;
					default:
						ff.check();
						IntegrityCheck.getInstance().insert(ff.getSegmentID(),
								ff);
						break;
					}
				}

			} catch (StreamCorruptedException e) {
				// Thrown when control information that was read from an object
				// stream violates internal consistency checks.
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
