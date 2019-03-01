package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.Adapter.PendingLayoutAdapter;
import com.nic.RuralInspection.Adapter.ProjectListAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

/**
 * Created by NIC on 22-02-2019.
 */

public class PendinglayoutScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {
    private static PendingLayoutAdapter pendingLayoutAdapter;
    private RecyclerView pendingLayoutRecyclerView;
    private PrefManager prefManager;
    private ImageView back_img;
    private ArrayList<BlockListValue> pendingListValues = new ArrayList<>();
    private static Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_layout_screen);
        intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        context = this;
        pendingLayoutRecyclerView = (RecyclerView) findViewById(R.id.pending_recycler_view);
        back_img = (ImageView) findViewById(R.id.backimg);
        back_img.setOnClickListener(this);

//        pendingLayoutAdapter = new PendingLayoutAdapter(this, pendingListValues);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        pendingLayoutRecyclerView.setLayoutManager(mLayoutManager);
//        pendingLayoutRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        pendingLayoutRecyclerView.setHasFixedSize(true);
//        pendingLayoutRecyclerView.setNestedScrollingEnabled(false);
//        pendingLayoutRecyclerView.setFocusable(false);
//        retrievePendingdata();
        //  pendingLayoutRecyclerView.setAdapter(pendingLayoutAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backimg:
                onBackPress();
                break;
        }
    }

    private void retrievePendingdata() {
        pendingListValues.clear();
        //  String pendingList_sql = "select * from "+ DBHelper.INSPECTION +" where delete_flag = 0";
        String pendingList_sql = "select * from(select * from "+DBHelper.INSPECTION_PENDING +" WHERE inspection_id in (select inspection_id from "+DBHelper.CAPTURED_PHOTO+"))a left join (select * from observation)b on a.observation = b.id where delete_flag = 0";
        Log.d("sql", pendingList_sql);
        Cursor pendingList = getRawEvents(pendingList_sql, null);

        if (pendingList.getCount() > 0) {
            if (pendingList.moveToFirst()) {
                do {
                    String work_id = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.WORK_ID));
                    int inspection_id = pendingList.getInt(pendingList.getColumnIndexOrThrow(AppConstant.INSPECTION_ID));
                    String stage_of_work_on_inspection = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.STAGE_OF_WORK_ON_INSPECTION));
                    String stage_of_work_on_inspection_name = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.STAGE_OF_WORK_ON_INSPECTION_NAME));
                    String date_of_inspection = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.DATE_OF_INSPECTION));
                    int observation = pendingList.getInt(pendingList.getColumnIndexOrThrow(AppConstant.OBSERVATION_ID));
                    String inspection_remark = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.INSPECTION_REMARK));
                    String created_date = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.CREATED_DATE));
                    String created_ipaddress = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.CREATED_IMEI_NO));
                    String created_username = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.CREATED_USER_NAME));
                    String Observation = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.OBSERVATION_NAME));


                    BlockListValue pendingListValue = new BlockListValue();
                    pendingListValue.setWorkID(work_id);
                    pendingListValue.setInspectionID(inspection_id);
                    pendingListValue.setWorkStageCode(stage_of_work_on_inspection);
                    pendingListValue.setWorkStageName(stage_of_work_on_inspection_name);
                    pendingListValue.setDate_of_inspection(date_of_inspection);
                    pendingListValue.setObservationID(observation);
                    pendingListValue.setInspection_remark(inspection_remark);
                    pendingListValue.setCreatedDate(created_date);
                    pendingListValue.setCreatedUserName(created_username);
                    pendingListValue.setCreatedIpAddress(created_ipaddress);
                    pendingListValue.setWorkStageName(stage_of_work_on_inspection_name);
                    pendingListValue.setObservation(Observation);
                    pendingListValues.add(pendingListValue);

                } while (pendingList.moveToNext());
            }
        }

        if (!(pendingListValues.size() < 1)) {
            pendingLayoutRecyclerView.setAdapter(pendingLayoutAdapter);

        } else {
            //list_count.setText("0");
            //not_found_tv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }



    public JSONObject pending_Sync_Data(JSONObject dataset) {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(),getResources().getString(R.string.init_vector),dataset.toString().replaceAll(" ",""));
        JSONObject savedDataSet = new JSONObject();
        try {
            savedDataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            savedDataSet.put(AppConstant.DATA_CONTENT, authKey);

          new ApiService(this).makeJSONObjectRequest("pendingSaveData", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), savedDataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
return savedDataSet;
    }



    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("pendingSaveData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    // loadBlockList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                    Utils.showAlert(this, "Saved");
                }
                Log.d("saved_response", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

}
