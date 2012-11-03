package com.rnm.keepintouch.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
		return contacts;
	}
	
	
	public List<Contact> getFavoriteContacts() {
		List<Contact> favorites = new ArrayList<Contact>();
		for (Contact c : contacts) if (c.starred) favorites.add(c);
		Collections.sort(favorites, new Comparator<Contact>() {

			@Override
			public int compare(Contact lhs, Contact rhs) {
				if (lhs.lastcontact < rhs.lastcontact)
					return -1;
				else if (lhs.lastcontact > rhs.lastcontact)
					return 1;
				else
					return 0;
			}
		});
		for (Contact favorite : favorites) Log.d("Contact", "Favorite: "+favorite);

		return favorites;
	}
	
	public List<Contact> getAlphabeticalContacts() {
		for (Contact contact : contacts) Log.d("Contact", "Contact: "+contact);
		return new ArrayList<Contact>(contacts);
	}
	public List<Contact> getMostRecentContacts() {
		ArrayList<Contact> sorted = new ArrayList<Contact>(contacts);
		Collections.sort(sorted, new Comparator<Contact>() {

			@Override
			public int compare(Contact lhs, Contact rhs) {
				if (lhs.lastcontact < rhs.lastcontact)
					return 1;
				else if (lhs.lastcontact > rhs.lastcontact)
					return -1;
				else
					return 0;
			}
		});
		for (Contact contact : sorted) Log.d("Contact", "MostRecent: "+contact);
		return sorted;
	}
	
	

	
	
	
	private List<Contact> getContacts(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		/**
		 * http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
		 */
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.STARRED+" is 1", null, ContactsContract.Contacts.DISPLAY_NAME+" ASC");
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				Contact contact = new Contact();
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				contact.name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				contact.starred = Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.STARRED))) != 0;
				contact.uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
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
	               event.number = num;
	               
	               target.contactEvents.add(event);
	               if (event.timestamp > target.lastcontact) {
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
					event.number = number;
					
					target.contactEvents.add(event);
					if (event.timestamp > target.lastcontact) {
						target.lastcontact = event.timestamp;
					}
				}
				c.moveToNext();

			}
		}
		c.close();
	}

	
	private String reduceNumber(String number) {
		String s = number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").replace("+", "");
		if (s.startsWith("1")) s=s.replaceFirst("1", "");
		return s;
	}
}
