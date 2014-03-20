package com.translert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class StationSelectorActivity extends SherlockActivity {

	ArrayAdapter<String> lineAdapter;
	static final ArrayList<String> enabledLines = new ArrayList<String>(Arrays.asList(new String[] { "East West Line", "North South Line", "North East Line", "Circle Line", "Changi Airport Branch Line"}));
	Map<String, ArrayAdapter<String>> stationAdapters = new HashMap<String, ArrayAdapter<String>>();
	Map<String, String> nameMap = new HashMap<String, String>();
	boolean isFrom = false;
	Station fromStation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selector);
		Intent hello = getIntent();
		nameMap.put("East West Line", "EW");
		nameMap.put("North South Line", "NS");
		nameMap.put("North East Line", "NE");
		nameMap.put("Circle Line", "CC");
		nameMap.put("Changi Airport Branch Line", "CG");
		isFrom = hello.getBooleanExtra("from", false);
		if(!isFrom){
			fromStation = Station.reverseLookup.get(hello.getStringExtra("fromName"));
			TextView titleLabel = (TextView) findViewById(R.id.activityLabel);
			titleLabel.setText("Where are you headed?");
			Button goButton = (Button) findViewById(R.id.next);
			goButton.setText("Go!");
		}
		Spinner options = (Spinner) findViewById(R.id.lineSpinner);
		lineAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, enabledLines);
		options.setAdapter(lineAdapter);
		for(int i=0;i<Station.lines.size();i++){
			stationAdapters.put(Station.lines.get(i).trim(), new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Station.names.get(i)));
			Log.d("translert", "added " + Station.lines.get(i));
		}
		options.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Spinner edit = (Spinner) findViewById(R.id.stationSpinner);
				String lineName = nameMap.get(enabledLines.get(arg2));
				edit.setAdapter(stationAdapters.get(lineName));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		options = (Spinner) findViewById(R.id.stationSpinner);
		options.setAdapter(stationAdapters.get(nameMap.get(enabledLines.get(0))));
	}
	
	public void process(View v){
		Spinner selector = (Spinner) findViewById(R.id.stationSpinner);
		if(isFrom){
			Intent in = new Intent(this, StationSelectorActivity.class);
			in.putExtra("isFrom", false);
			in.putExtra("fromName", selector.getSelectedItem().toString());
			startActivity(in);
		}else{
			Station endStation = Station.reverseLookup.get(selector.getSelectedItem().toString());
			PathFinder.State answerState = MainActivity.pf.routeMe(fromStation, endStation, 0);
			PathFinder.answer = answerState;
			Intent in = new Intent(this, TripOverviewActivity.class);
			startActivity(in);
		}
	}
	
	
}
