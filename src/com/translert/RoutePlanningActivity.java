package com.translert;
/*
import com.translert.bus.F;
import com.translert.bus.utils.BusRoute;
import com.translert.bus.utils.SGGPosition;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RoutePlanningActivity extends Activity {
	
	BusRoute busRoute;
	TextView showRoute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_planning);
		showRoute = (TextView) findViewById(R.id.show_route);
		
		new GetBusRouteTask().execute();
		
	}
	
	private class GetBusRouteTask extends AsyncTask<Void, Void, Void> {
		SGGPosition current;
		SGGPosition destination;
		@Override
		protected Void doInBackground(Void... params) {
			String destinationString = RoutePlanningActivity.this.getIntent().getStringExtra("destination");
			current=F.getGPS();
			destination=F.getPosition(destinationString);
			if (destination == null) {
				destination = F.getSdPosition(destinationString);
			}
			if (destination != null && current != null) {
				busRoute = F.getRoute(current, destination);
				Log.d("translert", busRoute.format());
			}
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (busRoute != null) {
				showRoute.setText(busRoute.format());
			} else {
				showRoute.setText("cannot find specified location");
			}
			
		}
		
	}

}
*/