package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.Adapter.CommonAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Dialog.MyDialog;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.Support.ProgressHUD;
import com.nic.RuralInspection.Utils.UrlGenerator;
import com.nic.RuralInspection.Utils.Utils;
import com.nic.RuralInspection.api.Api;
import com.nic.RuralInspection.api.ApiService;
import com.nic.RuralInspection.api.ServerResponse;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class Dashboard extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private ImageView logout;
    private LinearLayout uploadInspectionReport;
    private PrefManager prefManager;
    private ProgressHUD progressHUD;
    private MyCustomTextView district_tv;
    private JSONArray updatedJsonArray;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        if (Utils.isOnline()) {
            intializeUI();
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }
    }

    private void intializeUI() {
        prefManager = new PrefManager(this);
        logout = (ImageView) findViewById(R.id.logout);
        uploadInspectionReport = (LinearLayout) findViewById(R.id.upload_inspection_report);
        district_tv = (MyCustomTextView) findViewById(R.id.district_tv);
        uploadInspectionReport.setOnClickListener(this);
        logout.setOnClickListener(this);
        district_tv.setText(prefManager.getDistrictName());
        fetchAllResponseFromApi();
    }

    public void fetchAllResponseFromApi(){
        getServiceList();
        getInspectionServiceList();
        if (prefManager.getLevels().equalsIgnoreCase("D")) {
            getBlockList();
        } else {
//            block_layout.setVisibility(View.GONE);
        }
        getSchemeList();
        getFinYearList();

    }


    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getServicesListUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject blockListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.blockListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("blockListDistrictWise", "" + authKey);
        return dataSet;
    }

    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), schemeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject schemeListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("schemeList", "" + authKey);
        return dataSet;
    }

    public void getFinYearList() {
        try {
            new ApiService(this).makeJSONObjectRequest("FinYearList", Api.Method.POST, UrlGenerator.getServicesListUrl(), finyearListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject finyearListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeFinyearListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("finYearList", "" + authKey);
        return dataSet;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                //DashboardFragment.setViolation();
                closeApplication();
                break;
            case R.id.upload_inspection_report:
                selectBlockSchemeScreen();
                break;
        }

    }


    public void getServiceList() {
        try {
            new ApiService(this).makeJSONObjectRequest("ServiceList", Api.Method.POST, UrlGenerator.getServicesListUrl(), serviceListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getInspectionServiceList() {
        try {
            new ApiService(this).makeJSONObjectRequest("InspectionServiceList", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), serviceListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject serviceListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.serviceListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("servicelist", "" + authKey);
        return dataSet;
    }

    public JSONObject inspectionServiceListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.inspectionServiceListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("inspectionservicelist", "" + authKey);
        return dataSet;
    }

    public void selectBlockSchemeScreen() {
        Intent intent = new Intent(this, SelectBlockSchemeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void closeApplication() {
        new MyDialog(Dashboard.this).exitDialog(Dashboard.this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if (prefManager.getLevels().equalsIgnoreCase("D")) {
                if ("BlockList".equals(urlType) && responseObj != null) {
                    String key = responseObj.getString(AppConstant.ENCODE_DATA);
                    String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                    JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                    if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                        loadBlockList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                    }
                    Log.d("BlockList", "" + responseDecryptedBlockKey);
                }

            }

            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadSchemeList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("schemeAll", "" + responseDecryptedSchemeKey);
            }
            if ("FinYearList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadFinYearList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("FinYear", "" + responseDecryptedSchemeKey);
            }
            if ("WorkListDistrictWise".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                Log.d("key", "" + responseDecryptedKey);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadBlockList(JSONArray jsonArray) {
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            for (int i = 0; i < jsonArray.length(); i++) {
                String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                String blockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                String blockName = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME);

                ContentValues blockListValues = new ContentValues();
                blockListValues.put(AppConstant.DISTRICT_CODE, districtCode);
                blockListValues.put(AppConstant.BLOCK_CODE, blockCode);
                blockListValues.put(AppConstant.BLOCK_NAME, blockName);

                LoginScreen.db.insert(DBHelper.BLOCK_TABLE_NAME, null, blockListValues);
                Log.d("LocalDBblockList", "" + blockListValues);

            }
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
    }

    private void loadSchemeList(JSONArray jsonArray) {
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;

            for (int i = 0; i < jsonArray.length(); i++) {
                String schemeSequentialID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_SEQUENTIAL_ID);
                String schemeName = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_NAME);

                ContentValues schemeListLocalDbValues = new ContentValues();
                schemeListLocalDbValues.put(AppConstant.SCHEME_SEQUENTIAL_ID, schemeSequentialID);
                schemeListLocalDbValues.put(AppConstant.SCHEME_NAME, schemeName);

                LoginScreen.db.insert(DBHelper.SCHEME_TABLE_NAME, null, schemeListLocalDbValues);
                Log.d("LocalDBSchemeList", "" + schemeListLocalDbValues);

            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
    }

    private void loadFinYearList(JSONArray jsonArray) {
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;

            for (int i = 0; i < jsonArray.length(); i++) {
                String financialYear = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);

                ContentValues FinYearListLocalDbValues = new ContentValues();
                FinYearListLocalDbValues.put(AppConstant.FINANCIAL_YEAR, financialYear);


                LoginScreen.db.insert(DBHelper.FINANCIAL_YEAR_TABLE_NAME, null, FinYearListLocalDbValues);
                Log.d("LocalDBSchemeList", "" + FinYearListLocalDbValues);

            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
