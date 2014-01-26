package com.brianysu.powernap;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class OptionDialogPreference extends DialogPreference {
	
	public OptionDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        persistBoolean(positiveResult);
        
        
    }
}
