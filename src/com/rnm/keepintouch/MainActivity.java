package com.rnm.keepintouch;

import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactsData;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;
    ContactsData data;
    List<Contact> alpha;
    List<Contact> fav;
    List<Contact> rec;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        data = new ContactsData();
        data.update(getApplicationContext());
        alpha = data.getAlphabeticalContacts();
        fav = data.getFavoriteContacts();
        rec = data.getMostRecentContacts();
        
        new GetDataTask(getApplicationContext()).execute();
        
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        actionBar.selectTab(actionBar.getTabAt(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

		@Override
		public Fragment getItem(int i) {

			switch (i) {
			case 0:
				return new FavoritesFragment();
			case 1:
				return new AllContactsFragment();
			case 2:
				return new RecentFragment();
			}
			return null;
		}

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_section1).toUpperCase();
                case 1: return getString(R.string.title_section2).toUpperCase();
                case 2: return getString(R.string.title_section3).toUpperCase();
            }
            return null;
        }
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
			alpha = data.getAlphabeticalContacts();
	        fav = data.getFavoriteContacts();
	        rec = data.getMostRecentContacts();
//	        if (dialog.isShowing()) {
//	            dialog.dismiss();
//	        }
		}
		
		
    	
    }
}
