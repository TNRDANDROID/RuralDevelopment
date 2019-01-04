package com.nic.RuralInspection.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nic.RuralInspection.Application.NICApplication;
import com.nic.RuralInspection.Support.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */
public class ApiService {
    private Context context;
    private static ApiService apiService = null;
    private ProgressHUD progressHUD;



    public static ApiService getInstance(Context c) {
        if (apiService == null) {
            apiService = new ApiService(c);
        }
        return apiService;
    }

    public ApiService(Context c) {
        this.context = c;
        try {
            progressHUD = ProgressHUD.show(this.context, "Loading...", true, false, null);
        } catch (Exception e) {
        }

    }

    public CustomRequest getRequest(String api, int method, String url , String type, final Api.ServerResponseListener listener) {
        Log.d("url*",url);
        return new CustomRequest(api, method, url , type, new Response.Listener<ServerResponse>() {
            @Override
            public void onResponse(ServerResponse myResponse) {
                hideProgress();
                listener.OnMyResponse(myResponse);
                Log.d("Response", myResponse.getResponse().toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgress();
                Log.d("Volley Response", "error response");
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
    void hideProgress() {
        try {
            if (progressHUD != null)
                progressHUD.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
