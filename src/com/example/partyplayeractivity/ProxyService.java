package com.example.partyplayeractivity;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.dashproxyserver.DashProxyServer;

public class ProxyService extends Service {
	
	public MyBinder sBinder;
	
	public class MyBinder extends Binder{
		
		public ProxyService getProxyService(){
			return ProxyService.this;
		}
				
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return sBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startedId){
		
		DashProxyServer server = new DashProxyServer();
		try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
		return START_STICKY;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		sBinder = new MyBinder();
	}

}
