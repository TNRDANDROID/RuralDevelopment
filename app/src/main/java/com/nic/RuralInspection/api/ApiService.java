package com.nic.RuralInspection.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nic.RuralInspection.NICApplication;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by AchanthiSundar on 20-01-2016.
 */
public class ApiService {
    private Context context;
    private static ApiService apiService = null;


    public static ApiService getInstance(Context c) {
        if (apiService == null) {
            apiService = new ApiService(c);
        }
        return apiService;
    }

    public ApiService(Context c) {
        this.context = c;

    }

    public CustomRequest getRequest(String api, int method, String url , String type, final Api.ServerResponseListener listener) {
        Log.d("url*",url);
        return new CustomRequest(api, method, url , type, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse myResponse) {
                listener.OnMyResponse(myResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.OnError(volleyError);
            }
        });
    }

    public JRequest getRequest_1(String api, int method, String url, JSONArray parmas, String type, final Api.ServerResponseListener listener) {
        return new JRequest(api, method, url, parmas, type, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse jsonObject) {
                listener.OnMyResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.OnError(volleyError);
            }
        });
    }

    public JRequest getJSONObjectRequest(String api, int method, String url, JSONObject parmas, String type, final Api.ServerResponseListener listener) {
        return new JRequest(api, method, url, parmas, type, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse jsonObject) {
                listener.OnMyResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.OnError(volleyError);
            }
        });
    }

    public void makeRequest_1(String api, int method, String url, JSONArray parmas, String type,final Api.ServerResponseListener listener) {
        JRequest request = getRequest_1(api, method, url, parmas, type, listener);
        request.setTimeout();
        NICApplication.getInstance().addToRequestQueue(request);
    }

    public void makeRequest(String api, int method, String url , String type, Api.ServerResponseListener listener) {
        CustomRequest request = getRequest(api, method, url , type, listener);
        request.setTimeout();
        NICApplication.getInstance().addToRequestQueue(request);
    }

    public void makeJSONObjectRequest(String api, int method, String url, JSONObject parmas, String type, final Api.ServerResponseListener listener) {
        JRequest request = getJSONObjectRequest(api, method, url, parmas, type, listener);
        request.setTimeout();
        NICApplication.getInstance().addToRequestQueue(request);
    }

}
