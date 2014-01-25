package com.brianysu.powernap;

import android.support.v4.app.Fragment;

public class AccelerometerActivity extends SingleFragmentActivity {

	public Fragment createFragment() {
		return AccelerometerFragment.newInstance();
	}

}
