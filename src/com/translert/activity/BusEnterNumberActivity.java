package com.translert.activity;

import com.translert.R;
import com.translert.R.id;
import com.translert.R.layout;
import com.translert.bus.GPSTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class BusEnterNumberActivity extends Activity {
	
	EditText busNoTextBox;
	boolean listenerFlag;
	public static GPSTracker gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_enter_number);
		
		gps = new GPSTracker(this);
		busNoTextBox = (EditText) findViewById(R.id.busNoTextBox);
		
		
		TextView.OnEditorActionListener keyListener = new TextView.OnEditorActionListener(){
			
			@Override
	        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				
				if (listenerFlag) {	
					
					
					listenerFlag = false;
					String busNumber = view.getText().toString();
					Log.d("translert", "received bus number " + busNumber);
	                Intent i = new Intent (BusEnterNumberActivity.this, BusEnterDestinationActivity.class);
	                i.putExtra("busNumber", busNumber);
	                startActivity(i);
	                
				}
				
				return true;
				
	        }
			
	    };
	    
	    busNoTextBox.setOnEditorActionListener(keyListener);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listenerFlag = true;
	}
	
}