package com.translert.train;

import com.translert.*;
import com.translert.bus.utils.C;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class TransServiceRef extends Service{
	
	//Must be checked in UI thread
	public static Ringtone ringtone = null;
	public static Vibrator vibrator = null;
	private static AudioManager audio;
	private static final double TRANS_STD_VOLUME_PERCENTAGE = 25.0;
	
	private static boolean alarmed = false;
	
	public TransServiceRef(){
		//
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		//
		return null;
	}

	@Override
	public void onCreate() {
		//
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		//
		Notification nf;
    	NotificationManager nm;
    	PendingIntent pi;
    	String[] buffArr = new String[1];
		long curDueTime = TransAppDB.getAlarmDueTime(this, buffArr) - 1000L;
		long offset = TransAppDB.getTrainAlarmOffset(this);
		
		if(curDueTime < offset){					
			//alarm
			alarmed = true;
			if(WatchActivity.ringtone != null){ //to ensure 100% sync with other context
				WatchActivity.ringtone.stop();
				WatchActivity.vibrator.cancel();
			}
			ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));			
			vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			startAlarm(this, ringtone, vibrator);			
						
			String strMsg = "Click to stop the alarm";

			//update Noti Panel
			pi = PendingIntent.getActivity(this, 0,
				       new Intent(this, WatchActivity.class), //TODO: consider App's main activity
				       PendingIntent.FLAG_CANCEL_CURRENT);
			
			nf = new NotificationCompat.Builder(this)
					.setOngoing(true)
					.setContentTitle("Wake Up!")
					.setContentText(strMsg)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(pi)
					.getNotification();
			
			nm = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			nm.notify(TransReceiver.TRANS_ID1, nf);
			
			if(!WatchActivity.isActive()){
				Toast.makeText(getBaseContext(), "Wake up! You're almost at your destination", Toast.LENGTH_LONG).show();
			}
			
			//we're done or may be continue to other LINE
			TransAppDB.updateAlarmDueTime(this, curDueTime);
			
		}else{
			
			//recreate itself
			TransReceiver.createAlarm(this, curDueTime, buffArr[0]);			
		}
	}
	
	/*
	 * @Used internally & by WatchActivity
	 */
	public static void startAlarm(Context c, Ringtone r, Vibrator v){
		r.setStreamType(AudioManager.STREAM_ALARM);
		r.play();
		v.vibrate(C.vibratingPattern, 0);
		//set volume at TRANS_STD_VOLUME_PERCENTAGE (20%) of MAX
		audio = (AudioManager)  c.getSystemService(Context.AUDIO_SERVICE);
		int vol = audio.getStreamMaxVolume(AudioManager.STREAM_ALARM);

		vol = (int)((double)vol * (TRANS_STD_VOLUME_PERCENTAGE / 100.0));
		
		audio.setStreamVolume(AudioManager.STREAM_ALARM,
							  vol,
							  AudioManager.FLAG_VIBRATE);
	}
	
	/*
	 */
	public static boolean isAlarming(){
		return alarmed;
	}
}
