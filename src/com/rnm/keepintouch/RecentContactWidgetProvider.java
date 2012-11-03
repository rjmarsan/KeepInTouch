package com.rnm.keepintouch;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class RecentContactWidgetProvider extends AppWidgetProvider {
//	private static final String ACTION_CLICK = "ACTION_CLICK";

	private static final String TAG = "recendcontactwidgetprovider";
	
	String name = "Nelson What";
	String phone = "phone";
	Long time = 22L;
	

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		 final int N = appWidgetIds.length;
		 
		 Log.i(TAG, "on update widget: " + N);
		 for (int widgetId : appWidgetIds) {
			 Log.i(TAG, "looping through widgets");
			 
			 RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
			          R.layout.widget_layout);
			 
			 remoteViews.setTextViewText(R.id.contacted_title, name);
			 remoteViews.setTextViewText(R.id.contacted_name, name);
			 remoteViews.setTextViewText(R.id.contacted_time, "" + time);
			 
			// Register an onClickListener
//		      Intent intent = new Intent(context, RecentContactWidgetProvider.class);
//
//		      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//		      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//
//		      PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//		          0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		      remoteViews.setOnClickPendingIntent(R.id.contacted_name	, pendingIntent);
		      appWidgetManager.updateAppWidget(widgetId, remoteViews);
		 }
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
//		Log.i(TAG, "onreceive widget");
//        Bundle extras = intent.getExtras();
        
//        name = extras.getString("name");
//        Log.i(TAG, "" + extras.getString("name"));
        
//        if(extras != null){
//        	name= extras.getString("name");
//        	phone = extras.getString("phone");
//        	time = extras.getLong("days");
//        	
//        	Log.i(TAG, "" + extras.getString("name"));
//	        if(extras.containsKey("contact")){
//	        	Log.i(TAG, "containskey");
//	        	Contact contact = (Contact) extras.getSerializable("contact");
//	        	Log.i(TAG, contact.name);
//	            this.contact = contact;
//	        }
//        }
//        super.onReceive(context, intent);
    }
}
