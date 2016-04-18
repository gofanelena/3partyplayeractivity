package com.ANT.MiddleWare.WiFi.WiFiNCP2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;
import com.ANT.MiddleWare.Entities.Segment;
import com.ANT.MiddleWare.Integrity.IntegrityCheck;
import com.ANT.MiddleWare.WiFi.WiFiFactory;
import com.ANT.MiddleWare.WiFi.WiFiBroad.WiFiBroad;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.widget.Toast;

public class RecvThread extends Thread {
	private static final  String TAG= RecvThread.class.getSimpleName();
	private Context context;
	private SocketChannel sChannel;
	//private static final String host="192.168.1.89";
	private byte[] data;
	private int start;
	private int stop;
	public RecvThread(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	 /**  
     * 数组转对象  
     * @param bytes  
     * @return  
     */  
    public Object toObject (byte[] bytes) {      
        Object obj = null;      
        try {        
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);        
            ObjectInputStream ois = new ObjectInputStream (bis);        
            obj = ois.readObject();      
            ois.close();   
            bis.close();   
        } catch (IOException ex) {        
            ex.printStackTrace();   
        } catch (ClassNotFoundException ex) {        
            ex.printStackTrace();   
        }      
        return obj;    
    }  
    
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			
			sChannel=SocketChannel.open();
			sChannel.configureBlocking(false);
			sChannel.connect(new InetSocketAddress(WiFiNCP2.host,WiFiNCP2.port));
			
			while(sChannel.finishConnect()){
				ByteBuffer buf = ByteBuffer.allocate(48);  
				int bytesRead = sChannel.read(buf); 
				while(bytesRead!=-1){
					buf.flip();
					while(buf.hasRemaining()){
						buf.get(data);
					}
					buf.clear();
					bytesRead=sChannel.read(buf);
				}
				if(toObject(data) instanceof P2Message){
					P2Message me =(P2Message) toObject(data);
					final int segID=me.getSegmentID();
					String message =me.getMessage();
					if(message.equals("initial push")){
						
						P2Message pullRequest =new P2Message(segID, start,stop,"subsequent pulls");
						ByteBuffer wbuf = ByteBuffer.allocate(48);  
						wbuf.clear();  
						wbuf.put(P2Message.toByte(pullRequest)); 
						
						wbuf.flip();  
						  
						while(wbuf.hasRemaining()) {  
						    sChannel.write(wbuf);  
						}
						
					}
				}else if(toObject(data) instanceof FileFragment){
					final FileFragment ff=(FileFragment) toObject(data);
					if (ff != null) {
						Log.d(TAG, ff.toString());
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(context, ff.toString(),
										Toast.LENGTH_SHORT).show();
							}
						});
						switch (ff.getSegmentID()) {
						case WiFiNCP2.FRAG_REQST_TAG:
							Segment s = IntegrityCheck.getInstance().getSeg(
									ff.getStartIndex());
							FileFragment f = s.getFragment(ff.getStopIndex());
							WiFiFactory.insertF(f);
							break;
						case WiFiNCP2.EMERGEN_SEND_TAG:
							((Activity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(context, "I am slave",
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
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			Socket socket=new Socket(host, 54321);
//			Log.d(TAG, "connect successful");
//			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);  
//            pw.println("I'm slave!");  
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));    
//            final String message = in.readLine();  
//            Log.d(TAG, "message From Server:" + message); 
//            ((Activity) context).runOnUiThread(new Runnable() {
//    			@Override
//    			public void run() {
//    				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//    			}
//    		});
//            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//            socket.close(); 
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		catch (FileFragmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				sChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
