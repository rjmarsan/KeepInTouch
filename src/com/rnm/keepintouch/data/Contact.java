package com.rnm.keepintouch.data;

import java.util.ArrayList;
import java.util.List;

public class Contact {
	public String name;
	public boolean starred;
	public List<String> phonenumber = new ArrayList<String>();
	long lastcontact = Long.MAX_VALUE;
	public List<ContactEvent> contactEvents = new ArrayList<ContactEvent>();
	
	@Override
	public String toString() {
		return "Contact [name=" + name + ", starred=" + starred
				+ ", phonenumber=" + phonenumber + ", lastcontact="
				+ lastcontact + ", contactEvents=" + contactEvents + "]";
	}
}
