package com.brianysu.powernap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.settings);
 
    }

	
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences prefs = android.preference.PreferenceManager.
				getDefaultSharedPreferences(getBaseContext());
		if (prefs.getBoolean(
				getResources().getString(R.string.prefKeyResetQuests), false)) {
			// apply reset, and then set the pref-value back to false
		}
	}

}
