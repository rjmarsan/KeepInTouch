package com.rnm.keepintouch;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactEvent;
import com.rnm.keepintouch.data.ContactEvent.TYPE;

public class ContactsAdapter implements ListAdapter{
	
	private List<Contact> contacts;
	private Context context;
	
	public ContactsAdapter(Context context){
		this.contacts = null;
		this.context = context;
	}
	
	public ContactsAdapter(List<Contact> contacts, Context context){
		this.contacts = contacts;
		this.context = context;
	}

	@Override
	public int getCount() {
		if(contacts!= null){
			return contacts.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(contacts!= null){
			return contacts.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if(contacts!= null){
			return contacts.get(position).hashCode();
		}
		return -1;
	}

	@Override
	public int getItemViewType(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		final Contact contact = (Contact) getItem(position);
		if(view == null){
			LayoutInflater inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			view = inf.inflate(R.layout.contact_list_item, null);
		}
		
		
		QuickContactBadge badge = (QuickContactBadge)view.findViewById(R.id.contacted_badge);
		badge.assignContactFromPhone(contact.phonenumber.get(0), false);
        badge.setMode(ContactsContract.QuickContact.MODE_LARGE);
        
		ContactEvent mostrecent = contact.getLatest();
		
		TextView method = (TextView)view.findViewById(R.id.contacted_method);		
		TextView contacted = (TextView)view.findViewById(R.id.contacted_time);
		
		
		if (mostrecent != null) {
			method.setText(mostrecent.type == TYPE.CALL ? "phone call" : "text message");
			if(mostrecent.timestamp == Long.MIN_VALUE){
				contacted.setText("never");
			}else{
				contacted.setText(DateUtils.formatDateRange(context, mostrecent.timestamp, System.currentTimeMillis(), DateUtils.LENGTH_SHORT));
			}
		} else {
			method.setText("none");
			contacted.setText("never");
		}
		
		
		return view;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled(int arg0) {
		// TODO Auto-generated method stub
		return true;
	}
	

}
