package com.rnm.keepintouch.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import com.rnm.keepintouch.data.ContactEvent.TYPE;

public class ContactsData {

	List<Contact> contacts;
	
	public void update(Context context) {
		this.contacts = gatherData(context);
	}
	
	public List<Contact> gatherData(Context context) {
		List<Contact> contacts = getContacts(context);
		Map<String,Contact> map = getNumberMapForContacts(contacts);
		updateCallLogIntoList(context, map);
		updateSMSIntoList(context, map);
		for (Contact contact : contacts) Log.d("Contact", "Contact: "+contact);
		return contacts;
	}
	
	
	private List<Contact> getContacts(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		/**
		 * http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
		 */
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME+" ASC");
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				Contact contact = new Contact();
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				contact.name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				contact.starred = Boolean.parseBoolean(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.STARRED)));
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

					while (pCur.moveToNext()) {
						String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						if (number != null) {
							contact.phonenumber.add(reduceNumber(number));
						}
					}
					pCur.close();	
					
					contacts.add(contact); //only add if we have a phone number
				}
			}
		}
		
		return contacts;
	}
	
	
	private HashMap<String,Contact> getNumberMapForContacts(List<Contact> contacts) {
		HashMap<String,Contact> map = new HashMap<String,Contact>();
		for (Contact contact : contacts) {
			for (String number : contact.phonenumber) {
				map.put(number, contact);
			}
		}
		return map;
	}
	
	
	private void updateCallLogIntoList(Context context, Map<String,Contact> contacts) {
		/**
		 * http://malsandroid.blogspot.com/2010/06/accessing-call-logs.html
		 */
		Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = context.getContentResolver().query(allCalls, null, null, null, null);
        if (c.moveToFirst())
        {
           do{
               String num = reduceNumber(c.getString(c.getColumnIndex(CallLog.Calls.NUMBER)));
               
               if (contacts.containsKey(num)) {
            	   Contact target = contacts.get(num);

	               long timestamp  = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE)));
	               int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));

	               ContactEvent event = new ContactEvent();
	               event.type = TYPE.CALL;
	               event.timestamp = timestamp;
	               event.callType = type;
	               
	               target.contactEvents.add(event);
	               if (event.timestamp < target.lastcontact) {
	            	   target.lastcontact = event.timestamp;
	               }
               }
           } while (c.moveToNext());
        }	
	}
	
	private void updateSMSIntoList(Context context, Map<String, Contact> contacts) {
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor c = context.getContentResolver().query(uri, null, null, null,null);

		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Log.d("Contacts", "row: "+Arrays.toString(c.getColumnNames()));
				String body = c.getString(c.getColumnIndexOrThrow("body")).toString();
				long timestamp = Long.parseLong(c.getString(c.getColumnIndexOrThrow("date")).toString());
				String number = reduceNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
				if (contacts.containsKey(number)) {
					Contact target = contacts.get(number);
					ContactEvent event = new ContactEvent();
					event.type = TYPE.SMS;
					event.timestamp = timestamp;
					event.message = body;
					
					target.contactEvents.add(event);
					if (event.timestamp < target.lastcontact) {
						target.lastcontact = event.timestamp;
					}
				}
				c.moveToNext();

			}
		}
		c.close();
	}

	
	private String reduceNumber(String number) {
		return number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");
	}
}
