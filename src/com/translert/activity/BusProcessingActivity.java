package com.translert.activity;

import com.translert.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class BusProcessingActivity extends Activity {
	
	public Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		context = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_processing);
		
	}

}
