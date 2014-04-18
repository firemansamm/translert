package com.translert.activity;

import com.translert.MainActivity;
import com.translert.R;
import com.translert.bus.SGGPosition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class BusTrainSelectorActivity extends Activity {
	
	public Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_train);
		
		SGGPosition trythisout = F.getBusStopPosition(this, "Blk 410", "14");
		Log.d("translert", trythisout.format());
		
	}
	
	public void startMRTMode (View v) {
		Intent startMRTIntent = new Intent (this, MainActivity.class);
		startActivity (startMRTIntent);
		
	}
	
	public void startBusMode (View v) {
		
		Intent startBusIntent = new Intent (this, BusEnterNumberActivity.class);
		startActivity (startBusIntent);
	}
	
	
	

}
