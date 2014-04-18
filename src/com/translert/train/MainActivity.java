package com.translert;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockActivity {
	
	public static PathFinder pf;
	public static Preferencer pref;
	public static ListView lv;
	public static RecentTripsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//initialize PathFinder and preferences storage - we do this here because this only needs to be done ONCE when the app starts up
		//these instances are static
		pf = new PathFinder(this);
		pref = new Preferencer(this);
		
		//we create the adapter - this populates the list with trips.
		lv = (ListView) findViewById(R.id.recenttrips);
		mAdapter = new RecentTripsAdapter(this);
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					//new trip
					Intent in = new Intent(MainActivity.this, StationSelectorActivity.class);
					in.putExtra("from", true);
					startActivity(in);
				}else{
					//load trip
					Trip x = pref.recent.get(position - 1);
					PathFinder.State e = pf.routeMe(Station.reverseLookup.get(x.source), 
										Station.reverseLookup.get(x.destination), 
										x.type);
					//now we need to display this state's overview and start
					PathFinder.answer = e;
					Intent in = new Intent(MainActivity.this, TripOverviewActivity.class);
					startActivity(in);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
