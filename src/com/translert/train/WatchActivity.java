package com.translert.train;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import com.translert.R;
import com.translert.TransAppDB;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WatchActivity extends Activity {

	private WatchActivity Me = this;
	
	private static final int MSG_REFRESHER = 0;
	private static final int MSG_RESERVED = 1;
	
	private static boolean activityVisible;
	
	int remainingMinutes = 0;
	int remainingSeconds = 0;
	public static WatchActivity activity;
	
	static NotificationManager nm;
	
	static Notification nf;
	static PendingIntent pi;
	
	public TextView tvTime;


	private long totalTime = 0;
	private int currentLeg = 1;
	private int legTotal = 1;
	private String endStation = "N/A";
	private int offset = 0;


	private Handler uiRefresher;
	
	private Long curDueTime;

	private String[] strArr;

	private boolean reCreated;

	private boolean justCreated = false;

	private String curDestStation;

	private String[] destStationArr;

	private long[] totalTimeArr;

	private TextView header1;

	private TextView header2;
	
	private Button button;
	
	static String timeString;
	
	
	public static Handler uiThreadHandler;
	public static Intent serviceIntent;
	
	//Keep in-sync with TransServiceRef
	public static Ringtone ringtone = null;
	public static Vibrator vibrator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
				
		reCreated = false;
		setContentView(R.layout.activity_watch);

		button = (Button) findViewById(R.id.next);

		tvTime = (TextView) findViewById(R.id.detailsLabel);
		Bundle b = getIntent().getExtras();
		
		if(b == null){
			reCreated = true;
			//TODO: get data from database!
		}
		
		//TODO: temporary if-statement, remove this once all data are saved initially in the DB
		if(b != null){
			
			currentLeg = b.getInt("legnum");
			legTotal = b.getInt("totalleg");
			totalTimeArr = b.getLongArray("minutes");
			curDueTime = totalTimeArr[0];
			destStationArr = b.getStringArray("destination");	
			curDestStation = destStationArr[0];
		}
						
		String[] buffArr = new String[1];
		justCreated = true;
		if(reCreated){											
			
			curDueTime = TransAppDB.getAlarmDueTime(Me, buffArr);			
			tvTime.setText(formatTime(curDueTime));
			legTotal = TransAppDB.getLeg(this, TransAppDB.LEG_TOTAL);			
			offset = (int)TransAppDB.getTrainAlarmOffset(this);
			
			if(curDueTime <= offset){				
				//Handler will see this case				
			}else{
				//
				currentLeg = TransAppDB.getLeg(this, TransAppDB.LEG_NUM);
			}			
			curDestStation = buffArr[0];//currentLeg - 1];
		}else{

			//TODO: change 20Sec
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			int pref = Integer.parseInt(  preferences.getString("alarm_timing", "2")  );
			offset = (int)TimeUnit.MINUTES.toMillis(pref);
			Log.d("translert", Integer.toString(offset));			
			
			//save to DB for Broadcasting reference
			TransAppDB.saveCreatedAlertType(this, TransAppDB.TRAIN_ACTIVITY);
			TransAppDB.saveAlarmDueTime(this, offset, totalTimeArr, destStationArr, legTotal); //TODO: not hard-coded

			tvTime.setText(formatTime(curDueTime));			
			TransReceiver.createAlarm(this, curDueTime, curDestStation);
		}
		
		updateLabels();
		
		//the handler is synchronized with the RECEIVER through SQL database
		uiRefresher = new Handler(){
			public void handleMessage(Message msg)
			{        	
				switch (msg.what) {
				
					case MSG_REFRESHER:						
						try{							
							msgHdl(this);
						}catch(Exception e){
							//DEBUG
						}
						break;
						
					case MSG_RESERVED:						
						break;
				}
			}
		};
	}	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiRefresher.removeMessages(MSG_REFRESHER);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		activityVisible = true;
		
		String[] buffArr = new String[1];
		
		//initiate refresher
		if(!justCreated){		
			curDueTime = TransAppDB.getAlarmDueTime(Me, buffArr);
			curDestStation = buffArr[0];
			tvTime.setText(formatTime(curDueTime));
		}
		
		uiRefresher.sendEmptyMessageDelayed(MSG_REFRESHER, 1); // considered overheads appx 20ms
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		activityVisible = false;
		justCreated = false;
		//power saving, stop handler and continue either new or same instance
		uiRefresher.removeMessages(MSG_REFRESHER);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		activityVisible = false;
		justCreated = false;
		uiRefresher.removeMessages(MSG_REFRESHER);
	}
	
	@Override
	public void onBackPressed() {
		if(TransAppDB.isNoActiveAlert(this)){
			super.onBackPressed();
		}else{
			new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
							.setTitle("WARNING")
							.setCancelable(false)
							.setPositiveButton("OK", null)					
							.setMessage("You have a pending alert. You can stop it if you want to.")
							.show();
		}		
	}
	
	//
	public static boolean isActive(){
		return activityVisible;
	} 	
	/*
	 *@
	 */
	private boolean checkAndHandleIfNeedToTransfer(){
		
		int curLeg = TransAppDB.getLeg(this, TransAppDB.LEG_NUM);
		final int totalLeg = TransAppDB.getLeg(this, TransAppDB.LEG_TOTAL);
		curLeg++;
		if(curLeg <= totalLeg){
			new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
			.setTitle("Transfer")
			.setCancelable(false)
			.setMessage("Wake up! You're almost at " + curDestStation + "!")
			.setPositiveButton("OK", new OnClickListener(){				
				@Override
				public void onClick(DialogInterface dialog, int which) {										
					stopAlarm();
					Bundle b = new Bundle();
					b.putInt("cur_leg", currentLeg);
					Intent in = new Intent(WatchActivity.this, InterimActivity.class);
					in.putExtras(b);
					startActivity(in);
					finish();
				}
			}).show();
			
			return true;
			
		}else{
			
			return false;			
		}
	}
	
	/*
	 * @ 
	 */
	public void process (View v){
		Log.d("translert", "try to stop service");
		cleanUp();
		TransAppDB.saveCreatedAlertType(WatchActivity.this,
				TransAppDB.NO_ACTIVITY);
		finish();
	}
	
	/* wrapper for DB cleanUp
	 * @
	 */
	private void cleanUp(){
		//clear DB field for new Alert
		uiRefresher.removeMessages(MSG_REFRESHER);
		
		Intent intent = new Intent("REFRESHER");
		PendingIntent pi1 = PendingIntent.getBroadcast(this, 0, intent, 0 );
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi1);
		
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(TransReceiver.TRANS_ID1);
		
		stopService(new Intent(this, TransServiceRef.class));
	} 
	
	/*
	 * @
	 */
	private void updateLabels(){
		strArr = new String[] {"Arriving at " + curDestStation + " in:", "Part " + currentLeg + " of " + legTotal+ " trip"};		
		if(header1 == null){
			header1 = (TextView) findViewById(R.id.destLabel);
			header2 = (TextView) findViewById(R.id.routeDescription);
		}
		header1.setText(strArr[0]);
		header2.setText(strArr[1]);
	}

	/*@
	 *@
	 *
	 */
	private void msgHdl(Handler handler){		
		String[] buffArr = new String[1];
		long temp = TransAppDB.getAlarmDueTime(Me, buffArr);
		//curDueTime -= 1000L;
		if(temp <= offset){
			tvTime.setText(formatTime(temp));
			if(TransServiceRef.ringtone == null && !TransServiceRef.isAlarming()){
				ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
				vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);				
				TransServiceRef.startAlarm(this, ringtone, vibrator);
			}

			boolean continueToNextLine = checkAndHandleIfNeedToTransfer();
			if(continueToNextLine){
				tvTime.setText(formatTime(curDueTime));												
			}else{
				//NOTE: May be wrapped inside the checkAndHandleIfNeedToTransfer 
		        new AlertDialog.Builder(WatchActivity.this)
					.setMessage("Wake up! You're almost at " + curDestStation + "!")
					.setTitle("Translert")
					.setPositiveButton("OK", new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int id) {
							stopAlarm();
							process(null);
						}})
					.setCancelable(false)
				    .create().show();		        
			}
			handler.removeMessages(MSG_REFRESHER);
		}else{
			curDueTime = temp;
			curDestStation = buffArr[0];
			tvTime.setText(formatTime(curDueTime));
			handler.removeMessages(MSG_REFRESHER);
			handler.sendEmptyMessageDelayed(MSG_REFRESHER, 200);//every 200ms
		}
	}
	
	/* Stop alarm that Ensure syncing
	 */
	private void stopAlarm(){
		if(TransServiceRef.ringtone == null){
			//TODO: no need to check
			if(ringtone != null){
				ringtone.stop();
				vibrator.cancel();
				ringtone = null;
				vibrator = null;
			}
		}else{
			//TODO: no need to check
			if(TransServiceRef.ringtone != null){
				TransServiceRef.ringtone.stop();
				TransServiceRef.vibrator.cancel();
				TransServiceRef.ringtone = null;
				TransServiceRef.vibrator = null;
			}
		}
	}
	
	/* 
	 * 
	 */
	public static String formatTime (long millis) {
		
		int seconds = (int) millis/1000;
		
		if (seconds < 3600) {			
			return String.format("%02d:%02d", seconds/60, seconds%60);
		} else {
			return String.format("%01d:%02d:%02d", seconds/3600, (seconds%3600)/60, seconds%60);
		}
			
	}
}
