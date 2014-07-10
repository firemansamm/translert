package com.translert.bus; 

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.translert.R;
import com.translert.bus.utils.C;

public class BusEnterDestinationActivity extends Activity{
	
	String busNumber;
	String busDestination;
	EditText busDestinationTextbox;
	AutoCompleteTextView busDestinationAuto;
	boolean listenerFlag;
	Intent inputIntent;
	static Intent outputIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_enter_destination);
		
		TextView title = (TextView)findViewById(R.id.select_destination);
		title.setTypeface(C.headingFont);
		
		inputIntent = getIntent();
		busNumber = inputIntent.getStringExtra("busNumber");
		busDestinationAuto = (AutoCompleteTextView) findViewById(R.id.busDestinationAuto);

		try {
			ArrayList<String> busStopNames = F.getBusStopNames(this, busNumber);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_list_item, busStopNames);
			busDestinationAuto.setAdapter(adapter);
		} catch (Exception e) {
			Toast.makeText(this, "Cannot find bus number " +busNumber, Toast.LENGTH_LONG).show();
			this.finish();
		}
		
		busDestinationAuto.setOnKeyListener(new OnKeyListener() {
	    	public boolean onKey(View v, int keyCode, KeyEvent event) {
	    		if (event.getAction() == KeyEvent.ACTION_DOWN
	    				&& keyCode == KeyEvent.KEYCODE_ENTER) {
	    			submitDestination(v);
					return true;
	    		} else {
	    			return false;
	    		}
	    	}
	    });
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listenerFlag = true;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra("nullDestination")) {
			String busDestination = intent.getStringExtra("nullDestination");
			String busNumber = intent.getStringExtra("busNumber");
			Toast.makeText(this, "Cannot find " + busDestination + " on bus route " + busNumber, Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void submitDestination (View v) {
		if (listenerFlag) {
			listenerFlag = false;
			busDestination = busDestinationAuto.getText().toString();
			Log.d("translert", "received bus destination " + busDestination);
			
			outputIntent = new Intent (BusEnterDestinationActivity.this, BusDistanceKeeperService.class);
			outputIntent.putExtra("busNumber", busNumber);
            outputIntent.putExtra("busDestination", busDestination);
            startService (outputIntent);
            Log.d("tranlert", "Intent sent to Service");
		}
	}

}
