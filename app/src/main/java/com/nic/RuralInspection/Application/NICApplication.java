package com.nic.RuralInspection.Application;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.nic.RuralInspection.api.LruBitmapCache;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class NICApplication extends Application {
    private static Context context;
    public static final String TAG = NICApplication.class.getSimpleName();

    private static NICApplication mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = getApplicationContext();

    }

    public static Context getGlobalContext() {
        return context;
    }

    public static synchronized NICApplication getInstance() {
        return mInstance;
    }


    public static Resources getAppResources() {
        return context.getResources();
    }

    public static String getAppString(int resourceId, Object... formatArgs) {
        String val = getAppResources().getString(resourceId, formatArgs);
        return val;
    }

    public static String getAppString(int resourceId) {
        return getAppResources().getString(resourceId);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
