package com.translert.activity;

import com.translert.R;
import com.translert.bus.C;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class BusProgressActivity extends Activity {
	
	TextView distanceCounter;
	String distance;
	Handler handler;
	Intent outputIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_progress);
		handler = new Handler(new handlerCallback());
		Intent inputIntent = getIntent();
		distance = inputIntent.getStringExtra("distance");
		
		distanceCounter = (TextView) findViewById(R.id.distance_counter);
		distanceCounter.setText("" + distance);
		
	}
	
	private Runnable PeriodicalUpdates = new Runnable() {
		
		@Override
		public void run () {
			
			outputIntent = new Intent(BusProgressActivity.this, com.translert.bus.ProcessingIntentService.class);
			outputIntent.putExtra("messageFlag", C.GET_DISTANCE);
			startService(outputIntent);
			handler.postDelayed(PeriodicalUpdates, C.DELAY_AMOUNT);
			
		}
		
	};
	
	class handlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case C.RETURN_DISTANCE:
				distanceCounter.setText("" + (Integer) msg.obj );
			}
			
			return true;
			
		}
		
	}
	
}
