package com.rnm.keepintouch;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Preferences extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}
}
