package com.translert.bus;

import java.util.Iterator;
import com.translert.bus.utils.C;
import com.translert.bus.utils.F;
import com.translert.bus.utils.SGGPosition;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class DistanceUpdateService extends Service implements LocationListener {
	static public SGGPosition destination;
	
	private static int alarmThreshold;
	private static Double overdriveModeThreshold;
	
	private static LocationManager locationManager;
	private static String locationProvider;
	
	private static boolean isRunning;
	private static boolean isOverdriveMode;
	
	private static Handler uiHandler;

	@Override
	public void onCreate() {
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		alarmThreshold = Integer.parseInt(  preferences.getString("alarm_distance", "500")  );
		overdriveModeThreshold = alarmThreshold + C.BUS_SPEED * C.DELAY_NORMAL/1000;
		Log.d("overdriveModeThreshold", String.valueOf(overdriveModeThreshold));
		isOverdriveMode = false;
		isRunning = false;
		uiHandler = ProgressActivity.uiHandler;
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId){
		if (isRunning) {
			return START_NOT_STICKY;
		}
		Log.d("translert", "Keeper service started");
		
		// getting GPS status
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (isGPSEnabled || isNetworkEnabled) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			locationProvider = locationManager.getBestProvider(criteria, true);
		} else {
			uiHandler.sendEmptyMessage(C.LOCATION_PROVIDER_NOT_FOUND_MESSAGE);
			return START_NOT_STICKY;
		}
		
		String busStopName = intent.getStringExtra("busDestination");
		String busNumber = intent.getStringExtra("busNumber");
		Log.d("translert", "Service received request for destination " + busStopName + " on route " + busNumber);
		destination = F.getSingleBusStop (this, busStopName, busNumber);
		if (destination == null) {
			uiHandler.sendEmptyMessage(C.BUS_STOP_NOT_FOUND_MESSAGE);
			return START_NOT_STICKY;
		}
		
		Location currentLocation = null;
		Iterator<String> iterator = locationManager.getProviders(true).iterator();
		while (iterator.hasNext()) {
			String provider = iterator.next();
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				currentLocation = location;
				break;
			}
		}
		if (currentLocation != null) {
			float distance = destination.getDistance(currentLocation);
			uiHandlerSendDistance(distance);
			if (distance < alarmThreshold) {
				uiHandler.sendEmptyMessage(C.ALARM_MESSAGE);
			} else if (distance > overdriveModeThreshold) {
				Log.d("translert", "normalMode");
				locationManager.requestLocationUpdates(locationProvider, C.DELAY_NORMAL, C.MIN_DISTANCE, this);
			} else {
				Log.d("translert", "overdriveMode");
				isOverdriveMode = true;
				locationManager.requestLocationUpdates(locationProvider, C.DELAY_OVERDRIVE, C.MIN_DISTANCE, this);
			}
		} else {
			locationManager.requestLocationUpdates(locationProvider, C.DELAY_NORMAL, C.MIN_DISTANCE, this);
			Log.d("translert", "normalModeNoCurrent");
		}
		
		isRunning = true;
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		isRunning = false;
		locationManager.removeUpdates(this);
		Log.d("translert", "Bus distance keeper destroyed");
	}

	@Override
	public void onLocationChanged(Location location) {
		float distance = destination.getDistance(location);
		uiHandlerSendDistance(distance);
		if (distance < alarmThreshold) {
			uiHandler.sendEmptyMessage(C.ALARM_MESSAGE);
		} else if (!isOverdriveMode && distance < overdriveModeThreshold) {
			Log.d("translert", "overdriveModeSwitch");
			isOverdriveMode = true;
			locationManager.removeUpdates(this);
			locationManager.requestLocationUpdates(locationProvider, C.DELAY_OVERDRIVE, C.MIN_DISTANCE, this);
		}
	}
	
	private void uiHandlerSendDistance(float distance) {
		String formattedDistance;
		if (distance > overdriveModeThreshold) {
			formattedDistance = String.format("%.1f km", distance/1000 );
		} else {
			formattedDistance = String.format("%s m", Math.round(distance)  );
		}
		Handler uiHandler = ProgressActivity.uiHandler;
		Message msg = uiHandler.obtainMessage(C.DISTANCE_MESSAGE, formattedDistance);
		uiHandler.sendMessage(msg);
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onProviderDisabled(String provider) {}
	@Override
	public IBinder onBind(Intent intent) { return null;}
}