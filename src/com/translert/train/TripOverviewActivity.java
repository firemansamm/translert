package com.translert.train;

import java.util.concurrent.TimeUnit;

import com.translert.R;
import com.translert.train.utils.PathFinder;
import com.translert.train.utils.Trip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TripOverviewActivity extends Activity {

	private String[] savedDestArr;
	private long[] savedTimeArr;
	
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
		
		final int Sz = hello.xfers.size();
		final int lastIdx = Sz - 1;
		savedDestArr = new String[Sz];		
		savedTimeArr = new long[Sz];
		for(int i = 0; i < Sz; i++){
			if(i == lastIdx){
				savedDestArr[i] = hello.end.longName;
				savedTimeArr[i] = hello.totalTime - hello.xfers.get(i).atTime;
				tv.setText(tv.getText() + "\n" + hello.xfers.get(i).position.longName + " to " + savedDestArr[i] + " (" + String.valueOf(savedTimeArr[i]) + " mins)");
			}else{
				savedDestArr[i] = hello.xfers.get(i+1).position.longName;
				savedTimeArr[i] = hello.xfers.get(i+1).atTime - hello.xfers.get(i).atTime;
				tv.setText(tv.getText() + "\n" + hello.xfers.get(i).position.longName + " to " + savedDestArr[i] + " (" + String.valueOf(savedTimeArr[i]) + " mins)");
			}
		}
	}
	
	public void process(View v){
		
		Bundle optionsBundle = getBundle();
		//calling TimerService from WatchActivity instead of here, since
		//TimerService needs the handler from WatchActivity
		//optionsBundle passed from this -> WatchActivity -> TimerService
		
//		serviceIntent = new Intent(this, TimerService.class);
//		serviceIntent.putExtras(optionsBundle);
//		startService(serviceIntent);
		
		Intent watchIntent = new Intent(this, WatchActivity.class);
		watchIntent.putExtras(optionsBundle);
		startActivity(watchIntent);
	}
	
	public Bundle getBundle(){
		PathFinder.State hello = PathFinder.answer;
		MainActivity.pref.addTrip(new Trip(System.currentTimeMillis(),
								  hello.start.longName,
								  hello.end.longName,
								  hello.totalTime,
								  0,
								  hello.xfers.size()));
		Bundle optionsBundle = new Bundle();
		int Sz = hello.xfers.size();
		String[] newArr = savedDestArr;
		long[] newArr1 = new long[Sz];

		for(int i = 0; i < Sz; i++){
			//newArr
			newArr1[i] = TimeUnit.MINUTES.toMillis(savedTimeArr[i]); //TODO: long?
		}
		optionsBundle.putStringArray("destination", newArr);
		optionsBundle.putLongArray("minutes", newArr1);
		optionsBundle.putInt("legnum", 1);
		optionsBundle.putInt("totalleg", hello.xfers.size());
		
		return optionsBundle;
	}
}
