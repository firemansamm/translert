package com.translert.bus;

import com.translert.bus.utils.C;
import com.translert.bus.utils.SGGPosition;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;


@SuppressLint("DefaultLocale")
public class BusDistanceKeeperService extends Service implements LocationListener {
	
	static boolean simulator = false;
	
	static HandlerThread keeper;
	static Handler uiHandler;
	static Handler myHandler;
	
	
	private String busDestination;
	private String busNumber;
	private SGGPosition current;
	static public SGGPosition destination;
	
	private int threshold;
	private double distance;
	Message msg;
	
	private static boolean activeFlag = false;
	
	private static LocationManager locationManager;
	
	
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
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		threshold = Integer.parseInt(  preferences.getString("alarm_distance", "500")  );
		Log.d("translert", Integer.toString(threshold));
		
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId){
		
		if (!activeFlag) {
		
			Log.d("translert", "keeper service started");
			
			busDestination = intent.getStringExtra("busDestination");
			busNumber = intent.getStringExtra("busNumber");
			Log.d("translert", " service received request for destination " + busDestination + " on route " + busNumber);
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
			current = F.getGPS();
			destination = F.getBusStopPosition (BusDistanceKeeperService.this, busDestination, busNumber);
			
			if (current == null) {
				
			} else if (destination == null) {
				Log.d("translert", "cannot get destination position");
				Intent outputIntent = new Intent (BusDistanceKeeperService.this, BusProgressActivity.class);
				outputIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				outputIntent.putExtra("nullDestination", busDestination);
				outputIntent.putExtra("busNumber", busNumber);
				startActivity(outputIntent);
				BusDistanceKeeperService.this.stopSelf();
			} else {

				Log.d("translert", current.format());
				Log.d("translert", destination.format());
				
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
				
				if (simulator) {
					myHandler.post(getDistancePeriodicRunnableSimulator);
				} else {
					myHandler.post(getDistancePeriodicRunnable);
				}
			}
		}
	};
	
	private void parseDistance(long delay) {
		msg = uiHandler.obtainMessage(C.RETURN_DISTANCE, formatDistanceToString(distance));
		uiHandler.sendMessage(msg);
		if (simulator) {
			myHandler.postDelayed(getDistancePeriodicRunnableSimulator, delay);
		} else {
			myHandler.postDelayed(getDistancePeriodicRunnable, delay);
		}
//		
		
	}
	
	private Runnable getDistancePeriodicRunnable = new Runnable() {
		@Override
		public void run() {
			current = F.getGPS();
			if (current != null) {
				Log.d("translert", current.format());
				distance = current.getDistance(destination);
				
				if (distance > 2000) {
					parseDistance(C.DELAY_LONG);
//					parseDistance(C.DELAY_LONG_SIMULATOR);
				} else if (distance <= 2000 && distance > threshold) {
					parseDistance(C.DELAY_SHORT);
//					parseDistance(C.DELAY_SHORT_SIMULATOR);
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
	
	private Runnable getDistancePeriodicRunnableSimulator = new Runnable() {

		@Override
		public void run() {
			
			if (distance > 2000) {
				parseDistance(C.DELAY_LONG_SIMULATOR);
				distance -= 1200;
			} else if (distance <=2000 && distance > threshold) {
				parseDistance(C.DELAY_SHORT_SIMULATOR);
				distance -= 50;
			} else {
				uiHandler.sendEmptyMessage(C.RETURN_DISTANCE_REACHED);
				myHandler.removeCallbacksAndMessages(null);
				keeper.quit();
				BusDistanceKeeperService.this.stopSelf();
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}