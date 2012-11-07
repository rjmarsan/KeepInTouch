package com.rnm.keepintouch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactEvent;

public class Utils {
	public static void sendSms(Contact contact, ContactEvent event, Context context) {
		String number = (event != null) ? event.number : contact.phonenumber.get(0); 
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number)));
	}
	
	public static void sendCall(Contact contact, ContactEvent event, Context context) {
		String number = (event != null) ? event.number : contact.phonenumber.get(0); 
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + number)));
	}
}
