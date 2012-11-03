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
	
	public long getLatestEventTime(){
		if(contactEvents.size() == 0){
			return -1;
		}
		Collections.sort(contactEvents, new customComparator());
		return contactEvents.get(0).timestamp;
	}
	
	public class customComparator implements Comparator<ContactEvent>{
		@Override
	    public int compare(ContactEvent object1, ContactEvent object2) {
	        return (int) (object1.timestamp - object2.timestamp);
	    }
	}
	
	@Override
	public String toString() {
		return "Contact [name=" + name + ", starred=" + starred
				+ ", phonenumber=" + phonenumber + ", lastcontact="
				+ lastcontact + ", contactEvents=" + contactEvents + "]";
	}
}
