package com.translert.activity;


import com.translert.bus.C;
import com.translert.bus.GPSTracker;
import com.translert.bus.SGGPosition;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.translert.activity.F.f;


public class BusDistanceKeeperService extends Service {
	
	static HandlerThread keeper;
	static Handler uiHandler;
	static Handler myHandler;
	
	
	private String busDestination;
	private SGGPosition current;
	static public SGGPosition destination;
	
	private double distance;
	Message msg;
	
	private static boolean activeFlag = false;
	

	public BusDistanceKeeperService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		
		keeper = new HandlerThread ("keeper");
		keeper.start();
		Looper keeperLooper = keeper.getLooper();
		myHandler = new Handler (keeperLooper);
		
		
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId){
		
		if (!activeFlag) {
		
			Log.d("translert", "keeper service started");
			
			busDestination = intent.getStringExtra("busDestination");
			Log.d("translert", " service received request for destination " + busDestination);
			myHandler.post(getDistanceInitiateRunnable);
			
			return START_STICKY;
			
		} else {
			
			this.stopSelf();
			return START_NOT_STICKY;
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		myHandler.removeCallbacksAndMessages(null);
		keeper.quit();
		BusDistanceKeeperService.this.stopSelf();
		Log.d("translert", "keeper service destroyed");
	}
	
	
	
	
	private Runnable getDistanceInitiateRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			current = f.getGPS();
			Log.d("translert", current.format());
			
			destination = f.getSdBusStopPosition (busDestination);
			Log.d("translert", destination.format());
			
			if (destination != null && current !=null) {
				
				distance = current.getDistance(destination);
				Log.d("translert", "initial distance is " + distance);
				
				Intent outputIntent = new Intent (BusDistanceKeeperService.this, BusProgressActivity.class);
				outputIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				outputIntent.putExtra("distance", formatDistanceToString(distance));
				
				startActivity(outputIntent);
				Log.d("translert", "Service initiated progress activity");
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				
				uiHandler = BusProgressActivity.handler;
				myHandler.post(getDistancePeriodicRunnable);
				
				
			} else {
				Log.d("translert", "null position(s)");
			}
		}
		
	};
	
	
	private Runnable getDistancePeriodicRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			current = f.getGPS();
			if (current != null && destination!= null) {
//				distance = current.getDistance(destination);
				
				
				if (distance > 2000) {
					msg = uiHandler.obtainMessage(C.RETURN_DISTANCE, formatDistanceToString(distance));
					uiHandler.sendMessage(msg);
					
					distance -= 1200;
					myHandler.postDelayed(getDistancePeriodicRunnable, C.DELAY_LONG);	
				} else if (distance <=2000 && distance > 400) {
					msg = uiHandler.obtainMessage(C.RETURN_DISTANCE, formatDistanceToString(distance));
					uiHandler.sendMessage(msg);
					
					distance -= 400;
					myHandler.postDelayed(getDistancePeriodicRunnable, C.DELAY_SHORT);
				} else {
					uiHandler.sendEmptyMessage(C.RETURN_DISTANCE_REACHED);
					myHandler.removeCallbacksAndMessages(null);
					keeper.quit();
					BusDistanceKeeperService.this.stopSelf();
				}
				
			} else {
				Log.d("translert", "periodical updates failed");
			}
			
		}
		
	};
	
	
	private String formatDistanceToString(double distance) {
		if (distance > 2000) {
			return String.format("%.1f km", distance/1000 );
		} else {
			return String.format("%s m", Math.round(distance)  );
		}
	}
	

}
