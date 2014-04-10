package com.translert;



import com.translert.bus.C;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WatchActivity extends Activity {
	
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
		
	}
	
	
	public void process (View v){
		stopService(WatchActivity.serviceIntent);
		nm.cancel(0);
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
				nf = (new NotificationCompat.Builder(WatchActivity.activity)).setOngoing(true).setContentTitle("Approximately " + timeString + " to destination.").setContentText("Currently en route to " + TimerService.endStation + ".").setSmallIcon(R.drawable.ic_launcher).setContentIntent(pi).getNotification();
				nm.notify(0, nf);
				
				Log.d("translert", timeString);
				tvTime.setText(timeString);
				break;
			
			case C.RETURN_TIMER_OVER:
				//sec is obviously 0
				//alarm here
				
				Intent beepIntent = new Intent(WatchActivity.this, WatchActivity.class);
				beepIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(beepIntent);
				
				new AlertDialog.Builder(WatchActivity.this).setMessage("Wake up! You're almost at " + TimerService.endStation + "!").setTitle("Translert").setPositiveButton("Yes", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int id) {
						WatchActivity.nm.cancel(0);
						if(TimerService.currentLeg < TimerService.legTotal){
							Intent interimIntent = new Intent(WatchActivity.this, InterimActivity.class);
							interimIntent.putExtra("leg", TimerService.currentLeg + 1);
							startActivity(interimIntent);
						}
						finish();
					}
				}).setCancelable(false).create().show();
				break;
				
			}
			return true;
		}
	}
	
}
