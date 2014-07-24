package com.translert;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.translert.R;
import com.translert.bus.EnterNumberActivity;
import com.translert.bus.utils.C;
//import com.translert.bus.utils.GPSTracker;
import com.translert.train.MainActivity;
import com.translert.train.WatchActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class BusTrainSelectorActivity extends SherlockActivity {
	

	
	private static TransDB transDb;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		C.headingFont = Typeface.createFromAsset(getAssets(), "fonts/Rex-Light.otf");
		C.bodyFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
		C.headingFont = Typeface.createFromAsset(getAssets(), "fonts/CODE-Light.otf");
		
		setContentView(R.layout.activity_bus_train);
		TextView title = (TextView)findViewById(R.id.select_mode);
		title.setTypeface(C.headingFont);
	}
	
	public void startMRTMode (View v) {
		Intent startMRTIntent = new Intent (this, MainActivity.class);
		startActivity (startMRTIntent);
		
	}
	
	public void startBusMode (View v) {
		Intent startBusIntent = new Intent (this, EnterNumberActivity.class);
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
	public void onResume(){
		super.onResume();
		//if true, then switch to specific context
		if(TransAppDB.checkAndViewAlert(this)){			
			return;
		}
		//others
	}
	
	
}