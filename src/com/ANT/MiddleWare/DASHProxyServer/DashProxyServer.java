package com.ANT.MiddleWare.DASHProxyServer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;

import android.os.Environment;
import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Integrity.IntegrityCheck;
import com.ANT.MiddleWare.PartyPlayerActivity.MainFragment;
import com.ANT.MiddleWare.PartyPlayerActivity.test.CellularDownTest;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by ljw on 6/18/15.
 */
public class DashProxyServer extends NanoHTTPD {
	private static final String TAG = DashProxyServer.class.getSimpleName();
	private static final String mp4 = "application/x-mpegurl";
	private static final String dir = "/video/4/";

	public DashProxyServer() {
		super(9999);
		try {
			this.start();
			Log.e(TAG, "start");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		Log.e(TAG, "filename" + session.getUri());
		try {
			if (!getFileName(session, ".m3u8").equals("")) {
				return localFile("index.m3u8");
			}
			String playist = getFileName(session, ".mp4");
			Log.v(TAG, "playist" + playist);
			if (!playist.equals("")) {
				switch (MainFragment.configureData.getWorkingMode()) {
				case LOCAL_MODE:
					return localFile(playist);
				case G_MDOE:
					IntegrityCheck iTC = IntegrityCheck.getInstance();
					int tmpp = Integer.parseInt(playist.substring(0, 1));
					byte[] tmp = iTC.getSegments(tmpp);
					return newFixedLengthResponse(Response.Status.OK, mp4, tmp);
				case JUNIT_TEST_MODE:
					Stack<FileFragment> s = CellularDownTest.fraList;
					if (s.empty()) {
						Log.wtf(TAG, "file nothing");
						return newFixedLengthResponse(
								Response.Status.NOT_FOUND, mp4, null, 0);
					}
					FileFragment f = s.pop();
					Response res = newFixedLengthResponse(
							Response.Status.PARTIAL_CONTENT, mp4, f.getData());
					res.addHeader(
							"Content-Range",
							"Content-Range " + f.getStartIndex() + "-"
									+ f.getStopIndex() + "/"
									+ CellularDownTest.base);
					return res;
				default:
					Log.wtf(TAG, "file nothing");
					return newFixedLengthResponse(
							Response.Status.INTERNAL_ERROR, mp4, null, 0);
				}
			}
			Log.wtf(TAG, "file nothing");
			return newFixedLengthResponse(Response.Status.NOT_FOUND, mp4, null,
					0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.wtf(TAG, e);
			return newFixedLengthResponse(Response.Status.NOT_FOUND, mp4, null,
					0);
		} catch (IOException e) {
			e.printStackTrace();
			Log.wtf(TAG, e);
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, mp4,
					null, 0);
		}
	}

	private String getFileName(IHTTPSession session, String key) {
		String uri = session.getUri();
		String playlist = "";
		for (String s : uri.split("/")) {
			if (s.contains(key)) {
				playlist = s;
			}
		}
		return playlist;
	}

	private Response localFile(String str) throws IOException {
		str = dir + str;
		FileInputStream fis = new FileInputStream(
				Environment.getExternalStorageDirectory() + str);
		int length = fis.available();
		return newFixedLengthResponse(Response.Status.OK, mp4, fis, length);
	}

	private Response newFixedLengthResponse(Response.IStatus status,
			String mimeType, byte[] bytes) {
		return newFixedLengthResponse(status, mimeType,
				new ByteArrayInputStream(bytes), bytes.length);
	}
}
