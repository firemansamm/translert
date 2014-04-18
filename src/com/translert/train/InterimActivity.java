package com.translert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InterimActivity extends Activity {
	
	public static Intent serviceIntent;
	static int legNum;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		
		PathFinder.State hello = PathFinder.answer;
		TextView tv = (TextView) findViewById(R.id.sourceLabel);
		legNum = getIntent().getExtras().getInt("leg");
		tv.setText("Change trains here, proceeding towards");
		tv = (TextView) findViewById(R.id.destLabel);
		String destName = hello.end.longName;
		if(legNum + 1 < hello.xfers.size()) destName = hello.xfers.get(legNum + 1).position.longName;
		tv.setText(destName + ".");
		tv = (TextView) findViewById(R.id.detailsLabel);
		int remTime = hello.totalTime;
		for(int i=0;i<legNum;i++){
			remTime -= hello.xfers.get(i).atTime;
		}
		tv.setText(String.valueOf(remTime) + " minutes" + ((hello.xfers.size() - legNum > 0) ? ", " + String.valueOf(hello.xfers.size() - legNum) + " transfer" + ((hello.xfers.size() - legNum > 1) ? "s" : "") + " remaining" : ""));
		tv = (TextView) findViewById(R.id.routeDescription);
		for(int i=legNum - 1;i<hello.xfers.size();i++){
			if(i == hello.xfers.size() - 1){
				tv.setText(tv.getText() + "\n" + hello.xfers.get(i).position.longName + " to " + hello.end.longName + " (" + String.valueOf(hello.totalTime - hello.xfers.get(i).atTime) + " mins)");
			}else{
				tv.setText(tv.getText() + "\n" + hello.xfers.get(i).position.longName + " to " + hello.xfers.get(i+1).position.longName + " (" + String.valueOf(hello.xfers.get(i+1).atTime - hello.xfers.get(i).atTime) + " mins)");
			}
		}
	}
	
	public void process(View v){
		PathFinder.State hello = PathFinder.answer;
		Bundle optionsBundle = new Bundle();
		if(hello.xfers.size() <= legNum) {
			optionsBundle.putString("destination", hello.end.longName);
			optionsBundle.putInt("minutes", hello.totalTime);
		}else {
			optionsBundle.putString("destination", hello.xfers.get(legNum).position.longName);
			optionsBundle.putInt("minutes", hello.xfers.get(legNum).atTime);
		}
		optionsBundle.putInt("legnum", legNum);
		optionsBundle.putInt("totalleg", hello.xfers.size());
		serviceIntent = new Intent(this, TimerService.class);
		serviceIntent.putExtras(optionsBundle);
		startService(serviceIntent);
		Intent watchIntent = new Intent(this, WatchActivity.class);
		startActivity(watchIntent);
		finish();
	}
}
