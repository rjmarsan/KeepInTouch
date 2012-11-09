package com.rnm.keepintouch;

import java.util.List;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactEvent;
import com.rnm.keepintouch.data.ContactEvent.TYPE;

public class FavoritesFragment extends Fragment implements OnItemClickListener {
	
	ContactsAdapter contactsAdapter;
	List<Contact> contactlist;
	TextView error;
	
	ProgressBar empty;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		final int memClass = ((ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		final int cacheSize = 1024 * 1024 * memClass / 8;

		
		View v = inflater.inflate(R.layout.favorites_list, null);
		error = (TextView)v.findViewById(R.id.no_fav);
		empty = (ProgressBar)v.findViewById(R.id.empty);
		ListView listView = (ListView)v.findViewById(R.id.list);
		listView.setDividerHeight(0);
		//list.setDivider(getResources().getDrawable(android.R.drawable.menu_frame));
		contactsAdapter = new ContactsAdapter(getActivity(), new Cache(cacheSize), R.layout.contact_list_item, ((MainActivity)getActivity()).fav);
		listView.setAdapter(contactsAdapter);
		listView.setOnItemClickListener(this);
		listView.setEmptyView(empty);
		return v;
	}
	
	public void refresh(List<Contact> list) {
		if(list.size() == 0){
			error.setVisibility(View.VISIBLE);
			empty.setVisibility(View.INVISIBLE);
		}else if (list != contactlist) {
			error.setVisibility(View.INVISIBLE);
			contactlist = list;
			contactsAdapter.clear();
			contactsAdapter.addAll(list);
			contactsAdapter.notifyDataSetChanged();
		}else{
			error.setVisibility(View.INVISIBLE);
			contactsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Contact contact = contactsAdapter.getItem(position);
		ContactEvent latest = contact.getLatest();
		if (latest == null || latest.type == TYPE.SMS) {
			Utils.sendSms(contact, latest, getActivity());
		} else {
			Utils.sendCall(contact, latest, getActivity());
		}
	}
	
	
}
