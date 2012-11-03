package com.rnm.keepintouch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
        
//        data = new ContactsData();
//        data.update(getApplicationContext());
//        alpha = data.getAlphabeticalContacts();
//        fav = data.getFavoriteContacts();
//        rec = data.getMostRecentContacts();
        
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
        new GetDataTask(getApplicationContext()).execute();
    }
    


    class GetDataTask extends AsyncTask<Void, Void, ContactsData>{
    	Context context;
//    	private ProgressDialog dialog;
    	
    	public GetDataTask(Context context){
    		this.context = context;
    	}
    	
    	protected void onPreExecute() {
//    		dialog = new ProgressDialog(context);
//            this.dialog.setMessage("Loading contacts data");
//            this.dialog.show();
        }
    	
		@Override
		protected ContactsData doInBackground(Void... params) {
			ContactsData d = new ContactsData();
			d.update(context);
			return d;
		}
		
		@Override
		protected void onPostExecute(ContactsData d){
			data = d;
	        fav = data.getFavoriteContacts();
	        main.refresh(fav);
//	        if (dialog.isShowing()) {
//	            dialog.dismiss();
//	        }
		}
		
		
    	
    }
}
