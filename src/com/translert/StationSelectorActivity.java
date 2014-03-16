package com.translert;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class StationSelectorActivity extends SherlockActivity {

	ArrayAdapter<String> lineAdapter;
	ArrayList<ArrayAdapter<String>> stationAdapters = new ArrayList<ArrayAdapter<String>>();
	boolean isFrom = false;
	Station fromStation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selector);
		Intent hello = getIntent();
		isFrom = hello.getBooleanExtra("from", false);
		if(!isFrom){
			fromStation = Station.reverseLookup.get(hello.getStringExtra("fromName"));
			TextView titleLabel = (TextView) findViewById(R.id.activityLabel);
			titleLabel.setText("Where are you headed?");
			Button goButton = (Button) findViewById(R.id.next);
			goButton.setText("Go!");
		}
		Spinner options = (Spinner) findViewById(R.id.lineSpinner);
		lineAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Station.lines);
		options.setAdapter(lineAdapter);
		for(int i=0;i<Station.lines.size();i++){
			stationAdapters.add(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Station.names.get(i)));
		}
		options.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Spinner edit = (Spinner) findViewById(R.id.stationSpinner);
				edit.setAdapter(stationAdapters.get(arg2));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		options = (Spinner) findViewById(R.id.stationSpinner);
		options.setAdapter(stationAdapters.get(0));
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
