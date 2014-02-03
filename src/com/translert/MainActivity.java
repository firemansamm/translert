package com.translert;

import com.actionbarsherlock.app.SherlockActivity;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//test
		PathFinder pf = new PathFinder(this);
		pf.routeMe(Station.reverseLookup.get("Kent Ridge"), Station.shortLookup.get("TNM"), 0);
		pf.routeMe(Station.reverseLookup.get("Kent Ridge"), Station.shortLookup.get("TNM"), 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
