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

@SuppressWarnings("deprecation")
public class ProgressActivity extends Activity {
	
	private static boolean isVisible;
	
	private static TextView topTextView;
	private static TextView distanceCounter;
	private static TextView bottomTextView;
	
	private static NotificationManager nm;
	private static PendingIntent pi;
	private static Notification nf;
	
	private static Ringtone ringtone;
	private static Vibrator vibrator;
	
	public static Handler uiHandler;
	
	private static Intent serviceIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_progress);
		
		uiHandler = new Handler(new UiUpdateCallback());
		
		distanceCounter = (TextView) findViewById(R.id.distance_counter);
		distanceCounter.setTypeface(C.numFont);		
		topTextView = (TextView)findViewById(R.id.approximately);
		topTextView.setTypeface(C.bodyFont);
		bottomTextView = (TextView)findViewById(R.id.to_arrival);
		bottomTextView.setTypeface(C.bodyFont);
		
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		pi = PendingIntent.getActivity(
				this
				,0
				,new Intent(this, ProgressActivity.class)
				,PendingIntent.FLAG_UPDATE_CURRENT
				);
		
		ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) );
		ringtone.setStreamType(AudioManager.STREAM_ALARM);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		serviceIntent = new Intent(this, DistanceUpdateService.class);
		serviceIntent.putExtras(getIntent());
		startService(serviceIntent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isVisible = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isVisible = false;
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
				stopService(serviceIntent);
				ProgressActivity.super.onBackPressed();
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
	protected void onNewIntent(Intent intent) {
		if (intent.getBooleanExtra("restart", false)) {
			final Intent restartIntent = new Intent(ProgressActivity.this, BusTrainSelectorActivity.class);
			restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(restartIntent);
			finish();
		}
	}
	
	@Override
	protected void onDestroy () {
		super.onDestroy();
		stopService(serviceIntent);
		nm.cancel(0);
		ringtone.stop();
		vibrator.cancel();
	}
	
	public void stop (View v) {
		finish();
	}
	
	class UiUpdateCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			
			case C.DISTANCE_MESSAGE:
				String distance = (String) msg.obj;
				distanceCounter.setText(distance);
				topTextView.setText("Approximately");
				bottomTextView.setText("to destination");
				nf = (new NotificationCompat.Builder(ProgressActivity.this))
						.setOngoing(true)
						.setContentTitle("Approximately " + distance  +" to destination.")
						.setContentText("Currently en route to " + DistanceUpdateService.destination.title + ".")
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentIntent(pi)
						.getNotification();
				nm.notify(0, nf);
				return true;
			
			case C.ALARM_MESSAGE:
				ringtone.play();
				vibrator.vibrate(C.vibratingPattern, 0);
				
				stopService(serviceIntent);
				
				if (isVisible) {
					ProgressActivity.nm.cancel(0);
					
					Intent beepIntent = new Intent(ProgressActivity.this, ProgressActivity.class);
					beepIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(beepIntent);
					
					new AlertDialog.Builder(ProgressActivity.this)
					.setMessage("Wake up! You're almost at your destination")
					.setTitle("Translert")
					.setPositiveButton("Yes", new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int id) {
							startActivity(
								new Intent(ProgressActivity.this, ProgressActivity.class).putExtra("restart", true));
						}
					})
					.setCancelable(false).create().show();
				} else {
					Toast.makeText(getBaseContext(), "Wake up! You're almost at your destination", Toast.LENGTH_LONG).show();
					PendingIntent piDestroy = PendingIntent.getActivity(
							ProgressActivity.this
							,0
							,new Intent(ProgressActivity.this, ProgressActivity.class).putExtra("restart", true)
							,PendingIntent.FLAG_CANCEL_CURRENT
							);
					nf = (new NotificationCompat.Builder(ProgressActivity.this))
							.setOngoing(true)
							.setContentTitle("Wake up!")
							.setContentText("Click to stop the alarm.")
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentIntent(piDestroy)
							.getNotification();
					nm.notify(0, nf);
				}
				return true;
				
			case C.BUS_STOP_NOT_FOUND_MESSAGE:
				String busStopName = getIntent().getStringExtra("busDestination");
				String busNumber = getIntent().getStringExtra("busNumber");
				Toast.makeText(
						ProgressActivity.this, "Cannot find " + busStopName + " on bus route " + busNumber, Toast.LENGTH_LONG).show();
				finish();
				return true;
			
			case C.LOCATION_PROVIDER_NOT_FOUND_MESSAGE:
				
			}
			
			return false;
		}
	}
	
}
