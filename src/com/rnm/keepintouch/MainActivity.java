package com.rnm.keepintouch;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactsData;

public class MainActivity extends FragmentActivity {

    ContactsData data;
    List<Contact> fav = new ArrayList<Contact>();
    FavoritesFragment main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        main = (FavoritesFragment)getSupportFragmentManager().findFragmentById(R.id.favorites_fragment);
        
        getActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
