package com.rnm.keepintouch.data;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ContactPersist {
	public final static String PREFS_KEY = "widgets_prefs";
	
	public static void putContact(Context context, Contact contact, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, 0);
		Editor edit = prefs.edit();
		edit.putString(key, contact.jsonize());
		edit.apply();
	}
	
	public static Contact getContact(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, 0);
		if (prefs.contains(key)) {
			return Contact.fromjson(prefs.getString(key, ""));
		} 
		return null;
	}
	
	public static void clearAll(Context context, Set<String> ok) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, 0);
		Editor edit = prefs.edit();
		for (String key : prefs.getAll().keySet()) {
			if (ok.contains(key) == false) {
				Log.d("ContactsPersist", "!!!!!!!!!!!!!!!!!!! REMOVING !!!!!!!!!!!!!!!!!!!!!! "+key);
				edit.remove(key);
			}
		}
		edit.apply();
	}
}
