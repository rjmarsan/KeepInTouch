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
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.rnm.keepintouch.data.ContactEvent.TYPE;

public class ContactsData {

	List<Contact> contacts;
	
	public void update(Context context) {
		this.contacts = gatherData(context);
	}
	
	public List<Contact> gatherData(Context context) {
		List<Contact> contacts = getContacts(context);
		Map<String,List<Contact>> map = getNumberMapForContacts(contacts);
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
							contact.phonenumber.add(number);
						}
					}
					pCur.close();	
					
					contacts.add(contact); //only add if we have a phone number
				}
			}
		}
		
		return contacts;
	}
	
	
	private HashMap<String,List<Contact>> getNumberMapForContacts(List<Contact> contacts) {
		HashMap<String,List<Contact>> map = new HashMap<String,List<Contact>>();
		for (Contact contact : contacts) {
			for (String number : contact.phonenumber) {
				addToList(map, reduceNumber(number), contact);
			}
		}
		return map;
	}
	
	private void addToList(HashMap<String,List<Contact>> map, String entry, Contact contact) {
		if (map.containsKey(entry) == false) {
			map.put(entry, new ArrayList<Contact>());
		} 
		map.get(entry).add(contact);
	}
	
	private Contact getContactFromList(List<Contact> contacts, String number, Context context) {
		for (Contact c : contacts) {
			for (String testnum : c.phonenumber) {
				if (compareNumber(number, testnum, context))
					return c;
			}
		}
		return null;
	}
	
	
	private void updateCallLogIntoList(Context context, Map<String,List<Contact>> contacts) {
		/**
		 * http://malsandroid.blogspot.com/2010/06/accessing-call-logs.html
		 */
		Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = context.getContentResolver().query(allCalls, null, null, null, null);
        if (c.moveToFirst())
        {
           do{
               String num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
               
               if (contacts.containsKey(reduceNumber(num))) {
            	   Contact target = getContactFromList(contacts.get(reduceNumber(num)), num, context);
            	   if (target == null) continue;

	               long timestamp  = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE)));
	               int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));
	               int duration = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));

	               final int CALL_DURATION_THRESH = 1;
	               //Only log a call if it's not a missed call, and it's a certain duration.
	               if (duration >= CALL_DURATION_THRESH && type != CallLog.Calls.MISSED_TYPE) {
		               
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
               }
           } while (c.moveToNext());
        }	
	}
	
	private void updateSMSIntoList(Context context, Map<String, List<Contact>> contacts) {
		Uri uri = Uri.parse("content://sms");
		Cursor c = context.getContentResolver().query(uri, null, null, null,null);

		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Log.d("Contacts", "row: "+Arrays.toString(c.getColumnNames()));
				for (String s : c.getColumnNames()) Log.d("Contacts", "     "+s+": "+c.getString(c.getColumnIndex(s)));
				
				String body = c.getString(c.getColumnIndexOrThrow("body")).toString();
				long timestamp = Long.parseLong(c.getString(c.getColumnIndexOrThrow("date")).toString());
				int type = Integer.parseInt(c.getString(c.getColumnIndexOrThrow("type")).toString());
				String number = c.getString(c.getColumnIndexOrThrow("address")).toString();
				if (contacts.containsKey(reduceNumber(number))) {
					Contact target = getContactFromList(contacts.get(reduceNumber(number)), number, context);
					Log.d("Contacts", "looked for : "+reduceNumber(number)+ " and found "+target+" for "+number);
	            	if (target != null) {
		            	
						ContactEvent event = new ContactEvent();
						event.type = TYPE.SMS;
						event.timestamp = timestamp;
						event.message = body;
						event.number = number;
						event.callType = type;
						
						target.contactEvents.add(event);
						if (event.timestamp > target.lastcontact) {
							target.lastcontact = event.timestamp;
						}
	            	}
				}
				c.moveToNext();

			}
		}
		c.close();
	}

	
	private String reduceNumber(String number) {
		number = PhoneNumberUtils.formatNumber(number);
		String s = number.replaceAll("[^0-9]", "");
		if (s.length() > 5)
			return s.substring(s.length()-4);
		else
			return s;
	}
	private boolean compareNumber(String number1, String number2, Context context) {
		return PhoneNumberUtils.compare(context, number1, number2);
	}
}
