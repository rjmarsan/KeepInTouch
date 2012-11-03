package com.rnm.keepintouch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.rnm.keepintouch.data.Contact;

public class AllContactsFragment extends ListFragment {
	
	ContactsAdapter contactsAdapter;
	
	OnClickListener onClickListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		contactsAdapter = new ContactsAdapter(((MainActivity)getActivity()).alpha, getActivity());
		setListAdapter(contactsAdapter);
	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id){
		Contact contact = (Contact) contactsAdapter.getItem(position);
		Log.i("allcontactfragment", "name: " + contact.name);
		
		new ContactDialog(contact).show(getFragmentManager(), getTag());
		
//		onClickListener.onClickListener(contact);
	}
	
	
	public interface OnClickListener{
		public void onClickListener(Contact contact);
	}

//	@Override
//	public void onAttach(Activity activity){
//		super.onAttach(activity);
//		try{
//			onClickListener = (OnClickListener) activity;
//		}catch(ClassCastException e){
//			throw new ClassCastException(activity.toString() + "Must implement OnClickListener");
//		}
//	}
	

}
