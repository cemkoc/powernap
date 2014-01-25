package com.brianysu.powernap;

import android.support.v4.app.Fragment;

public class AlarmActivity extends SingleFragmentActivity {
	
	@Override
	public Fragment createFragment() {
		return AlarmFragment.newInstance();
	}
}
