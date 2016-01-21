package com.lingci.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	
	private static PreferencesManager instance;
	public static final String NAME = "config";
	private static SharedPreferences sp;
	private static Editor editor;

	private PreferencesManager() {
	}

	public static PreferencesManager getInstance() {
		if (instance == null) {
			instance = new PreferencesManager();
			sp = GlobalParame.main.getSharedPreferences(NAME, Context.MODE_PRIVATE);
			editor = sp.edit();
		}
		return instance;
	}

	public boolean getBoolean(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}

	public void putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	public void putInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}
	
	public String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	public void putString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}
}
