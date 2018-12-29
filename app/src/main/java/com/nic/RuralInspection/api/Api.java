package com.nic.RuralInspection.api;

import com.android.volley.VolleyError;

/**
 * Created by AchanthiSundar on 20-01-2016.
 */
public class Api {

    public interface Method {
        int GET = 0;
        int POST = 1;
    }

    public interface OnParseListener {
        void onParseComplete(int i);
    }

    public interface ServerResponseListener {
        void OnMyResponse(ServerResponse serverResponse);

        void OnError(VolleyError volleyError);
    }
}
