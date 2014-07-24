package com.translert.train;

import com.translert.*;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

public class TransReceiver extends BroadcastReceiver {

	public static final int TRANS_ID1 = 101;
	public static final int TRANS_ID2 = 102;
	public static Long SEC_IN_MILLI = 1000L;	

	@Override
    public void onReceive(Context context, Intent intent) {
				
		Intent myIntent = new Intent(context, TransServiceRef.class);
        context.startService(myIntent);
    }
	
	/*Use to init alarm and refresh Noti Manager by recreating alarm as well
     * @
     */
    public static void createAlarm(Context context, long curDueTime, String destStation){
    	Notification nf;
    	NotificationManager nm;
    	PendingIntent pi;
    	long triggerTime = SystemClock.elapsedRealtime() + 1000; //TODO: every 1sec

    	TransAppDB.updateAlarmDueTime(context, curDueTime);
		
		//update Noti Panel
		pi = PendingIntent.getActivity(context, 0,
			       new Intent(context, WatchActivity.class), //TODO: consider App's main activity
			       PendingIntent.FLAG_CANCEL_CURRENT);
		
		nf = new NotificationCompat.Builder(context)
				.setOngoing(true)
				.setContentTitle("Approximately " + WatchActivity.formatTime(curDueTime) + " to destination.")
				.setContentText("Currently en route to " + destStation + ".")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pi)
				.getNotification();
		
		nm = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		nm.notify(TRANS_ID1, nf);
    	
    	
    	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    	//TransReceiver as REFRESHER
		Intent intent = new Intent("REFRESHER");
		pi = PendingIntent.getBroadcast( context, 0, intent, 0 );

		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
		//Toast.makeText(context, "Created AlarmService", Toast.LENGTH_LONG).show();
    }

}