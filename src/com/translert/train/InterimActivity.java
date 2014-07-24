package com.translert.train;

import com.translert.R;
import com.translert.TransAppDB;
import com.translert.train.utils.PathFinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InterimActivity extends Activity {
	
	public static Intent serviceIntent;
	static int legNum;
	private long curDueTime;
	private String curDestStation;
	private long nextDueTime;
	private String nextDestStation;
	private int legTotal;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		
		Bundle b = getIntent().getExtras();
		int curLeg = b.getInt("cur_leg"); 
		String[] buffArr = new String[1];				
		
		curDueTime = TransAppDB.getAlarmDueTime(this, buffArr); //current endStation
		curDestStation = buffArr[0];										
		
		TransAppDB.updatecurLegNum(this, curLeg+1);
		nextDueTime = TransAppDB.getAlarmDueTime(this, buffArr); //next endStations's DueTime
		nextDestStation = buffArr[0];
		
		legTotal = TransAppDB.getLeg(this, TransAppDB.LEG_TOTAL);
		
		String transfer = ". ", ext = " ";
		int nextLeg = curLeg + 1, rem = legTotal - nextLeg; //at least 1
		if(nextLeg < legTotal){
			if(rem > 1){
				ext = "s ";
			}
			transfer += Integer.toString(rem) + " tranfer" + ext +"remaining.";  
		}
		
		//@
		TextView tv = (TextView) findViewById(R.id.sourceLabel);
		tv.setText("Change trains here, proceeding towards ");
		
		//@
		tv = (TextView) findViewById(R.id.destLabel);
		tv.setText(curDestStation + ".");
		
		//@
		tv = (TextView) findViewById(R.id.detailsLabel);
		tv.setText(WatchActivity.formatTime(nextDueTime) + " minutes" + transfer); // TODO: must be total time
		
		//@
		tv = (TextView) findViewById(R.id.routeDescription);	
		tv.setText(tv.getText() + "\n" + curDestStation + " to " + nextDestStation + " (" + WatchActivity.formatTime(nextDueTime) + " mins)");
	}
	
	/*
	 *
	 */
	public void process(View v){
		TransReceiver.createAlarm(this, nextDueTime, nextDestStation);
		
		Intent watchIntent = new Intent(this, WatchActivity.class);
		startActivity(watchIntent);
		finish();
	}
}
