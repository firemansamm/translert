package com.translert.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.translert.R;
import com.translert.R.id;
import com.translert.R.layout;
import com.translert.bus.*;

public class BusEnterDestinationActivity extends Activity {
	
	String busNumber;
	String busDestination;
	EditText busDestinationTextbox;
	boolean listenerFlag;
	public static Handler uiHandler;
	Intent inputIntent;
	public static Intent outputIntent;
	public static GPSTracker gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_enter_destination);
		
//		inputIntent = getIntent();
//		busNumber = inputIntent.getStringExtra("busNumber");
		busNumber = "placeholder";
		busDestinationTextbox = (EditText) findViewById(R.id.busDestinationTextbox);
		gps = new GPSTracker(this);
		
		TextView.OnEditorActionListener keyListener = new TextView.OnEditorActionListener(){
			
			@Override
	        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				
				if (listenerFlag) {
					
					listenerFlag = false;
					busDestination = view.getText().toString();
					Log.d("translert", "received bus destination " + busDestination);
					
					outputIntent = new Intent (BusEnterDestinationActivity.this, BusProcessingActivity.class);
					startActivity (outputIntent);
					
					outputIntent = new Intent (BusEnterDestinationActivity.this, BusDistanceKeeperService.class);
					outputIntent.putExtra("busNumber", busNumber);
	                outputIntent.putExtra("busDestination", busDestination);
	                startService (outputIntent);
	                Log.d("tranlert", "Intent sent to Service");
	                
				}
				
				return true; 
				
	        }
			
	    };
	    
	    busDestinationTextbox.setOnEditorActionListener(keyListener);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listenerFlag = true;
	}

}
