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
import com.nic.RuralInspection.Adapter.ViewActionAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

public class ViewActions extends AppCompatActivity implements View.OnClickListener,Api.ServerResponseListener {

    private static ViewActionAdapter viewActionAdapter;
    private RecyclerView viewActionRecycleView;
    private PrefManager prefManager;
    private ImageView back_img;
    private ArrayList<BlockListValue> actionListValues = new ArrayList<>();
    private static Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_action_screen);
        intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        context = this;
        viewActionRecycleView = (RecyclerView) findViewById(R.id.viewAction_recycler_view);
        back_img = (ImageView) findViewById(R.id.backimg);
        back_img.setOnClickListener(this);

        viewActionAdapter = new ViewActionAdapter(this, actionListValues);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        viewActionRecycleView.setLayoutManager(mLayoutManager);
        viewActionRecycleView.setItemAnimator(new DefaultItemAnimator());
        viewActionRecycleView.setHasFixedSize(true);
        viewActionRecycleView.setNestedScrollingEnabled(false);
        viewActionRecycleView.setFocusable(false);
        retrieveActiondata();
        viewActionRecycleView.setAdapter(viewActionAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backimg:
                onBackPress();
                break;
        }
    }

    private void retrieveActiondata() {
        actionListValues.clear();
        String actionList_sql = "select * from(select * from "+DBHelper.INSPECTION_PENDING +" WHERE inspection_id in (select inspection_id from "+DBHelper.CAPTURED_PHOTO+"))a left join (select * from observation)b on a.observation = b.id where delete_flag = 0";
        Log.d("sql", actionList_sql);
        Cursor actionList = getRawEvents(actionList_sql, null);

        if (actionList.getCount() > 0) {
            if (actionList.moveToFirst()) {
                do {
//                    String work_id = actionList.getString(pendingList.getColumnIndexOrThrow(AppConstant.WORK_ID));
//                    int inspection_id = actionList.getInt(pendingList.getColumnIndexOrThrow(AppConstant.INSPECTION_ID));
//                    String stage_of_work_on_inspection = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.STAGE_OF_WORK_ON_INSPECTION));
//                    String stage_of_work_on_inspection_name = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.STAGE_OF_WORK_ON_INSPECTION_NAME));
//                    String date_of_inspection = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.DATE_OF_INSPECTION));
//                    int observation = pendingList.getInt(pendingList.getColumnIndexOrThrow(AppConstant.OBSERVATION_ID));
//                    String inspection_remark = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.INSPECTION_REMARK));
//                    String created_date = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.CREATED_DATE));
//                    String created_ipaddress = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.CREATED_IP_ADDRESS));
//                    String created_username = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.CREATED_USER_NAME));
//                    String Observation = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.OBSERVATION_NAME));
//
//
//                    BlockListValue pendingListValue = new BlockListValue();
//                    pendingListValue.setWorkID(work_id);
//                    pendingListValue.setInspectionID(inspection_id);
//                    pendingListValue.setWorkStageCode(stage_of_work_on_inspection);
//                    pendingListValue.setWorkStageName(stage_of_work_on_inspection_name);
//                    pendingListValue.setDate_of_inspection(date_of_inspection);
//                    pendingListValue.setObservationID(observation);
//                    pendingListValue.setInspection_remark(inspection_remark);
//                    pendingListValue.setCreatedDate(created_date);
//                    pendingListValue.setCreatedUserName(created_username);
//                    pendingListValue.setCreatedIpAddress(created_ipaddress);
//                    pendingListValue.setWorkStageName(stage_of_work_on_inspection_name);
//                    pendingListValue.setObservation(Observation);
                    //actionListValues.add(pendingListValue);

                } while (actionList.moveToNext());
            }
        }

        if (!(actionListValues.size() < 1)) {
            viewActionRecycleView.setAdapter(viewActionAdapter);

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
