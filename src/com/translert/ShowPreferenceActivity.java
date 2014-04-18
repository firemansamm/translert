package com.translert;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ShowPreferenceActivity extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
