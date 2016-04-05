package com.example.celluar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.example.Integrity.IntegrityCheck;
import com.example.entities.FileFragment;

public class CellularDown {

	public void queryFragment(int url) {
		new CellThread(url).start();
	}

	private class CellThread extends Thread {
		private int url;

		CellThread(int url) {
			super();
			this.url = url;
		}

		@Override
		public void run() {
			Log.d("testtest", "test");
			HttpURLConnection connection = null;
			try {
				URL uurl = new URL(IntegrityCheck.URL_TAG + "?filename="
						+ (url)
						+ ".mp4&sessionid=lykfr9oyqipf2q3tvy2l73bao216");
				Log.d("testtest", "" + uurl);
				while (true) {
					connection = (HttpURLConnection) uurl.openConnection();
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(5000);
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setRequestProperty("Accept-Encoding", "");
					connection.setDoOutput(true);
					// data need to be modified
					// String data = "filename=" + url
					// + ".mp4&sessionid=lykfr9oyqipf2q3tvy2l73bao216";
					// OutputStream out = connection.getOutputStream();
					// out.write(data.getBytes());
					// out.flush();
					// out.close();
					Log.d("ResponseCode",
							String.valueOf(connection.getResponseCode()));

					if (connection.getResponseCode() == 206) {
						InputStream in = connection.getInputStream();
						String contentRange = connection.getHeaderField(
								"Content-Range").toString();
						String range = contentRange.split(" ")[1].trim();
						String start = range.split("-")[0];
						String end = range.split("-")[1].split("/")[0];
						String total = range.split("-")[1].split("/")[1];
						Log.d("Total", total);
						Log.d("PieceStart", start);
						Log.d("PieceEnd", end);
						int startOffset = Integer.parseInt(start);
						int endOffset = Integer.parseInt(end);
						int totalLength = Integer.parseInt(total);
						int pieceLength = endOffset - startOffset;

						byte[] tmpbuff = new byte[pieceLength];
						int hasRead = 0;
						while (hasRead < pieceLength) {
							hasRead += in.read(tmpbuff, hasRead, pieceLength
									- hasRead);
						}

						IntegrityCheck IC = IntegrityCheck.getInstance();
						IC.setSegLength(url, totalLength);
						FileFragment fm = new FileFragment(startOffset,
								endOffset, url);
						Log.d("test", "" + fm);
						fm.setData(tmpbuff);
						IC.insert(url, fm);
					} else if (connection.getResponseCode() == 200) {
						Log.d("Test", "else");
						break;
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.d("Test", "MalformedURLException");
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("Test", "IOException");
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("Test", "Exception");
			} finally {
				connection.disconnect();
			}
		}

	}

}
