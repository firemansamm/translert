package com.translert;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TripOverviewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		PathFinder.State hello = PathFinder.answer;
		TextView tv = (TextView) findViewById(R.id.sourceLabel);
		tv.setText(hello.start.longName + " to");
		tv = (TextView) findViewById(R.id.destLabel);
		tv.setText(hello.end.longName);
		tv = (TextView) findViewById(R.id.detailsLabel);
		tv.setText(String.valueOf(hello.totalTime) + " minutes" + ((hello.xfers.size() > 0) ? ", " + String.valueOf(hello.xfers.size()) + " transfer" + ((hello.xfers.size() > 1) ? "s" : "") : ""));
		
	}
	
}
