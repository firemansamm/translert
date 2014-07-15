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

	@Override
	public void onCreate() {
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		alarmThreshold = Integer.parseInt(  preferences.getString("alarm_distance", "500")  );
		overdriveModeThreshold = alarmThreshold + C.BUS_SPEED * C.DELAY_NORMAL;
		isOverdriveMode = false;
		isRunning = false;
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d("translert", "Keeper service started");
		if (isRunning) {
			this.stopSelf();
			return START_NOT_STICKY;
		}
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
			Log.d("translert", "Cannot get location services");
			Intent outputIntent = new Intent (this, ProgressActivity.class);
			outputIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			outputIntent.putExtra("nullLocationService", true);
			startActivity(outputIntent);
			this.stopSelf();
			return START_NOT_STICKY;
		}
		
		String busStopName = intent.getStringExtra("busDestination");
		String busNumber = intent.getStringExtra("busNumber");
		Log.d("translert", "Service received request for destination " + busStopName + " on route " + busNumber);
		destination = F.getBusStopPosition (DistanceUpdateService.this, busStopName, busNumber);
		if (destination == null) {
			Log.d("translert", "Cannot get destination position");
			Intent outputIntent = new Intent (DistanceUpdateService.this, ProgressActivity.class);
			outputIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			outputIntent.putExtra("nullDestination", busStopName);
			outputIntent.putExtra("busNumber", busNumber);
			startActivity(outputIntent);
			this.stopSelf();
			return START_NOT_STICKY;
		}
		
		Intent outputIntent = new Intent (DistanceUpdateService.this, ProgressActivity.class);
		outputIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(outputIntent);
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		
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
			sendDistance(distance);
			if (distance < alarmThreshold) {
				sendAlarm();
			} else if (distance > overdriveModeThreshold) {
				locationManager.requestLocationUpdates(locationProvider, C.DELAY_NORMAL, C.MIN_DISTANCE, this);
			} else {
				isOverdriveMode = true;
				locationManager.requestLocationUpdates(locationProvider, C.DELAY_OVERDRIVE, C.MIN_DISTANCE, this);
			}
		} else {
			locationManager.requestLocationUpdates(locationProvider, C.DELAY_NORMAL, C.MIN_DISTANCE, this);
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
		sendDistance(distance);
		if (distance < alarmThreshold) {
			sendAlarm();
		} else if (!isOverdriveMode && distance < overdriveModeThreshold) {
			isOverdriveMode = true;
			locationManager.removeUpdates(this);
			locationManager.requestLocationUpdates(locationProvider, C.DELAY_OVERDRIVE, C.MIN_DISTANCE, this);
		}
	}
	
	private void sendAlarm() {
		Handler uiHandler = ProgressActivity.uiHandler;
		uiHandler.sendEmptyMessage(C.ALARM_MESSAGE);
		stopSelf();
	}
	
	private void sendDistance(float distance) {
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