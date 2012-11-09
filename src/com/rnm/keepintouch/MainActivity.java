package com.rnm.keepintouch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactsData;

public class MainActivity extends Activity {

    ContactsData data;
    List<Contact> fav = new ArrayList<Contact>();
    FavoritesFragment main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		
        
//        main = (FavoritesFragment) getFragmentManager().findFragmentById(R.id.favorites_fragment);
        main = new FavoritesFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, main);
        transaction.commit();
        
        
        getActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_settings:
                	Log.i("TAG", "menu settings");
                	Preferences prefs = new Preferences();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(android.R.id.content, prefs);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
            }
            return false;
        }

    
    @Override
    protected void onResume() {
    	super.onResume();
        new GetDataTask().execute();
    }
    


    public class GetDataTask extends AsyncTask<Void, Void, ContactsData>{
		@Override
		protected ContactsData doInBackground(Void... params) {
			if (data == null) data = new ContactsData();
			data.update(MainActivity.this);
			return data;
		}
		
		@Override
		protected void onPostExecute(ContactsData d){
			data = d;
	        fav = data.getFavoriteContacts();
//			String json = fav.get(fav.size()-1).jsonize();
//			Log.d("JSONTEST", "To JSON: "+json);
//			Contact backagain = Contact.fromjson(json);
//			Log.d("JSONTEST", "Back again: "+backagain);
	        main.refresh(fav);
		}
    }
    
}
