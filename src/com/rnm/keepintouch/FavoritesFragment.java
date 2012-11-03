package com.rnm.keepintouch;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rnm.keepintouch.data.Contact;

public class FavoritesFragment extends Fragment implements OnItemClickListener {
	
	ContactsAdapter contactsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		ListView list = new ListView(getActivity());
		list.setBackgroundColor(Color.RED);
		contactsAdapter = new ContactsAdapter(getActivity(), R.layout.contact_list_item, ((MainActivity)getActivity()).fav);
		list.setAdapter(contactsAdapter);
		list.setOnItemClickListener(this);
		return list;
	}
	
	public void refresh(List<Contact> list) {
		contactsAdapter.clear();
		contactsAdapter.addAll(list);
		contactsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
}
