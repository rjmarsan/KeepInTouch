package com.rnm.keepintouch;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import android.graphics.Rect;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract.QuickContact;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactEvent;
import com.rnm.keepintouch.data.ContactEvent.TYPE;
import com.rnm.keepintouch.data.ContactPersist;
import com.rnm.keepintouch.data.ContactsData;

public class RecentContactWidgetProvider extends AppWidgetProvider {

	private static final String TAG = "recendcontactwidgetprovider";
	
	public final static String UPDATE_CUSTOM = "com.rnm.keepintouch.UPDATE_CUSTOM";
	public final static String ACTION_CLICKED = "com.rnm.keepintouch.ACTION_CLICKED";
	
	public final static int WIDGET_LARGE = 3;
	public final static int WIDGET_MEDIUM = 2;
	public final static int WIDGET_SMALL = 1;
	
	
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
			 RemoteViews views = updateId(context, widgetId, appWidgetManager.getAppWidgetInfo(widgetId).initialLayout);
			 if (views != null) appWidgetManager.updateAppWidget(widgetId, views);

		 }
		 
		 scheduleUpdate(context);
	}
	
	
	public RemoteViews updateId(Context context, int widgetId, int size) {
		 Log.d(TAG, "looping through widgets");
		 
		 Contact contact = ContactPersist.getContact(context, widgetId+"");
		 Log.d(TAG, "Contact: "+contact);
		 if (contact != null) {
			 if (contact.isPlaceholderForLatest) {
				 contact = getLeastRecentContact(context);
			 } else {
				 ContactsData data = new ContactsData();
				 data.updateContact(context, contact);
			 }
			 
			 RemoteViews remoteViews = setupRemoteViews(context, contact, size, widgetId);
			 //ContactPersist.putContact(context, contact, widgetId+""); //save it for later?
		      
		      return remoteViews;
		 }
		 return null;
		 
	}
	
	private static String r(Context context, int id) {
		return context.getResources().getString(id);
	}
	
	
	public static RemoteViews setupRemoteViews(Context c, Contact contact, int layout, int id) {
		ContactEvent latest = contact.getLatest();
		RemoteViews remoteViews = new RemoteViews(c.getPackageName(), layout);

		Bitmap bitmap;
		if (layout == R.layout.widget_layout) {
			bitmap = ItemUIHelper.getBitmap(c, contact.uri, ItemUIHelper.ICON_LARGE);
			remoteViews.setTextViewText(R.id.contacted_name, contact.name);
		} else {
			bitmap = ItemUIHelper.getBitmap(c, contact.uri, ItemUIHelper.ICON_SMALL);
			remoteViews.setViewVisibility(R.id.contacted_name, View.GONE);
			String name = (layout == R.layout.widget_layout_2by1) ? ItemUIHelper.getFirstName(contact.name) : contact.name;
			remoteViews.setTextViewText(R.id.contacted_title, name);
		}
		if (bitmap != null)
			remoteViews.setImageViewBitmap(R.id.contacted_badge, bitmap);
		else
			remoteViews.setImageViewResource(R.id.contacted_badge, R.drawable.ic_contact_picture);		 
		 if (latest != null) {
			 PrettyTime p = new PrettyTime();
			 String text = r(c,R.string.text_message);
			 String call = r(c,R.string.phone_call);
			 if (layout == R.layout.widget_layout_2by1) {
				 text = r(c,R.string.text_message_short);
				 call = r(c,R.string.phone_call_short);
			 }
			 remoteViews.setTextViewText(R.id.contacted_method, latest.type == TYPE.SMS ? text : call);
			 remoteViews.setTextViewText(R.id.contacted_time, "" + p.format(new Date(latest.timestamp)));
			 remoteViews.setImageViewResource(R.id.contacted_direction, latest.isOutgoing() ? R.drawable.outgoing : R.drawable.incoming);
		 } else {
			 remoteViews.setTextViewText(R.id.contacted_time, r(c,R.string.never));
			 remoteViews.setViewVisibility(R.id.contacted_method, View.GONE);
			 remoteViews.setViewVisibility(R.id.contacted_direction, View.GONE);
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
		PendingIntent pendingIntent = PendingIntent.getActivity(c, PendingIntent.FLAG_ONE_SHOT, i, 0);
		remoteViews.setOnClickPendingIntent(R.id.contacted_mainbox,pendingIntent);
		
		/** Setup the icon box **/
		//PendingIntent appIntent = PendingIntent.getActivity(c, PendingIntent.FLAG_ONE_SHOT, new Intent(c, MainActivity.class), 0);
		Intent contactintent = new Intent(c, RecentContactWidgetProvider.class);
		contactintent.setAction(ACTION_CLICKED);
		contactintent.putExtra("uri", contact.uri);
		PendingIntent appIntent = PendingIntent.getBroadcast(c, id, contactintent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.contacted_badge_box, appIntent);

		return remoteViews;
	}
	
	
	public static Contact getLeastRecentContact(Context context) {
		 ContactsData data = new ContactsData();
		 data.update(context);
		 List<Contact> favorites = data.getFavoriteContacts();
		 if (favorites.size() > 0)
			 return favorites.get(0);
		 else
			 return null;
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
				RemoteViews views = updateId(context, widgetId, manager.getAppWidgetInfo(widgetId).initialLayout);
				if (views != null)
					manager.updateAppWidget(widgetId, views);
			}
			Set<String> ok = new HashSet<String>();
			for (int i : ids) ok.add(i+"");
			ContactPersist.clearAll(context, ok);
			scheduleUpdate(context);
		} else if (intent.getAction().equals(ACTION_CLICKED)) {
			String uri = intent.getStringExtra("uri");
			QuickContact.showQuickContact(context, intent.getSourceBounds(), Uri.parse(uri), QuickContact.MODE_LARGE, null);
		}

		
	}
}
