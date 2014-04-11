package com.translert.activity;

import com.translert.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BusProcessingActivity extends Activity {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_processing);		
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {		
		if (intent.hasExtra("kill")) {
			finish();
		}
	}

}
