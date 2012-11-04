package com.rnm.keepintouch;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactEvent;
import com.rnm.keepintouch.data.ContactEvent.TYPE;

public class FavoritesFragment extends Fragment implements OnItemClickListener {
	
	ContactsAdapter contactsAdapter;
	List<Contact> contactlist;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.favorites_list, null);
		ListView list = (ListView)v.findViewById(R.id.list);
		list.setDividerHeight(0);
		//list.setDivider(getResources().getDrawable(android.R.drawable.menu_frame));
		contactsAdapter = new ContactsAdapter(getActivity(), R.layout.contact_list_item, ((MainActivity)getActivity()).fav);
		list.setAdapter(contactsAdapter);
		list.setOnItemClickListener(this);
		list.setEmptyView(v.findViewById(R.id.empty));
		return v;
	}
	
	public void refresh(List<Contact> list) {
		if (list != contactlist) {
			contactlist = list;
			contactsAdapter.clear();
			contactsAdapter.addAll(list);
		}
		contactsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Contact contact = contactsAdapter.getItem(arg2);
		ContactEvent latest = contact.getLatest();
		if (latest == null || latest.type == TYPE.SMS) {
			sendSms(contact, latest);
		} else {
			sendCall(contact, latest);
		}
	}
	
	private void sendSms(Contact contact, ContactEvent event) {
		String number = (event != null) ? event.number : contact.phonenumber.get(0); 
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number)));
	}
	
	private void sendCall(Contact contact, ContactEvent event) {
		String number = (event != null) ? event.number : contact.phonenumber.get(0); 
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + number)));
	}
}
