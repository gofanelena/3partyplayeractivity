package com.example.celluar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.example.entities.FileFragment;
import com.example.entities.Segment;

public class CellularDown {
	
		
	public void queryFragment(Segment segment, URL key){		
		//handle 3G downloading
		CellThread a = new CellThread(segment, key);
		a.start();
		
	}
	
	private class CellThread extends Thread{
		private Segment segment;
		private URL url;
		
		private CellThread(Segment seg, URL mUrl){
			super();
			segment= seg;
			url = mUrl;
		}
		public void run(){
			HttpURLConnection connection=null;
			try {
	            connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("POST");
	            connection.setConnectTimeout(5000);
	            connection.setUseCaches(false);
	            connection.setDoInput(true);
	            connection.setDoOutput(true);
	            //data need to be modified
	            String data = "filename=photo.jpg&sessionid=lykfr9oyqipf2q3tvy2l73bqo3a2&id=1";
	            OutputStream out=connection.getOutputStream();
	            out.write(data.getBytes());
	            out.flush();
	            out.close();
	            Log.d("ResponseCode",String.valueOf(connection.getResponseCode()));
	            
	            if (connection.getResponseCode()==206) {
	                InputStream in=connection.getInputStream();
	                String contentRange=connection.getHeaderField("Content-Range").toString();
	                String range = contentRange.split(" ")[1].trim();
	                String start = range.split("-")[0];
	                String end = range.split("-")[1].split("/")[0];
	                String total = range.split("-")[1].split("/")[1];
	                Log.d("Total",total);
	                Log.d("PieceStart",start);
	                Log.d("PieceEnd",end);
	                int startOffset=Integer.parseInt(start);
	                int endOffset=Integer.parseInt(end);
	                int totalLength=Integer.parseInt(total);
	                int pieceLength=endOffset-startOffset+1;
	                
	                byte[] tmpbuff=new byte[pieceLength];
                	int hasRead=0;
                    while(hasRead<pieceLength){
                    	hasRead+=in.read(tmpbuff, hasRead, pieceLength-hasRead);
                    }
                    
                    segment.setSegLength(totalLength);
                    FileFragment fm = new FileFragment(startOffset, endOffset, segment.getSegmentID());
                    fm.setData(tmpbuff);
                    segment.insert(fm);
	            }else{
	            	Log.d("Test", "else");
	            } 
			}catch (MalformedURLException e) {
	            e.printStackTrace();
	            Log.d("Test", "MalformedURLException");
            } catch (IOException e) {
                e.printStackTrace();
            	Log.d("Test", "IOException");
            }finally {
                connection.disconnect();
            }
		}
                    
		
	}
	
	

}
