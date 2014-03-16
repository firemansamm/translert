package com.translert;

import com.translert.TimerService.Worker;

import android.app.Activity;
import android.os.Bundle;

public class WatchActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setContentView(something);
		TimerService.tickHandlers.add(new Worker(){
			@Override
			public void work(){
				//update the display here
			}
		});
		
		TimerService.endHandlers.add(new Worker(){
			@Override
			public void work(){
				//alarm here
			}
		});
	}
}
