package com.translert.train;

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
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WatchActivity extends Activity {
	
	boolean activityVisible;
	
	int remainingMinutes = 0;
	int remainingSeconds = 0;
	public static WatchActivity activity;
	
	static NotificationManager nm;
	
	static Notification nf;
	static PendingIntent pi;
	
	public TextView tvTime;
	static String timeString;

	public static Handler uiThreadHandler;
	public static Intent serviceIntent;
	
	public static Ringtone ringtone;
	public static Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch);
		uiThreadHandler = new Handler(new UIThreadHandlerCallback());
		
		
		Bundle b = getIntent().getExtras();
		serviceIntent = new Intent(this, TimerService.class);
		serviceIntent.putExtras(b);
		startService(serviceIntent);
		
		
		activity = this;
		pi = PendingIntent.getActivity(WatchActivity.this, 0, new Intent(WatchActivity.this, WatchActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		nm = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		tvTime = (TextView) findViewById(R.id.detailsLabel);
		
		ringtone = RingtoneManager.getRingtone(
				getApplicationContext(),
				RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) );
		ringtone.setStreamType(AudioManager.STREAM_ALARM);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	
	public void process (View v){
		Log.d("translert", "try to stop service");
		finish();
	}
	
	class UIThreadHandlerCallback implements Handler.Callback {
		@SuppressWarnings("deprecation")
		@Override
	    public boolean handleMessage (Message msg) {
			
			switch (msg.what) {
			
			case C.RETURN_TIMER_START:
				String[] titles = (String[]) msg.obj;
				((TextView) findViewById(R.id.destLabel)).setText(titles[0]);
				((TextView) findViewById(R.id.routeDescription)).setText(titles[1]);
				break;
				
				
			case C.RETURN_TIMER_RUNNING:
				
				timeString = (String) msg.obj;
				nf = (new NotificationCompat.Builder(WatchActivity.this))
						.setOngoing(true)
						.setContentTitle("Approximately " + timeString + " to destination.")
						.setContentText("Currently en route to " + TimerService.endStation + ".")
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentIntent(pi)
						.getNotification();
				nm.notify(0, nf);
				
				Log.d("translert", timeString);
				tvTime.setText(timeString);
				break;
			
			case C.RETURN_TIMER_OVER:
				//sec is obviously 0
				//alarm here
				ringtone.play();
				vibrator.vibrate(C.vibratingPattern, 0);
				
				
				
				if (activityVisible) {
					Intent beepIntent = new Intent(WatchActivity.this, WatchActivity.class);
					beepIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(beepIntent);
					
					new AlertDialog.Builder(WatchActivity.this).setMessage("Wake up! You're almost at " + TimerService.endStation + "!").setTitle("Translert").setPositiveButton("Yes", new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int id) {
							if(TimerService.currentLeg < TimerService.legTotal){
								startActivity(
										new Intent(WatchActivity.this, WatchActivity.class).putExtra("interim", true));
							} else {
								startActivity(
										new Intent(WatchActivity.this, WatchActivity.class).putExtra("restart", true));
							}
							
						}
					}).setCancelable(false).create().show();
				} else {
					Toast.makeText(getBaseContext(), "Wake up! You're almost at your destination", Toast.LENGTH_LONG).show();
					
					NotificationCompat.Builder builder = new NotificationCompat.Builder(WatchActivity.this)
					.setOngoing(true)
					.setContentTitle("Wake up!")
					.setContentText("Click to stop the alarm.")
					.setSmallIcon(R.drawable.ic_launcher);
//					nf = (new NotificationCompat.Builder(WatchActivity.this))
//							.setOngoing(true)
//							.setContentTitle("Wake up!")
//							.setContentText("Click to stop the alarm.")
//							.setSmallIcon(R.drawable.ic_launcher)
//							.setContentIntent(piDestroy)
//							.getNotification();
					if(TimerService.currentLeg < TimerService.legTotal){
						
						PendingIntent piInterim = PendingIntent.getActivity(
								WatchActivity.this
								,0
								,new Intent(WatchActivity.this, WatchActivity.class).putExtra("interim", true)
								,PendingIntent.FLAG_CANCEL_CURRENT);
						nf = builder.setContentIntent(piInterim).getNotification();
					} else {
						PendingIntent piDestroy = PendingIntent.getActivity(
								WatchActivity.this
								,0
								,new Intent(WatchActivity.this, WatchActivity.class).putExtra("restart", true)
								,PendingIntent.FLAG_CANCEL_CURRENT);
						nf = builder.setContentIntent(piDestroy).getNotification();
					}
					nm.notify(0, nf);
				}
				break;
			}
			return true;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		nm.cancel(0);
		stopService(serviceIntent);
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
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getBooleanExtra("restart", false)) {
			final Intent restartIntent = new Intent(WatchActivity.this, BusTrainSelectorActivity.class);
			restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			restartIntent.putExtra("restart", true);
			startActivity(restartIntent);
			finish();
		} else if (intent.getBooleanExtra("interim", false)) {
			final Intent interimIntent = new Intent(WatchActivity.this, InterimActivity.class);
			interimIntent.putExtra("leg", TimerService.currentLeg + 1);
			startActivity(interimIntent);
			nm.cancel(0);
			stopService(serviceIntent);
			ringtone.stop();
			vibrator.cancel();
		}
	}
	
}
