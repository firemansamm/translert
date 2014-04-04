package com.translert.activity;

import com.translert.MainActivity;
import com.translert.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BusTrainSelectorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_train);
		
		
		
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
