package com.translert.bus; 

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.translert.R;
import com.translert.bus.utils.C;
import com.translert.bus.utils.F;

public class EnterBusStopNameActivity extends Activity{
	
	AutoCompleteTextView busStopNameEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_enter_destination);
		
		TextView title = (TextView)findViewById(R.id.select_destination);
		title.setTypeface(C.headingFont);
		
		String busNumber = getIntent().getStringExtra("busNumber");
		busStopNameEditText = (AutoCompleteTextView) findViewById(R.id.busDestinationAuto);

		try {
			ArrayList<String> busStopNames = F.getBusStopNames(this, busNumber);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_list_item, busStopNames);
			busStopNameEditText.setAdapter(adapter);
		} catch (Exception e) {
			Toast.makeText(this, "Cannot find bus number " + busNumber, Toast.LENGTH_LONG).show();
			finish();
		}
		busStopNameEditText.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String busStopName = (String) parent.getItemAtPosition(position);
				Log.d("translert", "Received bus stop name " + busStopName);
		        startActivity (new Intent (EnterBusStopNameActivity.this, ProgressActivity.class)
				.putExtras(getIntent())
				.putExtra("busDestination", busStopName));
				
			}
		});
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra("nullLocationServices")) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		   	 
	        // Setting Dialog Title
	        alertDialog.setTitle("GPS settings");
	 
	        // Setting Dialog Message
	        alertDialog.setMessage("Please go to Settings and enable location services.");
	 
	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	            	EnterBusStopNameActivity.this.startActivity(intent);
	            }
	        });
	 
	        // on pressing cancel button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            }
	        });
	 
	        // Showing Alert Message
	        alertDialog.show();
		}
		
	}
	
	public void submitDestination (View v) {
		String busStopName = busStopNameEditText.getText().toString();
		Log.d("translert", "Received bus stop name " + busStopName);
        startActivity (new Intent (this, ProgressActivity.class)
		.putExtras(getIntent())
		.putExtra("busDestination", busStopName));
	}
}
