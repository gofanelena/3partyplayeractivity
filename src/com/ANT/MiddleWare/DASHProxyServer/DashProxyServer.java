package com.ANT.MiddleWare.DASHProxyServer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import android.os.Environment;
import android.util.Log;

import com.ANT.MiddleWare.Integrity.IntegrityCheck;
import com.ANT.MiddleWare.PartyPlayerActivity.ConfigureData;
import com.ANT.MiddleWare.PartyPlayerActivity.MainFragment;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * Created by ljw on 6/18/15.
 */
public class DashProxyServer extends NanoHTTPD {
	private static final String TAG = DashProxyServer.class.getSimpleName();

	public DashProxyServer(int port) {
		super(port);
	}

	public DashProxyServer(String hostname, int port) {
		super(hostname, port);
	}

	public DashProxyServer() {
		super(9999);
	}

	@Override
	public Response serve(IHTTPSession session) {
		FileInputStream fis = null;
		int length = 0;
		if (!getFileName(session, ".m3u8").equals("")) {
			Log.v(TAG, "filename" + session.getUri());
			try {
				fis = new FileInputStream(
						Environment.getExternalStorageDirectory()
								+ "/video/4/index.m3u8");

				length = fis.available();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return newFixedLengthResponse(Response.Status.OK,
					"application/x-mpegurl", fis, length);
		} else {
			Log.v(TAG, "DashProxy uri:" + session.getUri());
			String playist = getFileName(session, ".mp4");
			Log.v(TAG, "playist" + playist);

			if (MainFragment.configureData.getWorkingMode() == ConfigureData.WorkMode.LOCAL_MODE) {
				try {
					fis = new FileInputStream(
							Environment.getExternalStorageDirectory()
									+ "/video/4/" + playist);

					length = fis.available();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return newFixedLengthResponse(Response.Status.OK,
						"application/x-mpegurl", fis, length);

			} else if (MainFragment.configureData.getWorkingMode() == ConfigureData.WorkMode.G_MDOE) {

				IntegrityCheck iTC = IntegrityCheck.getInstance();
				int tmpp = Integer.parseInt(playist.substring(0, 1));
				byte[] tmp = iTC.getSegments(tmpp);

				return newFixedLengthResponse(Response.Status.OK,
						"application/x-mpegurl", tmp);

			} else {

				return newFixedLengthResponse(Response.Status.OK,
						"application/x-mpegurl", fis, length);
			}
		}
	}

	public String getFileName(IHTTPSession session, String key) {
		String uri = session.getUri();
		String playlist = "";
		for (String s : uri.split("/")) {
			if (s.contains(key)) {
				playlist = s;
			}
		}
		return playlist;
	}



	private Response newFixedLengthResponse(Response.IStatus status,
			String mimeType, byte[] bytes) {
		return newFixedLengthResponse(status, mimeType,
				new ByteArrayInputStream(bytes), bytes.length);
	}
}
