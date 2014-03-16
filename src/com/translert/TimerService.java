package com.translert;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class TimerService extends Service {

	public static boolean active = false;
	public static int secondsRemaining = 0, currentLeg = 0, legTotal = 0;
	public static String endStation = "";
	private static Timer tripTimer;
	
    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId){
		active = true;
		Bundle b = intent.getExtras();
		secondsRemaining = b.getInt("minutes") * 60;
		currentLeg = b.getInt("legnum");
		legTotal = b.getInt("totalleg");
		endStation = b.getString("destination");
		tripTimer = new Timer(true);
		tripTimer.scheduleAtFixedRate(tt, 0, 1000);
		return START_STICKY;
	}
	
	private TimerTask tt = new TimerTask(){
		@Override
		public void run() {
			secondsRemaining--;
			for (int i=0;i<tickHandlers.size();i++){
				tickHandlers.get(i).work();
			}
			if(secondsRemaining == 0) {
				active = false;
				for (int i=0;i<endHandlers.size();i++){
					endHandlers.get(i).work();
				}
			}
		}
	};
	
	public interface Worker{
		void work();
	};
	public static ArrayList<Worker> tickHandlers = new ArrayList<Worker>();
	public static ArrayList<Worker> endHandlers = new ArrayList<Worker>();
	
	@Override
	public void onDestroy(){
		tripTimer.cancel();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private final IBinder mBinder = new LocalBinder();

}
