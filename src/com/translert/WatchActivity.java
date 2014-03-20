package com.translert;

import com.translert.TimerService.Worker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

public class WatchActivity extends Activity {
	
	int remainingMinutes = 0;
	int remainingSeconds = 0;
	public static WatchActivity activity;
	NotificationManager nm;
	static Notification nf;
	static PendingIntent pi;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch);
		activity = this;
		pi = PendingIntent.getActivity(WatchActivity.this, 0, new Intent(WatchActivity.this, WatchActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		TextView tv = (TextView) findViewById(R.id.destLabel);
		tv.setText("Arriving at " + TimerService.endStation + " in:");
		tv = (TextView) findViewById(R.id.routeDescription);
		tv.setText("Part " + String.valueOf(TimerService.currentLeg) + " of " + String.valueOf(TimerService.legTotal) + " of trip");
		nm = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		
	    remainingMinutes = TimerService.secondsRemaining / 60;
		remainingSeconds = TimerService.secondsRemaining % 60;
		String minText = ((remainingMinutes < 10)?"0":"") + String.valueOf(remainingMinutes),
				secText = ((remainingSeconds < 10)?"0":"") + String.valueOf(remainingSeconds);
		nf = (new NotificationCompat.Builder(this)).setOngoing(true).setContentTitle("Approximately " + minText + ":" + secText + " to destination.").setContentIntent(pi).setContentText("Currently en route to " + TimerService.endStation + ".").setSmallIcon(R.drawable.ic_launcher).getNotification();
		tv = (TextView) findViewById(R.id.detailsLabel);
	    nm.notify(0, nf);
		tv.setText(minText+":"+secText);	
		TimerService.tickHandlers.add(new Worker(){
			@Override
			public void work(int sec){
				//update the display here
				remainingMinutes = sec / 60;
				remainingSeconds = sec % 60;
				TextView tv = (TextView) findViewById(R.id.detailsLabel);
				String minText = ((remainingMinutes < 10)?"0":"") + String.valueOf(remainingMinutes),
						secText = ((remainingSeconds < 10)?"0":"") + String.valueOf(remainingSeconds);
				nf = (new NotificationCompat.Builder(WatchActivity.this)).setOngoing(true).setContentTitle("Approximately " + minText + ":" + secText + " to destination.").setContentText("Currently en route to " + TimerService.endStation + ".").setSmallIcon(R.drawable.ic_launcher).setContentIntent(pi).getNotification();
				nm.notify(0, nf);
				tv.setText(minText+":"+secText);
			}
		});
		
		TimerService.endHandlers.add(new Worker(){
			@Override
			public void work(int sec){
				//sec is obviously 0
				//alarm here
				Intent beepIntent = new Intent(WatchActivity.this, WatchActivity.class);
				startActivity(beepIntent);
				new AlertDialog.Builder(WatchActivity.this).setMessage("Wake up! You're almost at " + TimerService.endStation + "!").setTitle("Translert").setPositiveButton("Yes", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int id) {
						nm.cancel(0);
						if(TimerService.currentLeg < TimerService.legTotal){
							Intent interimIntent = new Intent(WatchActivity.this, InterimActivity.class);
							interimIntent.putExtra("leg", TimerService.currentLeg + 1);
							startActivity(interimIntent);
						}
						finish();
					}
				}).setCancelable(false).create().show();
				
			}
		});
	}
	
	public void process (View v){
		stopService(TripOverviewActivity.serviceIntent);
		nm.cancel(0);
		finish();
	}
}
