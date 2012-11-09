package com.rnm.keepintouch;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class Cache {

	private LruCache<String,Bitmap> mMemoryCache;
	
	
	public Cache(int cacheSize) {
		mMemoryCache = new LruCache<String,Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in bytes rather than number of items.
	            return bitmap.getByteCount();
	        }
	    };
	}
	
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}

}
