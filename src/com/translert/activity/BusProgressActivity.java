package com.translert.activity;

import com.translert.InterimActivity;
import com.translert.R;
import com.translert.TimerService;
import com.translert.WatchActivity;
import com.translert.bus.C;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class BusProgressActivity extends Activity {
	
	static TextView distanceCounter;
	static String distance;
	public static Handler handler;
	
	static NotificationManager nm;
	static PendingIntent pi;
	static Notification nf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent killIntent = new Intent (this, BusProcessingActivity.class);
		killIntent.putExtra("kill", true);
		killIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		killIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(killIntent);
		
		if (getIntent().hasExtra("nullDestination")) {
			Intent reEnterDestination = new Intent (this, BusEnterDestinationActivity.class);
			reEnterDestination.putExtras( getIntent().getExtras() );
			reEnterDestination.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			reEnterDestination.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(reEnterDestination);
			finish();
		}
		
		
		setContentView(R.layout.activity_bus_progress);
		
		handler = new Handler(new HandlerCallback());
		distanceCounter = (TextView) findViewById(R.id.distance_counter);
		
		nm = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		pi = PendingIntent.getActivity(this, 0, new Intent(this, BusProgressActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		distance = getIntent().getStringExtra("distance");
		distanceCounter.setText(distance);
		
	}
	
	class HandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case C.RETURN_DISTANCE:
				distance = (String) msg.obj;
				distanceCounter.setText(distance);
				nf = (new NotificationCompat.Builder(BusProgressActivity.this)).setOngoing(true).setContentTitle("Approximately " + distance  +" to destination.").setContentText("Currently en route to " + BusDistanceKeeperService.destination.title + ".").setSmallIcon(R.drawable.ic_launcher).setContentIntent(pi).getNotification();
				nm.notify(0, nf);
				break;
			
			
			case C.RETURN_DISTANCE_REACHED:
				Intent beepIntent = new Intent(BusProgressActivity.this, BusProgressActivity.class);
				beepIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(beepIntent);
				
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
				final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				r.play();
				
				new AlertDialog.Builder(BusProgressActivity.this).setMessage("Wake up! You're almost at your destination").setTitle("Translert").setPositiveButton("Yes", new OnClickListener(){
					
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						BusProgressActivity.nm.cancel(0);
						r.stop();
						Intent restartIntent = new Intent(BusProgressActivity.this, BusTrainSelectorActivity.class);
						restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(restartIntent);
						finish();
					}
				}).setCancelable(false).create().show();
				break;
			
			}
			
			return true;
			
		}
		
	}
	
	public void stopKeeper (View v) {
//		Intent stopKeeperIntent = new Intent (this, BusDistanceKeeperService.class);
		stopService(BusEnterDestinationActivity.outputIntent);
		finish();
	}
	
	
}
