package com.nic.RuralInspection.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.Activity.Dashboard;
import com.nic.RuralInspection.Adapter.PendingLayoutAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomLayoutManager;
import com.nic.RuralInspection.Support.MyCustomTextView;
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

public class PendingLayoutFragment extends Fragment implements View.OnClickListener,Api.ServerResponseListener {

    private PrefManager prefManager;
    private String cacheValue = "cache";
    private RecyclerView pending_recycler_view;
    private ArrayList<BlockListValue> pendingListValues = new ArrayList<>();

    private JSONArray updatedJsonArray;
    private Context context;
    private static PendingLayoutAdapter pendingLayoutAdapter;
    private MyCustomTextView not_found_tv,title_tv;


    public PendingLayoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pending_layout_screen, container, false);
        initializeUI(view);
        return view;
    }

    public void initializeUI(View view) {

        prefManager = new PrefManager(getActivity());

        pending_recycler_view = (RecyclerView) view.findViewById(R.id.pending_recycler_view);
        not_found_tv = (MyCustomTextView) view.findViewById(R.id.not_found_tv);
        title_tv = (MyCustomTextView) view.findViewById(R.id.title_tv);
        MyCustomLayoutManager mLayoutManager = new MyCustomLayoutManager(getActivity());
        pending_recycler_view.setLayoutManager(mLayoutManager);
        pendingLayoutAdapter = new PendingLayoutAdapter(getActivity(), pendingListValues,this );
        pending_recycler_view.setAdapter(pendingLayoutAdapter);
        pending_recycler_view.setNestedScrollingEnabled(false);
        if (prefManager.getLevels().equalsIgnoreCase("D")) {
            retrievePendingdata_Inspection();
        }else if(prefManager.getLevels().equalsIgnoreCase("B")) {
            retrievePendingdata_Action();
        }
        title_tv.setText("Pending Screen");


    }
    public void retrievePendingdata_Inspection() {
        pendingListValues.clear();
        String pendingList_sql = "select * from(select * from "+DBHelper.INSPECTION_PENDING +" WHERE inspection_id in (select inspection_id from "+DBHelper.CAPTURED_PHOTO+"))a left join (select * from "+DBHelper.OBSERVATION_TABLE+")b on a.observation = b.id where delete_flag = 0";
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
            Dashboard.getPendingCount();
            pending_recycler_view.setAdapter(pendingLayoutAdapter);

        } else {
            //not_found_tv.setVisibility(View.VISIBLE);
            Dashboard.getPendingCount();
        }

    }

    public void retrievePendingdata_Action() {
        pendingListValues.clear();
        String pendingList_sql = "select * from " + DBHelper.INSPECTION_ACTION + " WHERE id in (select action_id from captured_photo) and delete_flag = 0";
        Cursor pendingList = getRawEvents(pendingList_sql, null);

        if (pendingList.getCount() > 0) {
            if (pendingList.moveToFirst()) {
                do {
                    String work_id = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.WORK_ID));
                    int inspection_id = pendingList.getInt(pendingList.getColumnIndexOrThrow(AppConstant.INSPECTION_ID));
                    String date_of_action = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.DATE_OF_ACTION));
                    String action_remark = pendingList.getString(pendingList.getColumnIndexOrThrow(AppConstant.ACTION_REMARK));
                    int action_id = pendingList.getInt(pendingList.getColumnIndexOrThrow("id"));


                    BlockListValue pendingListValue = new BlockListValue();
                    pendingListValue.setWorkID(work_id);
                    pendingListValue.setInspectionID(inspection_id);
                    pendingListValue.setActionID(action_id);
                    pendingListValue.setDate_of_Action(date_of_action);
                    pendingListValue.setAction_remark(action_remark);

                    pendingListValues.add(pendingListValue);

                } while (pendingList.moveToNext());
            }
        }

        if (!(pendingListValues.size() < 1)) {
            Dashboard.getPendingCount();
            pending_recycler_view.setAdapter(pendingLayoutAdapter);

        } else {
            //not_found_tv.setVisibility(View.VISIBLE);
            Dashboard.getPendingCount();
        }

    }


    void startActivity( ) {


            Intent intent = new Intent(getActivity(), Dashboard.class);

            getActivity().startActivityForResult(intent, 1);
            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                String cameback="CameBack";
                Intent intent = new Intent(getActivity(),Dashboard.class);
                intent.putExtra("Comingback", cameback);
                startActivity(intent);
                return true;
        }
        return false;
    }
    @Override
    public void onClick(View v) {

    }

//    public void pending_Sync_Data() {
//        try {
//            new ApiService(getActivity()).makeJSONObjectRequest("pendingSaveData", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), pendingLayoutAdapter.dataTobeSavedJsonParams(), "not cache",  this);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    public JSONObject pending_Sync_Data(JSONObject dataset) {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(),getResources().getString(R.string.init_vector),dataset.toString().replaceAll(" ",""));
        JSONObject savedDataSet = new JSONObject();
        try {
            savedDataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            savedDataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(getActivity()).makeJSONObjectRequest("pendingSaveData", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), savedDataSet, "not cache", this);

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
                    if (prefManager.getLevels().equalsIgnoreCase("D")) {
                        db.delete(DBHelper.INSPECTION_PENDING,"inspection_id=?",new String[] {prefManager.getKeyDeleteId()});
                        retrievePendingdata_Inspection();
                    }else if(prefManager.getLevels().equalsIgnoreCase("B")) {
                        db.delete(DBHelper.INSPECTION_ACTION,"id=?",new String[] {prefManager.getKeyDeleteId()});
                        retrievePendingdata_Action();
                    }

                    pendingLayoutAdapter.notifyDataSetChanged();

                    Utils.showAlert(getActivity(), "Uploaded");
                }
                Log.d("saved_response", "" + responseDecryptedBlockKey);
            }
            Dashboard.getPendingCount();
            if(pendingLayoutAdapter.getPendingSize() <1){
                Dashboard.hidePending();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
}
