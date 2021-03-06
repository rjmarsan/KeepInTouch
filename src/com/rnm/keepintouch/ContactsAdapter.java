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
		TextView title;
		TextView contacted;
		ImageView direction;
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
			holder.title = (TextView)view.findViewById(R.id.contacted_title);
			holder.method = (TextView)view.findViewById(R.id.contacted_method);		
			holder.contacted = (TextView)view.findViewById(R.id.contacted_time);
			holder.direction = (ImageView)view.findViewById(R.id.contacted_direction);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}

		holder.method.setTypeface(thin);
		holder.contacted.setTypeface(thin);
		
		Log.d(TAG, "Making badge " + contact.name);
		
		
		if (contact.isPlaceholderForLatest) {
			setupLatestContact(holder);
		} else {
			setupContact(holder, contact, view);
			ContactEvent mostrecent = contact.getLatest();		
			fillInEvent(holder, mostrecent);
		}
		
		return view;
	}
	
	private void setupContact(ViewHolder holder, final Contact contact, View view) {
		setContactImage(holder, contact);

		holder.name.setText(contact.name);
		view.findViewById(R.id.contacted_badge_box).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QuickContact.showQuickContact(getContext(), v, Uri.parse(contact.uri), QuickContact.MODE_LARGE, null);
			}
		});
		holder.title.setText(R.string.last_contact);
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
	
	private void fillInEvent(ViewHolder holder, ContactEvent mostrecent) {
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
	}
	
	private void setupLatestContact(ViewHolder holder) {
		holder.title.setText(R.string.contact_least_recently);
		holder.name.setText(R.string.contact_least_recently_name);
		holder.direction.setVisibility(View.GONE);
		holder.method.setText(R.string.contact_least_recently_text);
		holder.contacted.setText(R.string.contact_least_recently_subtext);
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
//	        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), Uri.parse(contact.uri), true);
//	        
//			Bitmap bitmap =  BitmapFactory.decodeStream(input);
			
			Bitmap bitmap = ItemUIHelper.getBitmap(context, contact.uri, ItemUIHelper.ICON_LARGE);

			
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
	}

}
