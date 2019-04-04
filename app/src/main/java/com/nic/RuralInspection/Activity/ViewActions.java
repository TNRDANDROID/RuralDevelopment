package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.nic.RuralInspection.Support.MyCustomTextView;
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
    private ImageView back_img,homeimg;
    private ArrayList<BlockListValue> actionListValues = new ArrayList<>();
    private static Context context;
    private MyCustomTextView not_found_tv,title_tv;

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
        not_found_tv = (MyCustomTextView) findViewById(R.id.not_found_tv);
        title_tv = (MyCustomTextView) findViewById(R.id.title_tv);
        back_img = (ImageView) findViewById(R.id.backimg);
        homeimg = (ImageView) findViewById(R.id.homeimg);
        back_img.setOnClickListener(this);
        homeimg.setOnClickListener(this);

        viewActionAdapter = new ViewActionAdapter(this, actionListValues);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        viewActionRecycleView.setLayoutManager(mLayoutManager);
        viewActionRecycleView.setItemAnimator(new DefaultItemAnimator());
        viewActionRecycleView.setHasFixedSize(true);
        viewActionRecycleView.setNestedScrollingEnabled(false);
        viewActionRecycleView.setFocusable(false);
        title_tv.setText("View Actions");
        retrieveActiondata();
        viewActionRecycleView.setAdapter(viewActionAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backimg :
                onBackPress();
                break;
            case R.id.homeimg :
                dashboard();
                break;

        }
    }

    public void dashboard() {
        Intent intent = new Intent(this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    private void retrieveActiondata() {
        actionListValues.clear();
        String work_id = getIntent().getStringExtra(AppConstant.WORK_ID);
        String inspection_id = getIntent().getStringExtra(AppConstant.INSPECTION_ID);

        String actionList_sql = "select * from inspection_action where inspection_id ="+inspection_id+" and work_id ="+work_id;
        Log.d("sql", actionList_sql);
        Cursor actionList = getRawEvents(actionList_sql, null);

        if (actionList.getCount() > 0) {
            if (actionList.moveToFirst()) {
                do {
                    String date_of_action = Utils.formatDate(actionList.getString(actionList.getColumnIndexOrThrow(AppConstant.DATE_OF_ACTION)));
                    String action_remark = actionList.getString(actionList.getColumnIndexOrThrow(AppConstant.ACTION_REMARK));
                    String dist_action = actionList.getString(actionList.getColumnIndexOrThrow(AppConstant.DISTRICT_ACTION));
                    String state_action = actionList.getString(actionList.getColumnIndexOrThrow(AppConstant.STATE_ACTION));
                    String sub_div_action = actionList.getString(actionList.getColumnIndexOrThrow(AppConstant.SUB_DIV_ACTION));
                    String delete_flag = actionList.getString(actionList.getColumnIndexOrThrow(AppConstant.DELETE_FLAG));

                    BlockListValue actionListValue = new BlockListValue();
                    actionListValue.setDate_of_Action(date_of_action);
                    actionListValue.setAction_remark(action_remark);
                    if(delete_flag.equals("1")) {
                        actionListValue.setDelete_Flag("Online");
                    }else if(delete_flag.equals("0")){
                        actionListValue.setDelete_Flag("Offline");
                    }

                    if (dist_action == null   && state_action == null && sub_div_action == null){
                        actionListValue.setActionresult("NO Action Taken Yet");
                    }else
                    if ((dist_action.equals("")   & state_action.equals("") & sub_div_action.equals(""))) {
                        actionListValue.setActionresult("NO Action Taken Yet");
                    } else if (dist_action == "1" && state_action == "1" && sub_div_action == "1") {
                        actionListValue.setActionresult("Accepted");
                    } else {
                        actionListValue.setActionresult("Pending");
                    }
                   actionListValues.add(actionListValue);

                } while (actionList.moveToNext());
            }
        }

        if (!(actionListValues.size() < 1)) {
            viewActionRecycleView.setAdapter(viewActionAdapter);

        } else {
            //list_count.setText("0");
           not_found_tv.setVisibility(View.VISIBLE);
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
