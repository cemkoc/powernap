package com.brianysu.powernap;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmFragment extends Fragment implements SensorEventListener {

	private static final String TAG = "alarmfragment";
	
	private Button mAlarmButton;
	private PendingIntent mAlarmIntent;
	public static final int REQUEST_CODE = 0;
	
	private float mLastX, mLastY, mLastZ;
	private long mStartTime;
	
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInitialized = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_alarm, parent, false);
		
		mAlarmButton = (Button) v.findViewById(R.id.set_alarm_button);
		mAlarmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initializeAccelerometer();
				Log.d(TAG, "Accelerometer initialized");
			}
			
		});
		return v;
	}
	
	private void initializeAccelerometer() {
		
		reset();
		
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		Toast.makeText(getActivity(), R.string.quick_inst, Toast.LENGTH_LONG).show();
	}
	
	private void setAlarm(int numMin) {
		
		Intent i = new Intent(getActivity(), AlarmBroadcastReceiver.class);
		mAlarmIntent = PendingIntent.getBroadcast(getActivity(), REQUEST_CODE,
                i, 0);
		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        final int ONE_SEC_MILLIS = 1000;
        
        AlarmManager alarmManager = (AlarmManager)
                getActivity().getSystemService(getActivity().ALARM_SERVICE);
        
        alarmManager.set(alarmType, SystemClock.elapsedRealtime() + ONE_SEC_MILLIS * 5, mAlarmIntent);
        Toast.makeText(getActivity(), R.string.alarm_text, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mStartTime = System.currentTimeMillis();
			mInitialized = true;
		} else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			checkAwake();
		}
	}
	
	private boolean checkAwake() {
		if (mLastX > 4 || mLastY > 4 || mLastZ > 10) {
			ArrayList<Float> list = new ArrayList<Float>();
			list.add(mLastX);
			list.add(mLastY);
			list.add(mLastZ);
			float avg, total = 0;
			for (int i = 0; i < list.size(); i++) {
				total += i;
			}
			avg = total / list.size();
			Log.d(TAG, "User is still awake " + mLastX + " " + mLastY + " " + mLastZ);
			reset();
		} else {
			long currentTime = System.currentTimeMillis();
			if (currentTime - mStartTime > 5000) {
				Log.d(TAG, "User is asleep");
				stopAccelerometer();
				Log.d(TAG, "Accelerometer has stopped");
				setAlarm(1);
				Log.d(TAG, "Alarm has started");
				reset();
				return true;
			}
		}
		return false;
	}
	
	private void reset() {
		mStartTime = System.currentTimeMillis();
	}
	
	private void stopAccelerometer() {
		mSensorManager.unregisterListener(this);
	}
	
	
	
	
	
	
	
	
	public static Fragment newInstance() {
		Bundle args = new Bundle();
		AlarmFragment fragment = new AlarmFragment();
		fragment.setArguments(args);
		return fragment;
	}
}
