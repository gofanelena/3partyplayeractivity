package com.example.dashproxyserver;

import java.io.FileInputStream;

import android.os.Environment;
import android.util.Log;

import com.example.Integrity.IntegrityCheck;
import com.example.nanohttpd.NanoHTTPD;
import com.example.partyplayeractivity.ConfigureData;
import com.example.partyplayeractivity.MainFragment;

/**
 * Created by ljw on 6/18/15.
 */
public class DashProxyServer extends NanoHTTPD {
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
		if(!getFileName(session,".m3u8").equals("")){
			Log.v("filename", session.getUri());
			try {
				fis = new FileInputStream(
						Environment.getExternalStorageDirectory() + "/video/4/index.m3u8");

				length = fis.available();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return newFixedLengthResponse(Response.Status.OK,
					"application/x-mpegurl", fis, length);
		}else{
			Log.v("DashProxy", "uri:" + session.getUri());
			String s1 = getFileName(session, ".mp4");
			String playist = getFileName(session, ".mp4");
			Log.v("playist", playist);

			if (MainFragment.configureData.getWorkingMode() == ConfigureData.LOCAL_MODE) {
				try {
					fis = new FileInputStream(
							Environment.getExternalStorageDirectory() + "/video/4/"
									+ playist);

					length = fis.available();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return newFixedLengthResponse(Response.Status.OK,
						"application/x-mpegurl", fis, length);

			} else if (MainFragment.configureData.getWorkingMode() == ConfigureData.G_MDOE) {
						
				IntegrityCheck iTC = IntegrityCheck.getInstance();
				
				//String tmpp = iTC.URI_TAG + playist;
				int tmpp = Integer.parseInt(playist.substring(0,1));
				
				byte[] tmp = iTC.getSegments(tmpp);
				
				return newFixedLengthResponse(Response.Status.OK,
						"application/x-mpegurl", tmp);

			} else {

				return newFixedLengthResponse(Response.Status.OK,
						"application/x-mpegurl", fis, length);
			}
		}

		

		// switch(MainFragment.configureData.getWorkingMode()){
		// case ConfigureData.LOCAL_MODE:
		// try {
		// fis = new FileInputStream(Environment.getExternalStorageDirectory()
		// + "/video/4/"+playist);
		//
		// length=fis.available();
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// break;
		// case ConfigureData.G_MDOE:
		//
		// break;
		// case ConfigureData.COOPERATIVE_MODE:
		// break;
		// }

		// try {
		// fis = new FileInputStream(Environment.getExternalStorageDirectory()
		// + "/video/4/"+playist);
		//
		// length=fis.available();
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// return
		// newFixedLengthResponse(Response.Status.OK,"application/x-mpegurl",fis,length);

		// }else if(!getFileName(session,".ts").equals("")){
		// String tsFile=getFileName(session,".ts");
		//
		// if(tsFile.equals("output107.ts")){
		// Log.v("DashProxy","sleep");
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		//
		//
		// }
		//
		//
		// try {
		// fis = new FileInputStream(Environment.getExternalStorageDirectory()
		// + "/dash/"+tsFile);
		//
		// length=fis.available();
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// return
		// newFixedLengthResponse(Response.Status.OK,"video/mp2t",fis,length);
		//
		// }
		// else{
		//
		// return newFixedLengthResponse("Joke");
		// }

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
}
