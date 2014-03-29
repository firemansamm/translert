package com.translert;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TripOverviewActivity extends Activity {

	public static Intent serviceIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		PathFinder.State hello = PathFinder.answer;
		TextView tv = (TextView) findViewById(R.id.sourceLabel);
		tv.setText(hello.start.longName + " to");
		tv = (TextView) findViewById(R.id.destLabel);
		tv.setText(hello.end.longName);
		tv = (TextView) findViewById(R.id.detailsLabel);
		tv.setText(String.valueOf(hello.totalTime) + " minutes" + ((hello.xfers.size() > 0) ? ", " + String.valueOf(hello.xfers.size()) + " transfer" + ((hello.xfers.size() > 1) ? "s" : "") : ""));
		hello.xfers.add(0, MainActivity.pf.new Transfer(hello.start, 0));
		tv = (TextView) findViewById(R.id.routeDescription);
		for(int i=0;i<hello.xfers.size();i++){
			if(i == hello.xfers.size() - 1){
				tv.setText(tv.getText() + "\n" + hello.xfers.get(i).position.longName + " to " + hello.end.longName + " (" + String.valueOf(hello.totalTime - hello.xfers.get(i).atTime) + " mins)");
			}else{
				tv.setText(tv.getText() + "\n" + hello.xfers.get(i).position.longName + " to " + hello.xfers.get(i+1).position.longName + " (" + String.valueOf(hello.xfers.get(i+1).atTime - hello.xfers.get(i).atTime) + " mins)");
			}
		}
	}
	
	public void process(View v){
		PathFinder.State hello = PathFinder.answer;
		MainActivity.pref.addTrip(new Trip(System.currentTimeMillis(), hello.start.longName, hello.end.longName, hello.totalTime, 0, hello.xfers.size()));
		Bundle optionsBundle = new Bundle();
		if(hello.xfers.size() < 2) {
			optionsBundle.putString("destination", hello.end.longName);
			optionsBundle.putInt("minutes", hello.totalTime);
		}else {
			optionsBundle.putString("destination", hello.xfers.get(1).position.longName);
			optionsBundle.putInt("minutes", hello.xfers.get(1).atTime);
		}
		optionsBundle.putInt("legnum", 1);
		optionsBundle.putInt("totalleg", hello.xfers.size());
		serviceIntent = new Intent(this, TimerService.class);
		serviceIntent.putExtras(optionsBundle);
		startService(serviceIntent);
		Intent watchIntent = new Intent(this, WatchActivity.class);
		startActivity(watchIntent);
		finish();
	}
	
}
