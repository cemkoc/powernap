package com.brianysu.powernap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class AlarmFragment extends Fragment {

	private Button mAlarmButton, mAccelButton;
	private PendingIntent mAlarmIntent;
	public static final int REQUEST_CODE = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_alarm, parent, false);
		
		mAlarmButton = (Button) v.findViewById(R.id.set_alarm_button);
		mAlarmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setAlarm();
			}
			
		});
		mAccelButton = (Button) v.findViewById(R.id.accelerometer_button);
		mAccelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), AccelerometerActivity.class);
				startActivity(i);
			}
		});
		return v;
	}
	
	private void setAlarm() {
		Intent i = new Intent(getActivity(), AlarmBroadcastReceiver.class);
		mAlarmIntent = PendingIntent.getBroadcast(getActivity(), REQUEST_CODE,
                i, 0);
		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        final int FIVE_SEC_MILLIS = 5000;
        
        AlarmManager alarmManager = (AlarmManager)
                getActivity().getSystemService(getActivity().ALARM_SERVICE);
        
        alarmManager.set(alarmType, SystemClock.elapsedRealtime() + FIVE_SEC_MILLIS, mAlarmIntent);
        Toast.makeText(getActivity(), R.string.alarm_text, Toast.LENGTH_SHORT).show();
	}
	
	
	
	
	
	
	
	
	
	
	
	public static Fragment newInstance() {
		Bundle args = new Bundle();
		AlarmFragment fragment = new AlarmFragment();
		fragment.setArguments(args);
		return fragment;
	}
}
