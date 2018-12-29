package com.nic.RuralMonitoring;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

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
