package com.nic.RuralInspection.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.api.Api;
import com.nic.RuralInspection.api.ApiService;
import com.nic.RuralInspection.api.ServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class SplashScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {
    private TextView textView;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        callnetwork();
    }

    public void callnetwork() {
        textView = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.equals(button)) {
            try {
                callSampleApi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* public class SendPostRequest extends AsyncTask<String, Void, String> {

         protected void onPreExecute(){}

         protected String doInBackground(String... arg0) {

             try {

                 URL url = new URL("https://www.tnrd.gov.in/project/webservices_forms/inspection/login_services.php"); // here is your URL path

                 JSONObject postDataParams = new JSONObject();
                 postDataParams.put("name", "abc");
                 postDataParams.put("email", "abc@gmail.com");
                 Log.e("params",postDataParams.toString());

                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                 conn.setReadTimeout(15000 *//* milliseconds *//*);
                conn.setConnectTimeout(15000 *//* milliseconds *//*);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }*/
    private void callSampleApi() {

        new ApiService(this).makeRequest("petProfile", Api.Method.GET, "https://www.tnrd.gov.in/project/webservices_forms/inspection/login_services.php", "not cache", this);
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        JSONObject responseInnerJSONObj = null;
        String loginResponse = null;
        loginResponse = serverResponse.getResponse();

        Toast.makeText(this,loginResponse,Toast.LENGTH_LONG).show();
//            int status = responseInnerJSONObj.getInt("Status");
//            String message = responseInnerJSONObj.getString(AppConstant.KEY_MESSAGE);
//
//            if (status == 1) {
//                if ("SignIn".equalsIgnoreCase(apiType)) {
//                    JSONObject userJSONObject = responseInnerJSONObj.getJSONObject(AppConstant.KEY_USER);
//                    String id = userJSONObject.getString(AppConstant.KEY_USER_ID);
//                    String username = userJSONObject.getString(AppConstant.KEY_USER_NAME);
//                }
//            }

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
}
