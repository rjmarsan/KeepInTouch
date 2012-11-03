package com.rnm.keepintouch;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class FavoritesFragment extends ListFragment {
	
	ContactsAdapter contactsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		contactsAdapter = new ContactsAdapter(((MainActivity)getActivity()).fav, getActivity());
		setListAdapter(contactsAdapter);
	}
}
