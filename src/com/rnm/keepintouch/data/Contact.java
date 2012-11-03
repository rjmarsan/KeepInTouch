package com.rnm.keepintouch.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Contact {
	public String name;
	public boolean starred;
	public List<String> phonenumber = new ArrayList<String>();
	long lastcontact = Long.MIN_VALUE;
	public List<ContactEvent> contactEvents = new ArrayList<ContactEvent>();
	
	private static final int MILLIS_IN_DAY = 24*60*60*1000;
	
//	public long getLatestEventTime(){
//		if(contactEvents.size() == 0){
//			return -1;
//		}
//		Collections.sort(contactEvents, new customComparator());
//		return contactEvents.get(0).timestamp;
//	}
	
	public long getDayDifference(){
		if(lastcontact == Long.MIN_VALUE){
			return Long.MIN_VALUE;
		}
		long days = (System.currentTimeMillis() - lastcontact)/(MILLIS_IN_DAY);
		return days;
	}
	
	public class customComparator implements Comparator<ContactEvent>{
		@Override
	    public int compare(ContactEvent object1, ContactEvent object2) {
	        return (int) (object2.timestamp - object1.timestamp);
	    }
	}
	
	@Override
	public String toString() {
		return "Contact [name=" + name + ", starred=" + starred
				+ ", phonenumber=" + phonenumber + ", lastcontact="
				+ lastcontact + ", contactEvents=" + contactEvents + "]";
	}
}
