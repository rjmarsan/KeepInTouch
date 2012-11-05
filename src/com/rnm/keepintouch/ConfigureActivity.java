package com.rnm.keepintouch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactPersist;
import com.rnm.keepintouch.data.ContactsData;

public class ConfigureActivity extends Activity implements OnItemClickListener {
	
    ContactsData data;
	ContactsAdapter contactsAdapter;
	List<Contact> contactlist;
	int mAppWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.favorites_list);
		ListView list = (ListView)findViewById(R.id.list);
		list.setDividerHeight(0);
		//list.setDivider(getResources().getDrawable(android.R.drawable.menu_frame));
		contactsAdapter = new ContactsAdapter(this, R.layout.contact_list_item, new ArrayList<Contact>());
		list.setAdapter(contactsAdapter);
		list.setOnItemClickListener(this);
		list.setEmptyView(findViewById(R.id.empty));
		new GetDataTask().execute();
		
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
		    mAppWidgetId = extras.getInt(
		            AppWidgetManager.EXTRA_APPWIDGET_ID, 
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		setResult(RESULT_CANCELED); //just in case they back out

	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Contact contact = contactsAdapter.getItem(arg2);
		Log.d("ConfigureActivity", "Selected :" + contact);

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		//appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, 0);
		RemoteViews remoteViews = RecentContactWidgetProvider.setupRemoteViews(this, contact);
		appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
		
		RecentContactWidgetProvider.scheduleUpdate(this);
		ContactPersist.putContact(getApplicationContext(), contact, mAppWidgetId+"");

		
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();

	}
	
	
    public class GetDataTask extends AsyncTask<Void, Void, ContactsData>{
		@Override
		protected ContactsData doInBackground(Void... params) {
			if (data == null) data = new ContactsData();
			data.update(ConfigureActivity.this);
			return data;
		}
		
		@Override
		protected void onPostExecute(ContactsData d){
			data = d;
	        contactsAdapter.addAll(data.getFavoriteContacts());
		}
    }
    

}
