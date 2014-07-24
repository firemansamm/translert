package com.translert.bus;

import com.translert.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class EnterNumberActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_enter_number);
	}
	
	public void submitBusNumber (View v) {
		EditText tv = (EditText) findViewById(R.id.busNoTextBox);
		String busNumber = tv.getText().toString();
		Log.d("translert", "Received bus number " + busNumber);
        Intent i = new Intent (EnterNumberActivity.this, EnterBusStopNameActivity.class);
        i.putExtra("busNumber", busNumber);
        startActivity(i);
	}
}