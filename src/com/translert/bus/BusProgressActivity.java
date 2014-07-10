package com.translert.bus;

import com.translert.BusTrainSelectorActivity;
import com.translert.R;
import com.translert.bus.utils.C;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioManager;
import android.os.Vibrator;

public class BusProgressActivity extends Activity {
	
	boolean activityVisible;
	
	static TextView distanceCounter;
	static String distance;
	public static Handler handler;
	
	static NotificationManager nm;
	static PendingIntent pi;
	static Notification nf;
	
	public static Ringtone ringtone;
	public static Vibrator vibrator;
	
	
	class HandlerCallback implements Handler.Callback {

		@SuppressWarnings("deprecation")
		@Override
		public boolean handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case C.RETURN_DISTANCE:
				distance = (String) msg.obj;
				distanceCounter.setText(distance);
				nf = (new NotificationCompat.Builder(BusProgressActivity.this))
						.setOngoing(true)
						.setContentTitle("Approximately " + distance  +" to destination.")
						.setContentText("Currently en route to " + BusDistanceKeeperService.destination.title + ".")
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentIntent(pi)
						.getNotification();
				nm.notify(0, nf);
				break;
			
			case C.RETURN_DISTANCE_REACHED:
				ringtone.play();
				vibrator.vibrate(C.vibratingPattern, 0);
				
				final Intent restartIntent = new Intent(BusProgressActivity.this, BusProgressActivity.class);
				restartIntent.putExtra("restart", true);
				
				if (activityVisible) {
					BusProgressActivity.nm.cancel(0);
					
					Intent beepIntent = new Intent(BusProgressActivity.this, BusProgressActivity.class);
					beepIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(beepIntent);
					
					new AlertDialog.Builder(BusProgressActivity.this)
					.setMessage("Wake up! You're almost at your destination")
					.setTitle("Translert")
					.setPositiveButton("Yes", new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int id) {
							startActivity(restartIntent);
						}
					})
					.setCancelable(false).create().show();
				} else {
					Toast.makeText(getBaseContext(), "Wake up! You're almost at your destination", Toast.LENGTH_LONG).show();
					PendingIntent piDestroy = PendingIntent.getActivity(
							BusProgressActivity.this
							,0
							,restartIntent
							,PendingIntent.FLAG_CANCEL_CURRENT
							);
					nf = (new NotificationCompat.Builder(BusProgressActivity.this))
							.setOngoing(true)
							.setContentTitle("Wake up!")
							.setContentText("Click to stop the alarm.")
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentIntent(piDestroy)
							.getNotification();
					nm.notify(0, nf);
				}
				break;
			}
			return true;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		distanceCounter.setTypeface(C.numFont);		
		TextView tv = (TextView)findViewById(R.id.approximately);
		tv.setTypeface(C.bodyFont);
		tv = (TextView)findViewById(R.id.to_arrival);
		tv.setTypeface(C.bodyFont);
		
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		pi = PendingIntent.getActivity(this, 0, new Intent(this, BusProgressActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
				
		distance = getIntent().getStringExtra("distance");
		distanceCounter.setText(distance);
		
		ringtone = RingtoneManager.getRingtone(
				getApplicationContext(),
				RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) );
		ringtone.setStreamType(AudioManager.STREAM_ALARM);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
		.setTitle("Translert")
		.setMessage("Do you want to stop the current alarm?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopService(BusEnterDestinationActivity.outputIntent);
				BusProgressActivity.super.onBackPressed();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		})
		;
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	@Override
	protected void onDestroy () {
		super.onDestroy();
		nm.cancel(0);
		ringtone.stop();
		vibrator.cancel();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		activityVisible = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		activityVisible = false;
	}
	
	public void stopKeeper (View v) {
		stopService(BusEnterDestinationActivity.outputIntent);
		finish();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getBooleanExtra("restart", false)) {
			final Intent restartIntent = new Intent(BusProgressActivity.this, BusTrainSelectorActivity.class);
			restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(restartIntent);
			finish();
		}
	}
	
}
