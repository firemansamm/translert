package com.translert;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.translert.R;
import com.translert.bus.BusEnterNumberActivity;
import com.translert.train.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

public class BusTrainSelectorActivity extends SherlockActivity {
	
	public Context context = this;

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
	

}
