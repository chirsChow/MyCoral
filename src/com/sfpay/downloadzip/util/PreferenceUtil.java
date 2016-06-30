package com.sfpay.downloadzip.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
	private final Context context;
	private final SharedPreferences sharedPreferences;

	public PreferenceUtil(Context context, SharedPreferences sharedPreferences) {
		this.context = context;
		this.sharedPreferences = sharedPreferences;
	}

	public String getString(int prefKeyRes, String defaultValue) {
		return sharedPreferences.getString(context.getString(prefKeyRes), defaultValue);
	}

	public void putString(int prefKeyRes, String value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(context.getString(prefKeyRes), value);
		editor.apply();
	}
	
	public boolean getBoolean(int prefKeyRes, boolean defaultValue) {
		return sharedPreferences.getBoolean(context.getString(prefKeyRes), defaultValue);
	}
	
	public void putBoolean(int prefKeyRes, boolean value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(context.getString(prefKeyRes), value);
		editor.apply();
	}

}
