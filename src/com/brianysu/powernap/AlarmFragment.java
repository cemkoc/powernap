package com.brianysu.powernap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmFragment extends Fragment implements SensorEventListener {

	private static final String TAG = "alarmfragment";
	private static final int REQUEST_DATE = 0;
	private static final String DIALOG_DATE = "date";
	
	private TextView mTimer, mMustWake;
	private Button mAlarmButton;
	public static final int REQUEST_CODE = 0;
	private View v;
	private Date mDate;
	
	private float mLastX, mLastY, mLastZ;
	public static float mThresX = 4, mThresY = 4, mThresZ = 10;
	private long mStartTime;
	
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 0;
	private boolean mMustWakeEnabled = false;
	
	private String mColors;
	private int mAlarmDuration = 20;
	private int mSleep = 5;
	
	private int mSongId = R.raw.uplift;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInitialized = false;
	}
	
	
	
	private void setBg() {
		if (mColors != null) {
			String[] colors = mColors.split("\\s+");
			v.setBackgroundColor(Color.parseColor(colors[0]));
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_alarm, parent, false);
		setBg();
		resetButton();
		mDate = new Date();
		mTimer = (TextView) v.findViewById(R.id.alarm_time);
		mTimer.setText("0:00");
		mMustWake = (TextView) v.findViewById(R.id.must_wake_text);
		mMustWake.setTextColor(Color.GRAY);
		mMustWake.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				mMustWakeEnabled = !mMustWakeEnabled;
				if (!mMustWakeEnabled) {
					mMustWake.setTextColor(Color.GRAY);
				} else {
					mMustWake.setTextColor(Color.WHITE);
				}
				return true;
			}
		});
		
		mMustWake.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(mDate);
				dialog.setTargetFragment(AlarmFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		return v;
	}
	
	public void initializeAccelerometer() {
		
		reset();
		
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		CharSequence c = String.format("After %d minutes of inactivity, the alarm will begin.", mSleep);
		Toast.makeText(getActivity(), c, Toast.LENGTH_LONG).show();
	}
	
	private void setAlarm(int numMin) {
		
		mustWake();
		numMin = 1;
		final Duration alarm = new Duration(5 * 1000 * numMin, 1000, mTimer, getActivity(), this);
		alarm.start();
		mAlarmButton.setText(R.string.cancel_text);
		mAlarmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				alarm.end();
				Toast.makeText(getActivity(), R.string.alarm_canceled, Toast.LENGTH_LONG).show();
				resetButton();
			}
			
		});
		
        Toast.makeText(getActivity(), R.string.alarm_text, Toast.LENGTH_SHORT).show();
	}
	
	public void resetButton() {
		mAlarmButton = (Button) v.findViewById(R.id.set_alarm_button);
		if (mColors != null) {
			String[] colors = mColors.split("\\s+");
			mAlarmButton.setBackgroundColor(Color.parseColor(colors[1]));
		}
		mAlarmButton.setText(R.string.set_alarm_button);
		mAlarmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initializeAccelerometer();
				Log.d(TAG, "Accelerometer initialized");
			}
			
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        
        mColors = sharedPrefs.getString("prefColors", "blue");
        resetButton();
        try {
        	mAlarmDuration = Integer.parseInt(sharedPrefs.getString("prefTimer", "20"));
        	Log.d(TAG, "duration: " + mAlarmDuration);
        	mAlarmButton.setText(String.format("Alarm Set for %d minutes.", mAlarmDuration));
        	mTimer.setText(String.format("%02d:00", mAlarmDuration));
        } catch (NumberFormatException e) {
        	
        }
        
        try {
        	mSleep = Integer.parseInt(sharedPrefs.getString("prefSleep", "5"));
        	Toast.makeText(getActivity(), String.format("Sleep time is %d minutes.", mSleep), Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
        	
        }
        setBg();
		Log.d(TAG, "Fragment got colors: " + mColors);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
	
	public static void calibrate() {
		AlarmFragment f = new AlarmFragment();
		mThresX = mThresY = mThresZ = 0;
		long startTime = System.currentTimeMillis();
		long runningTime = 0;
		ArrayList<Float> x = new ArrayList<Float>();
		ArrayList<Float> y = new ArrayList<Float>();
		ArrayList<Float> z = new ArrayList<Float>();
		f.initializeAccelerometer();
		while (runningTime - startTime < 5000) {
			runningTime = System.currentTimeMillis();
			x.add(f.getLastX());
			y.add(f.getLastY());
			z.add(f.getLastZ());
		}
		f.stopAccelerometer();
		for (int i = 0; i < x.size(); i++) {
			mThresX += x.get(i);
			mThresY += y.get(i);
			mThresZ += z.get(i);
		}
		mThresX = mThresX / x.size() + 1;
		mThresY = mThresY / y.size() + 1;
		mThresZ = mThresZ / z.size() + 2;
		Log.d(TAG, "User is still awake " + mThresX + " " + mThresY + " " + mThresZ);
		Toast.makeText(f.getActivity(), "New Thresholds " + mThresX + " " + mThresY + " " + mThresZ, 5000);
	}
	
	public float getLastX() {
		return mLastX;
	}



	public float getLastY() {
		return mLastY;
	}



	public float getLastZ() {
		return mLastZ;
	}



	private boolean checkAwake() {
		if (mustWake()) {
			stopAccelerometer();
		}
		if (mLastX > mThresX || mLastY > mThresY || mLastZ > mThresZ) {
			//Log.d(TAG, "User is still awake " + mLastX + " " + mLastY + " " + mLastZ);
			reset();
		} else {
			long currentTime = System.currentTimeMillis();
			if (currentTime - mStartTime > 1000 * mSleep) {
				Log.d(TAG, "User is asleep");
				stopAccelerometer();
				Log.d(TAG, "Accelerometer has stopped");
				setAlarm(mAlarmDuration);
				Log.d(TAG, "Alarm has started");
				reset();
				return true;
			}
		}
		return false;
	}
	
	private boolean mustWake() {
		if (mMustWakeEnabled) {
			Log.d(TAG, "Enabled: " + mMustWakeEnabled);
			Calendar cal = Calendar.getInstance();
			Date currDate = cal.getTime();
			if (currDate.after(mDate)) {
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(2000);
				NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification();        
				notification.sound = Uri.parse("android.resource://com.brianysu.powernap/raw/" + mSongId);
				nm.notify(0, notification);
				Toast.makeText(getActivity(), R.string.alarm_canceled2, Toast.LENGTH_LONG).show();
			}
			return true;
		}
		return false;
	}
	
	private void reset() {
		mStartTime = System.currentTimeMillis();
	}
	
	public void stopAccelerometer() {
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) return;
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE);
			mDate = date;
			updateDate();
			
		}
	}
	
	private void updateDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		mMustWake.setText(String.format("Must Wake Up By: %02d:%02d", hour, min));
	}

	public static Fragment newInstance(String colors) {
		Bundle args = new Bundle();
		args.putString("Color", colors);
		AlarmFragment fragment = new AlarmFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	
}
