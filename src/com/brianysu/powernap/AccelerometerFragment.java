package com.brianysu.powernap;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccelerometerFragment extends Fragment implements SensorEventListener {

	private static final String TAG = "accelerometer";
	private float mLastX, mLastY, mLastZ;
	private long mStartTime;
	private View mView;
	
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 0;
	
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInitialized = false;
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		getActivity().setTitle(R.string.accelerometer_title);
		mView = inflater.inflate(R.layout.fragment_accelerometer, parent, false);
		return mView;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		TextView tvX= (TextView) mView.findViewById(R.id.x_axis_textview);
		TextView tvY= (TextView) mView.findViewById(R.id.y_axis_textview);
		TextView tvZ= (TextView) mView.findViewById(R.id.z_axis_textview);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mStartTime = System.currentTimeMillis();
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
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
			tvX.setText(Float.toString(deltaX));
			tvY.setText(Float.toString(deltaY));
			tvZ.setText(Float.toString(deltaZ));
			checkAwake();
		}
	}
	
	private boolean checkAwake() {
		if (mLastX > 10 || mLastY > 10 || mLastZ > 10) {
			ArrayList<Float> list = new ArrayList<Float>();
			list.add(mLastX);
			list.add(mLastY);
			list.add(mLastZ);
			float avg, total = 0;
			for (int i = 0; i < list.size(); i++) {
				total += i;
			}
			avg = total / list.size();
			Log.d(TAG, "User is still awake " + avg);
			reset();
		} else {
			long currentTime = System.currentTimeMillis();
			if (currentTime - mStartTime > 5000) {
				Log.d(TAG, "User is asleep");
				return true;
			}
		}
		return false;
	}
	
	private void reset() {
		mStartTime = System.currentTimeMillis();
	}
	
	public static Fragment newInstance() {
		Bundle args = new Bundle();
		AccelerometerFragment fragment = new AccelerometerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	
}
