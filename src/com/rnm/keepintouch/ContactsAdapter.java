package com.rnm.keepintouch;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.QuickContact;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rnm.keepintouch.data.Contact;
import com.rnm.keepintouch.data.ContactEvent;
import com.rnm.keepintouch.data.ContactEvent.TYPE;

public class ContactsAdapter extends ArrayAdapter<Contact> {
	Typeface thin;
	PrettyTime p = new PrettyTime();
	
	public ContactsAdapter(Context context, int textViewResourceId, List<Contact> objects) {
		super(context, textViewResourceId, objects);
		thin = Typeface.createFromAsset(context.getAssets(),"Roboto-Light.ttf");
		p.setLocale(context.getResources().getConfiguration().locale);
	}

	@TargetApi(14)
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		final Contact contact = (Contact) getItem(position);
		if(view == null){
			LayoutInflater inf = LayoutInflater.from(getContext());
			
			view = inf.inflate(R.layout.contact_list_item, null, false);
		}
		
		Log.d("ContactsAdapter", "Making badge " + contact.name);
		
//		QuickContactBadge badge = (QuickContactBadge)view.findViewById(R.id.contacted_badge);
//		//badge.assignContactFromPhone("630 913 9425", true);
//		badge.assignContactUri(contact.uri);
//        badge.setMode(ContactsContract.QuickContact.MODE_LARGE);
		ImageView badge = (ImageView)view.findViewById(R.id.contacted_badge);
        InputStream input;
        if (Build.VERSION.SDK_INT >= 14)
        	input = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(), Uri.parse(contact.uri), true);
        else
        	input = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(), Uri.parse(contact.uri));
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        if (bitmap != null)
        	badge.setImageBitmap(bitmap);
//        else
//        	badge.setImageResource(R.drawable.ic_contact_picture);
		TextView name = (TextView)view.findViewById(R.id.contacted_name);
		name.setText(contact.name);
		
		view.findViewById(R.id.contacted_badge_box).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QuickContact.showQuickContact(getContext(), v, Uri.parse(contact.uri), QuickContact.MODE_LARGE, null);
			}
		});

        
		ContactEvent mostrecent = contact.getLatest();
		
		if (position != 0) view.findViewById(R.id.spacertop).setVisibility(View.GONE);
		else view.findViewById(R.id.spacertop).setVisibility(View.VISIBLE);
		if (position != getCount()-1) view.findViewById(R.id.spacerbottom).setVisibility(View.GONE);
		else view.findViewById(R.id.spacerbottom).setVisibility(View.VISIBLE);
		
		TextView method = (TextView)view.findViewById(R.id.contacted_method);		
		TextView contacted = (TextView)view.findViewById(R.id.contacted_time);
		ImageView direction = (ImageView)view.findViewById(R.id.contacted_direction);
		
		method.setTypeface(thin);
		contacted.setTypeface(thin);
		
		if (mostrecent != null) {
			method.setVisibility(View.VISIBLE);
			method.setText(mostrecent.type == TYPE.SMS ? "text message" : "phone call");
			direction.setVisibility(View.VISIBLE);
			direction.setImageResource(mostrecent.isOutgoing() ? R.drawable.outgoing : R.drawable.incoming);
			if(mostrecent.timestamp == Long.MIN_VALUE){
				contacted.setText("never");
			}else{
				contacted.setText(formatTimeAgo(mostrecent.timestamp));
			}
		} else {
			direction.setVisibility(View.GONE);
			method.setText("");
			method.setVisibility(View.GONE);
			contacted.setText("never");
		}
		
		
		return view;
	}
	
	
	private String formatTimeAgo(long ago) {
		Log.d("ContactsAdapter", "Formatting span "+ago);
		return p.format(new Date(ago));
//		if (span < 1000*60) {
//			return "a few seconds ago";
//		} else if (span < 1000*60*5) {
//			return "a few minutes ago";
//		} else if (span < 1000*60*60) {
//			return ""+Math.round(((double)span)/(1000*60.0))+" minutes ago";
//		} else if (span < 1000*60*60*24) {
//			return ""+Math.round(((double)span)/(1000*60*60.0))+" hours ago";
//		} else if (span < 1000*60*60*24*30) {
//			return ""+Math.round(((double)span)/(1000*60*60*24.0))+" days ago";
//		}
//		long value = Math.round(((double)span)/(1000*60*60*24*30.0));
//		if (value <= 1)
//			return "a month ago";
//		else
//			return ""+value+" months ago";

	}

}
