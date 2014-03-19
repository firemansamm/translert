package com.translert;

import com.translert.TimerService.Worker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WatchActivity extends Activity {
	
	int remainingMinutes = 0;
	int remainingSeconds = 0;
	public static WatchActivity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch);
		activity = this;
		TextView tv = (TextView) findViewById(R.id.destLabel);
		tv.setText("Arriving at " + TimerService.endStation + " in:");
		tv = (TextView) findViewById(R.id.routeDescription);
		tv.setText("Part " + String.valueOf(TimerService.currentLeg) + " of " + String.valueOf(TimerService.legTotal) + " of trip");
		TimerService.tickHandlers.add(new Worker(){
			@Override
			public void work(int sec){
				//update the display here
				remainingMinutes = sec / 60;
				remainingSeconds = sec % 60;
				TextView tv = (TextView) findViewById(R.id.detailsLabel);
				String minText = ((remainingMinutes < 10)?"0":"") + String.valueOf(remainingMinutes),
						secText = ((remainingSeconds < 10)?"0":"") + String.valueOf(remainingSeconds);
				tv.setText(minText+":"+secText);				
			}
		});
		
		TimerService.endHandlers.add(new Worker(){
			@Override
			public void work(int sec){
				//sec is obviously 0
				//alarm here
				new AlertDialog.Builder(WatchActivity.this).setMessage("You're almost at " + TimerService.endStation + "!").setTitle("Translert").create().show();
				if(TimerService.currentLeg < TimerService.legTotal){
					Intent interimIntent = new Intent(WatchActivity.this, TripOverviewActivity.class);
					startActivity(interimIntent);
				}
				stopService(TripOverviewActivity.serviceIntent);
				finish();
			}
		});
	}
	
	public void process (View v){
		stopService(TripOverviewActivity.serviceIntent);
		finish();
	}
}
