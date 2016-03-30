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

	public void queryFragment(String url) {
		// handle 3G downloading
		//CellThread a = new CellThread(url);
		//a.start();
		new CellThread(url).start();
	}

	private class CellThread extends Thread {
		private String url;
		private int fileNameIndex;

		private CellThread(String url) {
			super();
			this.url = url;
			fileNameIndex = getFileNumFromUrl(url);
		}
		
		private int getFileNumFromUrl(String uri){
			String ss = null;
			for (String s : uri.split("/")) {
				if (s.contains(".php")) {
					ss = s;
					break;
				}
			}
			String[] tmp = ss.split(".");
			return Integer.parseInt(tmp[0]);
		}
		
		@Override
		public void run() {
			HttpURLConnection connection = null;
			try {
				URL uurl = new URL(url);
				while (true) {
					connection = (HttpURLConnection) uurl.openConnection();
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(5000);
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					// data need to be modified
					String data = "filename="+fileNameIndex+".mp4&sessionid=lykfr9oyqipf2q3tvy2l73bqo3a2";
					OutputStream out = connection.getOutputStream();
					out.write(data.getBytes());
					out.flush();
					out.close();
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

						if (pieceLength == 0) {
							break;
						}

						byte[] tmpbuff = new byte[pieceLength];
						int hasRead = 0;
						while (hasRead < pieceLength) {
							hasRead += in.read(tmpbuff, hasRead, pieceLength
									- hasRead);
						}

						IntegrityCheck IC = IntegrityCheck.getInstance();
						IC.setSegLength(url, totalLength);
						FileFragment fm = new FileFragment(startOffset,
								endOffset, IC.url2id(url));
						fm.setData(tmpbuff);
						IC.insert(url, fm);
					} else {
						Log.d("Test", "else");
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.d("Test", "MalformedURLException");
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("Test", "IOException");
			}  catch (Exception e) {
				e.printStackTrace();
				Log.d("Test", "Exception");
			} finally {
				connection.disconnect();
			}
		}

	}

}
