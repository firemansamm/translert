package com.translert.train;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.translert.R;
import com.translert.train.utils.PathFinder;
import com.translert.train.utils.Preferencer;
import com.translert.train.utils.RecentTripsAdapter;
import com.translert.train.utils.Station;
import com.translert.train.utils.Trip;

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
					in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					in.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); //to let system direct to 'Train Main activity'
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
					in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					in.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); //to let system direct to 'Train Main activity'
					startActivity(in);
					//Intent in = new Intent(MainActivity.this, TransService.class);			
					//startService(in);
				}
			}
		});
	}
}
