package com.rnm.keepintouch;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.ocpsoft.pretty.time.PrettyTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactEvent;
import com.rnm.keepintouch.data.ContactEvent.TYPE;
import com.rnm.keepintouch.data.ContactPersist;
import com.rnm.keepintouch.data.ContactsData;

public class RecentContactWidgetProvider extends AppWidgetProvider {
//	private static final String ACTION_CLICK = "ACTION_CLICK";

	private static final String TAG = "recendcontactwidgetprovider";
	
	public final static String UPDATE_CUSTOM = "com.rnm.keepintouch.UPDATE_CUSTOM";
	public final static String ACTION_CLICKED = "com.rnm.keepintouch.ACTION_CLICKED";
	
	
	public static void scheduleUpdate(Context pContext) {
	    Log.d(TAG, "startAlarm");
	    AlarmManager am = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);

	    Intent intent = new Intent(UPDATE_CUSTOM);
	    intent.setClass(pContext, RecentContactWidgetProvider.class);
	    PendingIntent pi = PendingIntent.getBroadcast(pContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

	    am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000*60*30, pi);
	}
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		super.onUpdate(context, appWidgetManager, appWidgetIds); 
		
		 Log.d(TAG, "on update widget: " + appWidgetIds.length);
		 for (int widgetId : appWidgetIds) {
			 RemoteViews views = updateId(context, widgetId);
			 if (views != null) appWidgetManager.updateAppWidget(widgetId, views);

		 }
		 
		 scheduleUpdate(context);
	}
	
	
	public RemoteViews updateId(Context context, int widgetId) {
		 Log.d(TAG, "looping through widgets");
		 
		 Contact contact = ContactPersist.getContact(context, widgetId+"");
		 Log.d(TAG, "Contact: "+contact);
		 if (contact != null) {
			 ContactsData data = new ContactsData();
			 data.updateContact(context, contact);
			 
			 
			 RemoteViews remoteViews = setupRemoteViews(context, contact);
			 //ContactPersist.putContact(context, contact, widgetId+""); //save it for later?
		      
		      return remoteViews;
		 }
		 return null;
		 
	}
	
	
	public static RemoteViews setupRemoteViews(Context context, Contact contact) {
		ContactEvent latest = contact.getLatest();
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);

		remoteViews.setTextViewText(R.id.contacted_name, contact.name);
		InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),Uri.parse(contact.uri), true);
		Bitmap bitmap = BitmapFactory.decodeStream(input);
		if (bitmap != null)
			remoteViews.setImageViewBitmap(R.id.contacted_badge, bitmap);
		else
			remoteViews.setImageViewResource(R.id.contacted_badge, R.drawable.ic_contact_picture);		 
		 if (latest != null) {
			 PrettyTime p = new PrettyTime();
			 remoteViews.setTextViewText(R.id.contacted_method, latest.type == TYPE.SMS ? "text message" : "phone call");
			 remoteViews.setTextViewText(R.id.contacted_time, "" + p.format(new Date(latest.timestamp)));
			 remoteViews.setImageViewResource(R.id.contacted_direction, latest.isOutgoing() ? R.drawable.outgoing : R.drawable.incoming);
		 } else {
			 remoteViews.setTextViewText(R.id.contacted_time, "never" );
			 remoteViews.setViewVisibility(R.id.contacted_method, View.GONE);
		 }

		 /** setup the main box **/
		Intent i;
		String number = (latest != null) ? latest.number : contact.phonenumber.get(0);
		Log.d(TAG, "Latest : "+latest);
		if (latest == null || latest.type == TYPE.SMS) {
			i = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
		} else {
			i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + number));
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_ONE_SHOT, i, 0);
		remoteViews.setOnClickPendingIntent(R.id.contacted_mainbox,pendingIntent);
		
		/** Setup the icon box **/
		PendingIntent appIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_ONE_SHOT, new Intent(context, MainActivity.class), 0);
		remoteViews.setOnClickPendingIntent(R.id.contacted_badge_box, appIntent);

		return remoteViews;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
	 	Log.d(TAG, "on INTENT: " + intent);
		super.onReceive(context, intent);
		if (intent.getAction().equals(UPDATE_CUSTOM)) {
		 	Log.d(TAG, "on Scheduling Update!");
			ComponentName provider = new ComponentName(context, RecentContactWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			int[] ids = manager.getAppWidgetIds(provider);
			for (int widgetId : ids) {
				RemoteViews views = updateId(context, widgetId);
				if (views != null)
					manager.updateAppWidget(widgetId, views);
			}
			Set<String> ok = new HashSet<String>();
			for (int i : ids) ok.add(i+"");
			ContactPersist.clearAll(context, ok);
			scheduleUpdate(context);
		}
	}
}
