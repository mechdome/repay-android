package com.repay.android;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by matt on 17/05/14.
 */
public class ImageCache extends Application {

    private static final String TAG = ImageCache.class.getSimpleName();

    /**
     * Store these in a HashMap so to keep them stored against the
     * URL that was used to call it.
     */
    private HashMap<Uri, Bitmap> mCachedImages;

    public ImageCache(){
        Log.d(TAG, "Creating image cache");
        // Instantiate it for when the application is loaded
        mCachedImages = new HashMap<Uri, Bitmap>();
    }

    /**
     * Looks at the HashMap stored in memory and gets the image stored for this URL (If there
     * is one).
     * @param url The URL that woud be used to obtain the image
     * @return Image Drawable that can be applied directly to ImageView. Returns null if no
     * image is found
     */
    public Bitmap getImage(Uri url) throws IOException {
        Log.d(TAG, "Accessing image cache for URL: "+url);
        if(mCachedImages.containsKey(url)){
            Log.d(TAG, "Image found");
            return mCachedImages.get(url);
        } else {
            Bitmap image = new ContactLookup(this).getContactPhoto(url, true);
            if(image!=null){
                Log.d(TAG, "Retrieved image from system. Adding to cache");
                storeImage(url, image);
                return image;
            }
            Log.d(TAG, "Cannot get an image for this person");
            return null;
        }
    }

    /**
     * Store the image in the active memory for use later
     * @param url The URL used to obtain the image
     * @param image The image after it has been downloaded and converted to Drawable
     */
    private void storeImage(Uri url, Bitmap image){
        if(mCachedImages.containsKey(url)){
            mCachedImages.remove(url);
        }
        mCachedImages.put(url, image);
    }
}