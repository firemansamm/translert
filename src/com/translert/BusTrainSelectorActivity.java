package com.translert;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.translert.R;
import com.translert.bus.BusEnterNumberActivity;
import com.translert.bus.utils.C;
import com.translert.bus.utils.GPSTracker;
import com.translert.train.MainActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class BusTrainSelectorActivity extends SherlockActivity {
	
	boolean listenerFlag;
	static public GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		C.headingFont = Typeface.createFromAsset(getAssets(), "fonts/Rex-Light.otf");
		C.bodyFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
		C.headingFont = Typeface.createFromAsset(getAssets(), "fonts/CODE-Light.otf");
		
		setContentView(R.layout.activity_bus_train);
		TextView title = (TextView)findViewById(R.id.select_mode);
		title.setTypeface(C.headingFont);
		
		gps = new GPSTracker(this);
		if (!gps.canGetGPS()) {
			gps.showSettingsAlert();
		}
		 
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
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra("restart")) {
			try {
				com.translert.bus.BusProgressActivity.ringtone.stop();
				com.translert.bus.BusProgressActivity.vibrator.cancel();
			} catch (Exception e) {}
			try {
				com.translert.train.WatchActivity.ringtone.stop();
				com.translert.train.WatchActivity.vibrator.cancel();
			} catch (Exception e) {}
		}
	}

}
