package com.translert.train;

import java.util.concurrent.TimeUnit;

import com.translert.bus.utils.C;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;


public class TimerService extends Service {
	
	public static Handler uiThreadHandler;
	
	static Message msg;
	static boolean activeFlag = false;
	
	private static long totalTime;
	
	public static int /*secondsRemaining = 0,*/ currentLeg = 0, legTotal = 0;
	public static String endStation = "";
	
	private static long offset;
	
	static MyCountDownTimer myCountDownTimer;
	
	@Override
	public void onCreate() {
		//get handler from the activity to update UI
		//TimerService.uiThreadHandler = new Handler(Looper.getMainLooper());
		TimerService.uiThreadHandler = WatchActivity.uiThreadHandler;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int pref = Integer.parseInt(  preferences.getString("alarm_timing", "2")  );
		offset = TimeUnit.MINUTES.toMillis(pref);
		Log.d("translert", Long.toString(offset));
	}

	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!activeFlag) {
			
			activeFlag = true;
			Bundle b = intent.getExtras();
			totalTime = TimeUnit.MINUTES.toMillis( b.getInt("minutes") );
			Log.d("translert", "received request for timer of " + String.valueOf(totalTime) + " milliseconds!");
			currentLeg = b.getInt("legnum");
			legTotal = b.getInt("totalleg");
			endStation = b.getString("destination");
			
			
			msg = uiThreadHandler.obtainMessage(C.RETURN_TIMER_START, 
					new String[] {"Arriving at " + endStation + " in:", "Part " + currentLeg + " of " + legTotal+ " trip"});
			uiThreadHandler.sendMessage(msg);
			
			myCountDownTimer = new MyCountDownTimer(totalTime - offset, 1000L);
			//myCountDownTimer = new MyCountDownTimer(10000L, 1000L);
			myCountDownTimer.start();
			return START_STICKY;
			
		} else {
			
			this.stopSelf();
			return START_NOT_STICKY;
			
		}
	}
	
	private class MyCountDownTimer extends CountDownTimer {

		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		
		@Override
		public void onTick(long millisUntilFinished) {
			//send a message to the UI handler to update UI on tick
			String formattedTime = formatTime(millisUntilFinished + offset);
			msg = uiThreadHandler.obtainMessage(C.RETURN_TIMER_RUNNING, formattedTime);
			uiThreadHandler.sendMessage(msg);
		}
		 
		@Override
		public void onFinish() {
			//send a message to the UI handler to update UI on finish
			activeFlag = false;
			uiThreadHandler.sendEmptyMessage(C.RETURN_TIMER_OVER);
			TimerService.this.stopSelf();
		}
	}
	
	private String formatTime (long millis) {
		
		int seconds = (int) millis/1000;
		
		if (seconds < 3600) {
			
			return String.format("%02d:%02d", seconds/60, seconds%60);
		} else {
			return String.format("%01d:%02d:%02d", seconds/3600, (seconds%3600)/60, seconds%60);
		}
			
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		//return mBinder;
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.d("translert", "service stopped");
		activeFlag = false;
		myCountDownTimer.cancel();
	}
	
	
}
