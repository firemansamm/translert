package com.translert;

import com.translert.bus.C;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class TimerService extends Service {
	
	public static Handler uiThreadHandler;
	
	static Message msg;
	public static boolean activeFlag = false;
	
	public static long totalTime;
	
	public static int /*secondsRemaining = 0,*/ currentLeg = 0, legTotal = 0;
	public static String endStation = "";
	

	
	static MyCountDownTimer myCountDownTimer;
	
	@Override
	public void onCreate() {
		//get handler from the activity to update UI
		//TimerService.uiThreadHandler = new Handler(Looper.getMainLooper());
		TimerService.uiThreadHandler = WatchActivity.uiThreadHandler;
	}

	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (activeFlag == false) {
			
			activeFlag = true;
			Bundle b = intent.getExtras();
			totalTime = (long) b.getInt("minutes") * 60 * 1000;
			Log.d("translert", "received request for timer of " + String.valueOf(totalTime) + " milliseconds!");
			currentLeg = b.getInt("legnum");
			legTotal = b.getInt("totalleg");
			endStation = b.getString("destination");
			
			
			msg = uiThreadHandler.obtainMessage(C.RETURN_TIMER_START, 
					new String[] {"Arriving at " + endStation + " in:", "Part " + currentLeg + " of " + legTotal+ " trip"});
			uiThreadHandler.sendMessage(msg);
			
			myCountDownTimer = new MyCountDownTimer(totalTime, 1000L);
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
			String formattedTime = formatTime(millisUntilFinished);
			msg = uiThreadHandler.obtainMessage(C.RETURN_TIMER_RUNNING, formattedTime);
			uiThreadHandler.sendMessage(msg);
		}
		 
		@Override
		public void onFinish() {
			//send a message to the UI handler to update UI on finish
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
	
}
