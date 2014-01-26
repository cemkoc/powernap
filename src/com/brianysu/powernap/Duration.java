package com.brianysu.powernap;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

public class Duration extends CountDownTimer {

	private long pauseTime;
    private TextView countView;
    private Context context;
    private AlarmFragment mAf;

 
    /**
     * 
     * @see android.os.CountDownTimer;
     * @param millisInFuture
     * @param countDownInterval
     */
    public Duration(long millisInFuture, long countDownInterval, TextView view,
            Context context, AlarmFragment af) {
        super(millisInFuture, countDownInterval);
        pauseTime = millisInFuture;
        countView = view;
        this.context = context;
        mAf = af;
 
    }
 
    @Override
    public void onFinish() {
        countView.setText("0:00");
        mAf.resetButton();
		Toast.makeText(context, R.string.alarm, Toast.LENGTH_LONG).show();
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	    vibrator.vibrate(2000);
	    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    Notification notification = new Notification();        
	    notification.sound = Uri.parse("android.resource://com.brianysu.powernap/raw/" + R.raw.uplift);
	    nm.notify(0, notification);
    }
	
    /**
     * I've added a little twist to the onThick() method, as it will call a
     * onNotify() method who notify the user that a certain time has passed
     * since the timer started
     * 
     * @see android.os.CountDownTimer.onTick;
     * @param millisUntilFinished
     *            milliseconds until finished
     */
    @Override
    public void onTick(long millisUntilFinished) {
        pauseTime = millisUntilFinished;
        
    	
    	
 
        if ((millisUntilFinished / 1000 % 60) < 10)
            countView.setText(millisUntilFinished / 60000 + ":0"
                    + (millisUntilFinished / 1000) % 60);
        else
            countView.setText(millisUntilFinished / 60000 + ":"
                    + (millisUntilFinished / 1000) % 60);
    }
 
    /**
     * Just cancel the ongoing Timer. Need to call onResume() to resume
     */
    public void onPause() {
        end();
    }
    
    public void end() {
    	super.cancel();
    	countView.setText("0:00");
    }
 
    /**
     * Creates a new Duration object with the remaining time
     * 
     * @return new Duration object
     */
    public Duration onResume() {
        Duration d = new Duration(pauseTime, 1000, countView, context, mAf);
        return d;
    }
 
    /**
     * @return current time in milliseconds
     */
    public long getTime() {
        return pauseTime;
    }
}
