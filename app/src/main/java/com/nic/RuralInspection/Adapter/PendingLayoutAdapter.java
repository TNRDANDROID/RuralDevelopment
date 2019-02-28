package com.nic.RuralInspection.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.Activity.Dashboard;
import com.nic.RuralInspection.Activity.PendinglayoutScreen;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Fragment.PendingLayoutFragment;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.Utils.UrlGenerator;
import com.nic.RuralInspection.Utils.Utils;
import com.nic.RuralInspection.api.Api;
import com.nic.RuralInspection.api.ApiService;
import com.nic.RuralInspection.api.PostMethod;
import com.nic.RuralInspection.api.ServerResponse;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

/**
 * Created by NIC on 21-02-2019.
 */

public class PendingLayoutAdapter extends RecyclerView.Adapter<PendingLayoutAdapter.MyViewHolder> {

    private static Context context;
    private List<BlockListValue> pendingListValues;
    static PrefManager prefManager;
    static JSONObject dataset = new JSONObject();
    private PendingLayoutFragment pendingLayoutFragment;

    public PendingLayoutAdapter(Context context, List<BlockListValue> pendingListValues, PendingLayoutFragment pendingLayoutFragment) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.pendingListValues = pendingListValues;
        this.pendingLayoutFragment = pendingLayoutFragment;
    }

    @Override
    public PendingLayoutAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_layout, parent, false);
        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(PendingLayoutAdapter.MyViewHolder holder, final int position) {

        holder.pend_work_id.setText(pendingListValues.get(position).getWorkID());
        holder.pend_stage.setText(pendingListValues.get(position).getWorkStageName());
        holder.pend_inspected_date.setText(pendingListValues.get(position).getDate_of_inspection());
        holder.pend_observation.setText(pendingListValues.get(position).getObservation());

        holder.add_inspection_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPending(position);
            }
        });

        holder.del_inspection_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePending(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private MyCustomTextView pend_work_id, pend_stage, pend_inspected_date, pend_observation, add_inspection_report,del_inspection_report;

        public MyViewHolder(View itemView) {
            super(itemView);
            pend_work_id = (MyCustomTextView) itemView.findViewById(R.id.pend_work_id);
            pend_stage = (MyCustomTextView) itemView.findViewById(R.id.pend_stage);
            pend_inspected_date = (MyCustomTextView) itemView.findViewById(R.id.pend_inspected_date);
            pend_observation = (MyCustomTextView) itemView.findViewById(R.id.pend_observation);
            add_inspection_report = (MyCustomTextView) itemView.findViewById(R.id.add_inspection_report);
            del_inspection_report = (MyCustomTextView) itemView.findViewById(R.id.del_inspection_report);
        }
    }

    public void UploadPending(int position) {

        String work_id = pendingListValues.get(position).getWorkID();
        int inspection_id = pendingListValues.get(position).getInspectionID();
        prefManager.setKeyDeleteId(String.valueOf(inspection_id));
        String stage_of_work_on_inspection = pendingListValues.get(position).getWorkStageCode();
        String stage_of_work_on_inspection_name = pendingListValues.get(position).getWorkStageName();
        String date_of_inspection = pendingListValues.get(position).getDate_of_inspection();
        int observation = pendingListValues.get(position).getObservationID();
        String inspection_remark = pendingListValues.get(position).getInspection_remark();
        String created_date = pendingListValues.get(position).getCreatedDate();
        String created_ipaddress = pendingListValues.get(position).getCreatedIpAddress();
        String created_username = pendingListValues.get(position).getCreatedUserName();
        String Observation = pendingListValues.get(position).getObservation();


        try {
            dataset.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_HIGH_VALUE_PROJECT_INSPECTION_SAVE);
            dataset.put(AppConstant.WORK_ID, work_id);
            dataset.put(AppConstant.STAGE_OF_WORK_ON_INSPECTION, stage_of_work_on_inspection);
            dataset.put(AppConstant.DATE_OF_INSPECTION, date_of_inspection);
            dataset.put(AppConstant.OBSERVATION, observation);
            dataset.put(AppConstant.INSPECTION_REMARK, inspection_remark);
            dataset.put(AppConstant.CREATED_DATE, created_date);
            dataset.put(AppConstant.CREATED_IP_ADDRESS, created_ipaddress);
            dataset.put(AppConstant.CREATED_USER_NAME, created_username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray imageJson = new JSONArray();

        String image_sql = "Select * from " + DBHelper.CAPTURED_PHOTO + " where inspection_id =" + inspection_id + " and work_id =" + work_id;
        Log.d("sql", image_sql);
        Cursor image = getRawEvents(image_sql, null);

        if (image.getCount() > 0) {
            int i = 0;
            if (image.moveToFirst()) {
                do {
                    String image_work_id = image.getString(image.getColumnIndexOrThrow(AppConstant.WORK_ID));
                    int image_inspection_id = image.getInt(image.getColumnIndexOrThrow(AppConstant.INSPECTION_ID));
                    String latitude = image.getString(image.getColumnIndexOrThrow(AppConstant.LATITUDE));
                    String longitude = image.getString(image.getColumnIndexOrThrow(AppConstant.LONGITUDE));
                    String images = image.getString(image.getColumnIndexOrThrow(AppConstant.IMAGE));
                    String description = image.getString(image.getColumnIndexOrThrow(AppConstant.DESCRIPTION));

                    JSONArray imageArray = new JSONArray();

                    imageArray.put(i);
                    imageArray.put(image_work_id);
                    imageArray.put(latitude);
                    imageArray.put(longitude);
                    imageArray.put(images.trim());
                    imageArray.put(description);
                    i++;
                    imageJson.put(imageArray);

                } while (image.moveToNext());
            }
        }

        try {
            dataset.put("image_details", imageJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String oof = dataset.toString();
        int maxLogSize = 1000;
        for (int i = 0; i <= oof.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > oof.length() ? oof.length() : end;
            Log.v("oof", oof.substring(start, end));
        }
        if(Utils.isOnline()) {
            pendingLayoutFragment.pending_Sync_Data(dataset);
        }else {
            Utils.showAlert(context,"Turn On Mobile Data To Upload");
        }


    }

    public void deletePending(int position) {
        String work_id = pendingListValues.get(position).getWorkID();
        String inspection_id = String.valueOf(pendingListValues.get(position).getInspectionID());

       int sdsm = db.delete(DBHelper.INSPECTION_PENDING,"inspection_id=? and work_id=?",new String[] {inspection_id,work_id});
       Log.d("sdsm",String.valueOf(sdsm));
       Dashboard.getPendingCount();
        pendingListValues.remove(position);
      //  pendingLayoutFragment.notify();
    }


    public static JSONObject dataTobeSavedJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), context.getResources().getString(R.string.init_vector), dataset.toString().replaceAll(" ", ""));
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("saving", "" + authKey);
        return dataSet;
    }


    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

}
