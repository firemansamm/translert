package com.translert;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.translert.R;
import com.translert.bus.BusEnterNumberActivity;
import com.translert.bus.utils.GPSTracker;
import com.translert.train.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class BusTrainSelectorActivity extends SherlockActivity {
	
//	public Context context = this;
	boolean listenerFlag;
	EditText destinationTextbox;
	static public GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_train);
		
		gps = new GPSTracker(this);
		if (!gps.canGetGPS()) {
			gps.showSettingsAlert();
		}
		
		destinationTextbox = (EditText) findViewById(R.id.destinationTextBox);
		
		TextView.OnEditorActionListener keyListener = new TextView.OnEditorActionListener(){
			
			@Override
	        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				
				if (listenerFlag) {	
					
					
					listenerFlag = false;
					String destination = view.getText().toString();
					Log.d("translert", "received destination " + destination);
	                Intent i = new Intent (BusTrainSelectorActivity.this, RoutePlanningActivity.class);
	                i.putExtra("destination", destination);
	                startActivity(i);
	                
				}
				
				return true;
				
	        }
			
	    };
	    
	    destinationTextbox.setOnEditorActionListener(keyListener);
		
	}
	
	public void startMRTMode (View v) {
		Intent startMRTIntent = new Intent (this, MainActivity.class);
		startActivity (startMRTIntent);
		
	}
	
	public void startBusMode (View v) {
		
		Intent startBusIntent = new Intent (this, BusEnterNumberActivity.class);
		startActivity (startBusIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, ShowPreferenceActivity.class));
		
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listenerFlag = true;
	}
	

}
