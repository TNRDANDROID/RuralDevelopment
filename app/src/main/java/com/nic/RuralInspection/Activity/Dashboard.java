package com.nic.RuralInspection.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Dialog.MyDialog;
import com.nic.RuralInspection.Fragment.PendingLayoutFragment;
import com.nic.RuralInspection.Helper.AppVersionHelper;
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

import java.util.ArrayList;
import java.util.List;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class Dashboard extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener,AppVersionHelper.myAppVersionInterface {
    private ImageView logout;
    private static LinearLayout  block_user_layout, pending_upload_layout,download_layout,district_user_layout,state_user_layout;
    private CardView uploadInspectionReport;
    private static PrefManager prefManager;
    private ProgressHUD progressHUD;
    private static MyCustomTextView district_tv, block_user_tv, upload_inspection_report_tv, count_tv, title_tv;
    private JSONArray updatedJsonArray;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    TelephonyManager telephonyManager;
    String imei;
    private List<BlockListValue> pendingUpload = new ArrayList<>();
    private Fragment mContent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.frame_layout_dashboard);
        if (mContent == null) {
            mContent = new PendingLayoutFragment();
        }
        if (Utils.isOnline()) {
            checkAppVersion();
        }
        intializeUI();
//        else {
//            Utils.showAlert(this, getResources().getString(R.string.no_internet));
//        }
    }

    private void intializeUI() {
        prefManager = new PrefManager(this);
        logout = (ImageView) findViewById(R.id.logout);
        uploadInspectionReport = (CardView) findViewById(R.id.upload_inspection_report);
        pending_upload_layout = (LinearLayout) findViewById(R.id.pending_upload_layout);
        block_user_layout = (LinearLayout) findViewById(R.id.block_user_layout);
        district_user_layout = (LinearLayout) findViewById(R.id.district_user_layout);
        state_user_layout = (LinearLayout) findViewById(R.id.state_user_layout);
        download_layout = (LinearLayout) findViewById(R.id.download_layout);
        block_user_tv = (MyCustomTextView) findViewById(R.id.block_user_tv);
        upload_inspection_report_tv = (MyCustomTextView) findViewById(R.id.upload_inspection_report_tv);
        count_tv = (MyCustomTextView) findViewById(R.id.count_tv);
        district_tv = (MyCustomTextView) findViewById(R.id.district_tv);
        title_tv = (MyCustomTextView) findViewById(R.id.title_tv);
        uploadInspectionReport.setOnClickListener(this);
        pending_upload_layout.setOnClickListener(this);
        download_layout.setOnClickListener(this);
        logout.setOnClickListener(this);
        title_tv.setText("Dashboard");
        district_tv.setText(prefManager.getDistrictName());
        if (prefManager.getLevels().equalsIgnoreCase("B")) {
            block_user_layout.setVisibility(View.VISIBLE);
            block_user_tv.setText(prefManager.getBlockName());
            upload_inspection_report_tv.setText(getResources().getString(R.string.action_taken_tv));
        }
        getPendingCount();
        if (Utils.isOnline()) {
            // to avoid insertion of data while back
            Cursor toCheck = getRawEvents("SELECT * FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);
            toCheck.moveToFirst();
            if (toCheck.getCount() < 1) {
                fetchAllResponseFromApi();
            }
        }
//        else{
//            Cursor toCheck = getRawEvents("SELECT * FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);
//            if (toCheck.getCount() < 1){
//                download_layout.setVisibility(View.GONE);
//            }
//            else {
//                download_layout.setVisibility(View.VISIBLE);
//            }
//        }
        if (prefManager.getLevels().equalsIgnoreCase("B")) {
            getSchemeList();
        }
        if(prefManager.getLevels().equalsIgnoreCase("S")){
            state_user_layout.setVisibility(View.VISIBLE);
            district_user_layout.setVisibility(View.GONE );
        }

    }
    private void checkAppVersion() {
        new AppVersionHelper(this,Dashboard.this).callAppVersionCheckApi();
    }
    private void getImei() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMobileDetails();
                    }
                }, 500);

            } else {
                getMobileDetails();
            }
        } else {
            getMobileDetails();

        }
    }

    private void getMobileDetails() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        imei = telephonyManager.getDeviceId();
        prefManager.setImei(imei);
        Log.d("imei", imei);
    }

    public void fetchAllResponseFromApi() {
        getStageList();
        getObservationList();
        getBlockList();
        getDistrictList();
        // getServiceList();
        // getInspectionServiceList();
//        if (prefManager.getLevels().equalsIgnoreCase("D")) {
//            getBlockList();
//        } else {
//          block_layout.setVisibility(View.GONE);
//        }
        getVillageList();
        getFinYearList();
        getInspectedOfficersName();
    }

    public static void getPendingCount() {
        String pendingList_sql = "";
        String level="";
        if(prefManager.getLevels().equalsIgnoreCase("D")) {
            level = "D";
        }else if(prefManager.getLevels().equalsIgnoreCase("S")) {
            level = "S";
        }
        if (prefManager.getLevels().equalsIgnoreCase("D") || prefManager.getLevels().equalsIgnoreCase("S")) {
            pendingList_sql = "select * from(select * from " + DBHelper.INSPECTION_PENDING + " WHERE inspection_id in (select inspection_id from captured_photo))a left join (select * from observation)b on a.observation = b.id where delete_flag = 0 and inspection_remark != '' and level ='"+level+"'";
        } else if (prefManager.getLevels().equalsIgnoreCase("B")) {
            pendingList_sql = "select * from " + DBHelper.INSPECTION_ACTION + " WHERE id in (select action_id from captured_photo) and delete_flag = 0 and action_remark != ''";
        }
        Log.d("pendingCount",pendingList_sql);
        Cursor pendingList = getEvents(pendingList_sql, null);
        int count = pendingList.getCount();
        if (count > 0) {
            pending_upload_layout.setVisibility(View.VISIBLE);
            count_tv.setText(String.valueOf(count));
        }
    }

    public static void hidePending() {
        pending_upload_layout.setVisibility(View.GONE);
    }

    public void getDistrictList() {
        try {
            new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getServicesListUrl(), districtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getServicesListUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void getFinYearList() {
        try {
            new ApiService(this).makeJSONObjectRequest("FinYearList", Api.Method.POST, UrlGenerator.getServicesListUrl(), finyearListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getStageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("StageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), stageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getObservationList() {
        try {
            new ApiService(this).makeJSONObjectRequest("ObservationList", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), ObservationListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getInspectedOfficersName() {
        try {
            new ApiService(this).makeJSONObjectRequest("InspectedOfficers", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), inspectedOfficersJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), schemeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject districtListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.districtListJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("districtList", "" + authKey);
        return dataSet;
    }
    public JSONObject blockListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.blockListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("blockListDistrictWise", "" + authKey);
        return dataSet;
    }

    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.villageListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + authKey);
        return dataSet;
    }



    public JSONObject finyearListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeFinyearListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("finYearList", "" + authKey);
        return dataSet;
    }

    public JSONObject stageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.stageListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("StageList", "" + authKey);
        return dataSet;
    }

    public JSONObject ObservationListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.observationListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("ObservationList", "" + authKey);
        return dataSet;
    }

    public JSONObject inspectedOfficersJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.getInspectedOfficers(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("inspectedOfficersName", "" + authKey);
        return dataSet;
    }

    public JSONObject schemeListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("schemeList", "" + authKey);
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
            case R.id.pending_upload_layout:
//                pendingLyoutScreen();
                openPendingLayoutFragment();
                break;
            case R.id.download_layout:
//                pendingLyoutScreen();
                if(Utils.isOnline()) {
                    downloadScreen();
                }else{
                    Utils.showAlert(this,getResources().getString(R.string.no_internet));
                }
                break;
        }

    }

    public void openPendingLayoutFragment() {

        mContent = new PendingLayoutFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, mContent).addToBackStack("editquestion");
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        transaction.commit();

    }

    public void downloadScreen() {
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
            if (prefManager.getLevels().equalsIgnoreCase("D") || (prefManager.getLevels().equalsIgnoreCase("S"))) {
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
            if ("DistrictList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadDistrictList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("DistrictList", "" + responseDecryptedBlockKey);
            }
            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadSchemeList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
            }

            if ("VillageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadVillageList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("VillageList", "" + responseDecryptedBlockKey);
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
            if ("StageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadStageList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("StageList", "" + responseDecryptedKey);
            }
            if ("ObservationList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadObservationList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("ResObservationList", "" + responseDecryptedKey);
            }
            if ("InspectedOfficers".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadInspectedOfficersName(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                    Log.d("InspectedOfficers", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadBlockList(JSONArray jsonArray) {
        progressHUD = ProgressHUD.show(this, "Loading...", true, false, null);
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
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }

    private void loadDistrictList(JSONArray jsonArray) {
        progressHUD = ProgressHUD.show(this, "Loading...", true, false, null);
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            for (int i = 0; i < jsonArray.length(); i++) {
                String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                String districtName = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME);

                ContentValues districtListValues = new ContentValues();
                districtListValues.put(AppConstant.DISTRICT_CODE, districtCode);
                districtListValues.put(AppConstant.DISTRICT_NAME, districtName);

                LoginScreen.db.insert(DBHelper.DISTRICT_TABLE_NAME, null, districtListValues);
                Log.d("LocalDBdistrictList", "" + districtListValues);

            }
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }

    private void loadSchemeList(JSONArray jsonArray) {
        try {
            db.delete(DBHelper.SCHEME_TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressHUD = ProgressHUD.show(this, "Downloading...", true, false, null);

        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;

            for (int i = 0; i < jsonArray.length(); i++) {
                String schemeSequentialID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_SEQUENTIAL_ID);
                String schemeName = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_NAME);
                String fin_year = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);

                ContentValues schemeListLocalDbValues = new ContentValues();
                schemeListLocalDbValues.put(AppConstant.SCHEME_SEQUENTIAL_ID, schemeSequentialID);
                schemeListLocalDbValues.put(AppConstant.SCHEME_NAME, schemeName);
                schemeListLocalDbValues.put(AppConstant.FINANCIAL_YEAR, fin_year);

                LoginScreen.db.insert(DBHelper.SCHEME_TABLE_NAME, null, schemeListLocalDbValues);
                Log.d("LocalDBSchemeList", "" + schemeListLocalDbValues);

            }
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }

    private void loadVillageList(JSONArray jsonArray) {
        progressHUD = ProgressHUD.show(this, "Loading...", true, false, null);
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            for (int i = 0; i < jsonArray.length(); i++) {
                String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                String blockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                String pvname = jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME);

                ContentValues villageListValues = new ContentValues();
                villageListValues.put(AppConstant.DISTRICT_CODE, districtCode);
                villageListValues.put(AppConstant.BLOCK_CODE, blockCode);
                villageListValues.put(AppConstant.PV_CODE, pvcode);
                villageListValues.put(AppConstant.PV_NAME, pvname);


                LoginScreen.db.insert(DBHelper.VILLAGE_TABLE_NAME, null, villageListValues);
                 Log.d("LocalDBVilageList", "" + villageListValues);

            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }



    private void loadFinYearList(JSONArray jsonArray) {
        progressHUD = ProgressHUD.show(this, "Loading...", true, false, null);
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
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }

    private void loadStageList(JSONArray jsonArray) {
        progressHUD = ProgressHUD.show(this, "Loading...", true, false, null);

        try {
            //  progressHUD = ProgressHUD.show(this.context, "Loading...", true, false, null);
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;

            for (int i = 0; i < jsonArray.length(); i++) {
                String workGroupID = jsonArray.getJSONObject(i).getString(AppConstant.WORK_GROUP_ID);
                String workTypeID = jsonArray.getJSONObject(i).getString(AppConstant.WORK_TYPE_ID);
                String workStageOrder = jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_ORDER);
                String workStageCode = jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_CODE);
                String workStageName = jsonArray.getJSONObject(i).getString(AppConstant.WORK_SATGE_NAME);

                ContentValues WorkStageLocalDbValues = new ContentValues();
                WorkStageLocalDbValues.put(AppConstant.WORK_GROUP_ID, workGroupID);
                WorkStageLocalDbValues.put(AppConstant.WORK_TYPE_ID, workTypeID);
                WorkStageLocalDbValues.put(AppConstant.WORK_STAGE_ORDER, workStageOrder);
                WorkStageLocalDbValues.put(AppConstant.WORK_STAGE_CODE, workStageCode);
                WorkStageLocalDbValues.put(AppConstant.WORK_SATGE_NAME, workStageName);

                LoginScreen.db.insert(DBHelper.WORK_STAGE_TABLE, null, WorkStageLocalDbValues);
                // Log.d("LocalDBSchemeList", "" + WorkStageLocalDbValues);

            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }

    private void loadObservationList(JSONArray jsonArray) {
        progressHUD = ProgressHUD.show(this, "Loading...", true, false, null);

        try {
            //  progressHUD = ProgressHUD.show(this.context, "Loading...", true, false, null);
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;

            for (int i = 0; i < jsonArray.length(); i++) {
                int observation_id = jsonArray.getJSONObject(i).getInt(AppConstant.OBSERVATION_ID);
                String observation_name = jsonArray.getJSONObject(i).getString(AppConstant.OBSERVATION_NAME);

                ContentValues ObservationLocalDbValues = new ContentValues();
                ObservationLocalDbValues.put(AppConstant.OBSERVATION_ID, observation_id);
                ObservationLocalDbValues.put(AppConstant.OBSERVATION_NAME, observation_name);

                LoginScreen.db.insert(DBHelper.OBSERVATION_TABLE, null, ObservationLocalDbValues);
                // Log.d("LocalDBSchemeList", "" + WorkStageLocalDbValues);

            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getImei();
                }
            }, 4000);


        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }

    private void loadInspectedOfficersName(JSONArray jsonArray) {
        try {
            db.delete(DBHelper.INSPECTED_OFFICER_LIST, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressHUD = ProgressHUD.show(this, "Downloading...", true, false, null);

        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;

            for (int i = 0; i < jsonArray.length(); i++) {
                String inspected_UserName = jsonArray.getJSONObject(i).getString(AppConstant.INSPECTED_USER_NAME);
                String inspected_UserId = jsonArray.getJSONObject(i).getString(AppConstant.INSPECTED_USER_ID);
                String inspected_DesigName = jsonArray.getJSONObject(i).getString(AppConstant.INSPECTED_DESIGATION_NAME);

                ContentValues inspectedOfficerDetails = new ContentValues();
                inspectedOfficerDetails.put(AppConstant.INSPECTED_USER_ID, inspected_UserId);
                inspectedOfficerDetails.put(AppConstant.INSPECTED_USER_NAME, inspected_UserName);
                inspectedOfficerDetails.put(AppConstant.INSPECTED_DESIGATION_NAME, inspected_DesigName);

                LoginScreen.db.insert(DBHelper.INSPECTED_OFFICER_LIST, null, inspectedOfficerDetails);
                Log.d("DBInspectedOfficersList", "" + inspectedOfficerDetails);

            }
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
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

    public static Cursor getEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    @Override
    public void onAppVersionCallback(String value) {
        try {

            if (value.length() > 0 && "Update".equalsIgnoreCase(value)) {
                startActivity(new Intent(this, AppVersionActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
