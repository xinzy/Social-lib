package com.gaodun.social;

import android.util.Log;

public class L {
	
	public static final String TAG = "l"; 

	static final void e(String msg) {
		Log.e(TAG, msg);
	}
	
	static final void e(String msg, Throwable t) {
		Log.e(TAG, msg, t);
	}
}
