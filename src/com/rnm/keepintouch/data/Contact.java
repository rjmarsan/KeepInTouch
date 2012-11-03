package com.rnm.keepintouch.data;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

public class Contact {
	public String name;
	public boolean starred;
	public List<String> phonenumber = new ArrayList<String>();
	long lastcontact = Long.MIN_VALUE;
	public List<ContactEvent> contactEvents = new ArrayList<ContactEvent>();
	public Uri uri;
	
	private static final int MILLIS_IN_DAY = 24*60*60*1000;
	
	public long getDayDifference(){
		if(lastcontact == Long.MIN_VALUE){
			return Long.MIN_VALUE;
		}
		long days = (System.currentTimeMillis() - lastcontact)/(MILLIS_IN_DAY);
		return days;
	}
	
	public ContactEvent getLatest() {
		if (contactEvents.isEmpty()) return null;
		ContactEvent mostrecent = contactEvents.get(0);
		for (ContactEvent event : contactEvents) {
			if (event.timestamp > mostrecent.timestamp) {
				mostrecent = event;
			}
		}
		return mostrecent;
	}
	
	@Override
	public String toString() {
		return "Contact [name=" + name + ", starred=" + starred
				+ ", phonenumber=" + phonenumber + ", lastcontact="
				+ lastcontact + ", contactEvents=" + contactEvents + "]";
	}
}
