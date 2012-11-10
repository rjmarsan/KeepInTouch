package com.rnm.keepintouch;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ItemUIHelper {
	
	
	public static int ICON_LARGE = 120;
	public static int ICON_SMALL = 100;
	

	
	public static String getFirstName(String name) {
		try {
			return name.split(" ")[0];
		} catch (Exception e) {
			e.printStackTrace();
			return name;
		}
	}
	
	public static Bitmap getBitmap(Context c, String uri, int minsizeDP) {
		InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(c.getContentResolver(),Uri.parse(uri), true);
		Bitmap bitmap = BitmapFactory.decodeStream(input);
		if (bitmap != null && bitmap.getScaledHeight(c.getResources().getDisplayMetrics()) < minsizeDP) {
			int newsize = Math.round(c.getResources().getDisplayMetrics().density * minsizeDP);
			bitmap = Bitmap.createScaledBitmap(bitmap, newsize, newsize, true);
		}
		return bitmap;
	}

}
