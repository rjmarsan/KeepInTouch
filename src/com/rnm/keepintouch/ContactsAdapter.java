package com.rnm.keepintouch;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
	private static final String TAG = "ContactsAdapter";
	Typeface thin;
	PrettyTime p = new PrettyTime();
	LayoutInflater inf;
	Cache mCache;
	
	public ContactsAdapter(Context context, Cache cache, int textViewResourceId, List<Contact> objects) {
		super(context, textViewResourceId, objects);
		thin = Typeface.createFromAsset(context.getAssets(),"Roboto-Light.ttf");
		p.setLocale(context.getResources().getConfiguration().locale);
		inf = LayoutInflater.from(getContext());
		mCache = cache;
	}
	
	private static class ViewHolder{
		ImageView badge;
		TextView name;
		TextView method;
		TextView contacted;
		ImageView direction;
		View spacerTop;
		View spacerBottom;
		String contactUri;
		LoadPicTask task;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		final Contact contact = (Contact) getItem(position);
		ViewHolder holder;
		
		if(view == null){
			view = inf.inflate(R.layout.contact_list_item, null, false);
			holder = new ViewHolder();
			holder.badge = (ImageView)view.findViewById(R.id.contacted_badge);
			holder.name = (TextView)view.findViewById(R.id.contacted_name);
			holder.method = (TextView)view.findViewById(R.id.contacted_method);		
			holder.contacted = (TextView)view.findViewById(R.id.contacted_time);
			holder.direction = (ImageView)view.findViewById(R.id.contacted_direction);
			holder.spacerTop = view.findViewById(R.id.spacertop);
			holder.spacerBottom = view.findViewById(R.id.spacerbottom);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}

		Log.d(TAG, "Making badge " + contact.name);
		
		setContactImage(holder, contact);

		holder.name.setText(contact.name);
		
		view.findViewById(R.id.contacted_badge_box).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QuickContact.showQuickContact(getContext(), v, Uri.parse(contact.uri), QuickContact.MODE_LARGE, null);
			}
		});
        
		ContactEvent mostrecent = contact.getLatest();
		
		if (position != 0) holder.spacerTop.setVisibility(View.GONE);
		else holder.spacerTop.setVisibility(View.VISIBLE);
		if (position != getCount()-1) holder.spacerBottom.setVisibility(View.GONE);
		else holder.spacerBottom.setVisibility(View.VISIBLE);

		holder.method.setTypeface(thin);
		holder.contacted.setTypeface(thin);
		
		if (mostrecent != null) {
			holder.method.setVisibility(View.VISIBLE);
			holder.method.setText(mostrecent.type == TYPE.SMS ? R.string.text_message : R.string.phone_call);
			holder.direction.setVisibility(View.VISIBLE);
			holder.direction.setImageResource(mostrecent.isOutgoing() ? R.drawable.outgoing : R.drawable.incoming);
			if(mostrecent.timestamp == Long.MIN_VALUE){
				holder.contacted.setText(R.string.never);
			}else{
				holder.contacted.setText(formatTimeAgo(mostrecent.timestamp));
			}
		} else {
			holder.direction.setVisibility(View.GONE);
			holder.method.setText("");
			holder.method.setVisibility(View.GONE);
			holder.contacted.setText(R.string.never);
		}
		
		return view;
	}
	
	
	private void setContactImage(ViewHolder holder, Contact contact) {
		
		if (holder.task != null) holder.task.cancel(true);
		holder.contactUri = contact.id;
		
		Bitmap bitmap = mCache.getBitmapFromMemCache(contact.id);
		if (bitmap == null) {
			holder.badge.setImageResource(R.drawable.ic_contact_picture);
			holder.task = new LoadPicTask(contact, holder, getContext(), contact.id, mCache);
			holder.task.execute();
		} else {
			holder.badge.setImageBitmap(bitmap);
		}
		

	}
	
	private static class LoadPicTask extends AsyncTask<Void, Void, Bitmap>{
		Contact contact;
		ViewHolder holder;
		Context context;
		String contactUid;
		Cache mCache;
		
		public LoadPicTask(Contact contact, ViewHolder holder, Context context, String contactUid, Cache cache){
			this.contact = contact;
			this.holder = holder;
			this.context = context;
			this.contactUid = contactUid;
			this.mCache = cache;
		}

		@Override
		protected Bitmap doInBackground(Void... param) {
	        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), Uri.parse(contact.uri), true);
	        
			Bitmap bitmap =  BitmapFactory.decodeStream(input);
			
			if (bitmap != null) mCache.addBitmapToMemoryCache(contactUid, bitmap);
			
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap){
			if (holder.contactUri.equals(contactUid)) {
				if (bitmap != null)
		        	holder.badge.setImageBitmap(bitmap);
				else
					holder.badge.setImageResource(R.drawable.ic_contact_picture);
			}
		}
		
	}
	
	private String formatTimeAgo(long ago) {
		Log.d(TAG, "Formatting span "+ago);
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
