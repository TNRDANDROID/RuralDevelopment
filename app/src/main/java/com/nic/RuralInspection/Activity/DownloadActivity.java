package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.nic.RuralInspection.Adapter.CommonAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MultiSelectionSpinner;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import static com.nic.RuralInspection.Activity.LoginScreen.db;
import static com.nic.RuralInspection.DataBase.DBHelper.BLOCK_TABLE_NAME;
import static com.nic.RuralInspection.DataBase.DBHelper.SCHEME_TABLE_NAME;
import static com.nic.RuralInspection.DataBase.DBHelper.VILLAGE_TABLE_NAME;

public class DownloadActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MultiSelectionSpinner.MultiSpinnerListener {
    private Button done, btn_view_finyear, btn_view_block, btn_view_village, btn_view_scheme;

    CheckBox high_value_projects, all_projects;

    private MyCustomTextView title_tv, selected_finyear_tv, selected_block_tv, selected_village_tv, selected_scheme_tv;
    ;

    private PrefManager prefManager;

    private ImageView back_img;
    private List<BlockListValue> Block = new ArrayList<>();
    private List<BlockListValue> Village = new ArrayList<>();
    private List<BlockListValue> Scheme = new ArrayList<>();
    private List<BlockListValue> FinYearList = new ArrayList<>();
    private LinearLayout select_fin_year_layout, select_block_layout, select_village_layout, select_scheme_layout;
    private View view;
    final ArrayList<Integer> mVillageItems = new ArrayList<>();
    final ArrayList<Integer> mUserItems = new ArrayList<>();
    final ArrayList<Integer> mFinYearItems = new ArrayList<>();
    final ArrayList<Integer> mSchemeItems = new ArrayList<>();
    final ArrayList<String> mySchemelist = new ArrayList<String>();
    String[] blockStrings = null;
    String[] schemeStrings = null;
    String[] finyearStrings = null;
    String[] villagestrings = null;
    boolean[] checkedItems;
    boolean[] schemeCheckedItems;
    String pref_Block, pref_Village, pref_Scheme, pref_finYear;
    private ProgressHUD progressHUD;
    private JSONArray updatedJsonArray;
    boolean clicked = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_layout);
        intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        select_fin_year_layout = (LinearLayout) findViewById(R.id.select_fin_year_layout);
        select_block_layout = (LinearLayout) findViewById(R.id.select_block_layout);
        select_village_layout = (LinearLayout) findViewById(R.id.select_village_layout);
        select_scheme_layout = (LinearLayout) findViewById(R.id.select_scheme_layout);
        done = (Button) findViewById(R.id.btn_download);
        btn_view_finyear = (Button) findViewById(R.id.btn_view_finyear);
        btn_view_block = (Button) findViewById(R.id.btn_view_block);
        btn_view_village = (Button) findViewById(R.id.btn_view_village);
        btn_view_scheme = (Button) findViewById(R.id.btn_view_scheme);
        high_value_projects = (CheckBox) findViewById(R.id.high_value_projects);
        all_projects = (CheckBox) findViewById(R.id.all_projects);
        back_img = (ImageView) findViewById(R.id.backimg);
        title_tv = (MyCustomTextView) findViewById(R.id.title_tv);
        selected_finyear_tv = (MyCustomTextView) findViewById(R.id.selected_finyear_tv);
        selected_block_tv = (MyCustomTextView) findViewById(R.id.selected_block_tv);
        selected_village_tv = (MyCustomTextView) findViewById(R.id.selected_village_tv);
        selected_scheme_tv = (MyCustomTextView) findViewById(R.id.selected_scheme_tv);
        view = (View) findViewById(R.id.scheme_view);
        back_img.setOnClickListener(this);
         done.setOnClickListener(this);
        title_tv.setText("Download");

        btn_view_finyear.setOnClickListener(this);
        btn_view_block.setOnClickListener(this);
        btn_view_village.setOnClickListener(this);
        btn_view_scheme.setOnClickListener(this);
        done.setOnClickListener(this);

//        home.setOnClickListener(this);

        high_value_projects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all_projects.setChecked(false);
                    // Toast.makeText(SelectBlockSchemeScreen.this, "high value projects", Toast.LENGTH_SHORT).show();
                } else {
                    all_projects.setChecked(true);
                }

            }
        });
        all_projects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    high_value_projects.setChecked(false);
                    //   Toast.makeText(SelectBlockSchemeScreen.this, "All projects", Toast.LENGTH_SHORT).show();
                } else {
                    high_value_projects.setChecked(true);
                }

            }
        });
//        loadOfflineDBValues();
    }

    public void loadOfflineDBValues() {
        if (prefManager.getLevels().equalsIgnoreCase("D")) {
            loadOfflineBlockListDBValues();
        } /*else {
            block_layout.setVisibility(View.GONE);
        }*/
//        if (!prefManager.getLevels().equalsIgnoreCase("B")) {
//            loadOfflineVillgeListDBValues();
//        }
        loadOfflineFinYearListDBValues();
//        if (!Utils.isOnline()) {
//            loadOfflineSchemeListDBValues();
//        }
    }

    public void loadOfflineFinYearListDBValues() {
        Cursor FinYear = getRawEvents("SELECT fin_year FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);
        FinYearList.clear();
        final boolean[] FinYearcheckedItems;
        final ArrayList<String> myFinYearlist = new ArrayList<String>();
        if (FinYear.getCount() > 0) {
            if (FinYear.moveToFirst()) {
                do {
                    BlockListValue finyearList = new BlockListValue();
                    String financialYear = FinYear.getString(FinYear.getColumnIndexOrThrow(AppConstant.FINANCIAL_YEAR));
                    finyearList.setFinancialYear(financialYear);
                    FinYearList.add(finyearList);
                    //   Log.d("finyeardb", "" + finyearList);
                } while (FinYear.moveToNext());
            }

        }

        for (int i = 0; i < FinYearList.size(); i++) {
            myFinYearlist.add(FinYearList.get(i).getFinancialYear());

        }

        finyearStrings = myFinYearlist.toArray(new String[myFinYearlist.size()]);
        FinYearcheckedItems = new boolean[finyearStrings.length];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.finyear_dialog_title);
        mBuilder.setMultiChoiceItems(finyearStrings, FinYearcheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    JSONArray finyearJsonArray = new JSONArray();
                    if (!mFinYearItems.contains(position)) {
                        mFinYearItems.add(position);
                        for (position = 0; position < mFinYearItems.size(); position++) {
                            finyearJsonArray.put(FinYearList.get(position).getFinancialYear());
                        }
                    }
                    prefManager.setFinYearJson(finyearJsonArray);
                    Log.d("FinYearArray", "" + finyearJsonArray);
                } else if (mFinYearItems.contains(position)) {
                    mFinYearItems.remove(position);
                }
//                if (isChecked) {
//                    mUserItems.add(position);
//                } else {
//                    mUserItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);
        final String[] finalFinYearStrings = finyearStrings;
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";

                for (int i = 0; i < mFinYearItems.size(); i++) {
                    item = item + finalFinYearStrings[mFinYearItems.get(i)];
                    if (i != mFinYearItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                select_fin_year_layout.setVisibility(View.VISIBLE);
                selected_finyear_tv.setText(item);
                if (Utils.isOnline()) {
                    try {
                        db.delete(DBHelper.SCHEME_TABLE_NAME, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getSchemeList();
                } else {
                    selectFinancialYear();
                    loadOfflineSchemeListDBValues();
                }

            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < FinYearcheckedItems.length; i++) {
                    FinYearcheckedItems[i] = false;
                    mFinYearItems.clear();
                    selected_finyear_tv.setText("");
                    select_fin_year_layout.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }


    public void loadOfflineBlockListDBValues() {

        Cursor BlockList = getRawEvents("SELECT * FROM " + BLOCK_TABLE_NAME, null);
        Block.clear();

        final ArrayList<String> myBlocklist = new ArrayList<String>();


        if (BlockList.getCount() > 0) {
            if (BlockList.moveToFirst()) {
                do {
                    BlockListValue blockList = new BlockListValue();
                    String districtCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String blockName = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_NAME));
                    blockList.setDistictCode(districtCode);
                    blockList.setBlockCode(blockCode);
                    blockList.setBlockName(blockName);
                    Block.add(blockList);
                } while (BlockList.moveToNext());
            }
        }
        for (int i = 0; i < Block.size(); i++) {
            myBlocklist.add(Block.get(i).getBlockName());
        }
        showMultipleBlock(myBlocklist);

    }

    public void showMultipleBlock(ArrayList<String> myBlocklist) {
        blockStrings = myBlocklist.toArray(new String[myBlocklist.size()]);
        checkedItems = new boolean[myBlocklist.size()];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.block_dialog_title);
        mBuilder.setMultiChoiceItems(blockStrings, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    JSONArray blockCodeJsonArray = new JSONArray();
                    if (!mUserItems.contains(position)) {
                        mUserItems.add(position);
                        for (position = 0; position < mUserItems.size(); position++) {
                            blockCodeJsonArray.put(Block.get(position).getBlockCode());
                        }
                    }
                    prefManager.setBlockCodeJson(blockCodeJsonArray);
                    Log.d("blockcode", "" + blockCodeJsonArray);
                } else if (mUserItems.contains(position)) {
                    mUserItems.remove(position);
                }
//                if(isChecked){
//                    mUserItems.add(position);
//                }else{
//                    mUserItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mUserItems.size(); i++) {
                    item = item + blockStrings[mUserItems.get(i)];
                    if (i != mUserItems.size() - 1) {
                        item = item + ", ";


                    }
                }
//                loadOfflineVillgeListDBValues(prefManager.getBlockCodeJson());
                select_block_layout.setVisibility(View.VISIBLE);
                selected_block_tv.setText(item);
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                    selected_block_tv.setText("");
                    select_block_layout.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void loadOfflineVillgeListDBValues(JSONArray filterVillage) {

        String villageSql = "SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " WHERE bcode in" + filterVillage.toString().replace("[", "(").replace("]", ")") + " order by bcode";
        Log.d("villageSql", "" + villageSql);
        Cursor VillageList = getRawEvents(villageSql, null);
        Village.clear();

        final ArrayList<String> myVillagelist = new ArrayList<String>();


        final boolean[] villageCheckedItems;


        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    BlockListValue villageList = new BlockListValue();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));

                    villageList.setVillageListDistrictCode(districtCode);
                    villageList.setVillageListBlockCode(blockCode);
                    villageList.setVillageListPvCode(pvCode);
                    villageList.setVillageListPvName(pvname);

                    Village.add(villageList);
                } while (VillageList.moveToNext());
            }
        }
        for (int i = 0; i < Village.size(); i++) {
            myVillagelist.add(Village.get(i).getVillageListPvName());
        }

        villagestrings = myVillagelist.toArray(new String[myVillagelist.size()]);
        villageCheckedItems = new boolean[villagestrings.length];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.village_dialog_title);
        mBuilder.setMultiChoiceItems(villagestrings, villageCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mVillageItems.contains(position)) {
                        mVillageItems.add(position);
                    }
                } else if (mVillageItems.contains(position)) {
                    mVillageItems.remove(position);
                }
//                if (isChecked) {
//                    mVillageItems.add(position);
//                } else {
//                    mVillageItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);
        final String[] finalvillageStrings = villagestrings;
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mVillageItems.size(); i++) {
                    item = item + finalvillageStrings[mVillageItems.get(i)];
                    if (i != mVillageItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                select_village_layout.setVisibility(View.VISIBLE);
                selected_village_tv.setText(item);
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < villageCheckedItems.length; i++) {
                    villageCheckedItems[i] = false;
                    mVillageItems.clear();
                    selected_village_tv.setText("");
                    select_village_layout.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void selectFinancialYear() {
        String fin_Year = "SELECT distinct(fin_year) FROM " + SCHEME_TABLE_NAME;
        String Fin_Year_DB = null;
        Cursor selectFinYearInDB = getRawEvents(fin_Year, null);
        if (selectFinYearInDB.moveToFirst()) {
            do {
                Fin_Year_DB = selectFinYearInDB.getString(0);
                Log.d("inspectionID", "" + Fin_Year_DB);
            } while (selectFinYearInDB.moveToNext());
        }
        if (!Fin_Year_DB.equalsIgnoreCase(String.valueOf(prefManager.getFinYearJson()))) {
            Utils.showAlert(this, "Data Not Available for this Financial Year! please turn on your Mobile Network");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view_finyear:
                loadOfflineFinYearListDBValues();
                break;
            case R.id.btn_view_block:
                loadOfflineBlockListDBValues();
                break;
            case R.id.btn_view_village:
                loadOfflineVillgeListDBValues(prefManager.getBlockCodeJson());
                break;
            case R.id.btn_view_scheme:
                clicked = true;
                loadOfflineSchemeListDBValues();
                break;
            case R.id.btn_download:
                if (Utils.isOnline()) {
                    getWorkListOptional();
                    getInspectionList_blockwise();
                    getInspectionList_Images_blockwise();
                    getAction_ForInspection();
                }
                else{
                    Utils.showAlert(this,getResources().getString(R.string.no_internet));
                }
                break;
            case R.id.backimg:
                onBackPress();
                break;
        }
    }

    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), schemeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getWorkListOptional() {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkListOptional", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), workListOptionalJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getInspectionList_blockwise() {
        try {
            new ApiService(this).makeJSONObjectRequest("InspectionListBlockWise", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), InspectionListBlockwiseJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getInspectionList_Images_blockwise() {
        try {
            new ApiService(this).makeJSONObjectRequest("InspectionListBlockWise_Images", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), InspectionListImageJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAction_ForInspection() {
        try {
            new ApiService(this).makeJSONObjectRequest("InspectionListBlockWise_Action", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), InspectionListActionJsonParams(), "not cache", this);
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

    public JSONObject workListOptionalJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.workListOptional(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkListOptional", "" + authKey);
        return dataSet;
    }

    public JSONObject InspectionListBlockwiseJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.InspectionListblockWise(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("InspectionList", "" + authKey);
        return dataSet;
    }

    public JSONObject InspectionListImageJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.InspectionList_Image(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("InspectionList_Image", "" + authKey);
        return dataSet;
    }

    public JSONObject InspectionListActionJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.InspectionList_Action(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("InspectionList_Action", "" + authKey);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {

            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadSchemeList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("schemeAll", "" + responseDecryptedSchemeKey);
            }
            if ("WorkListOptional".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListOptionalS(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, "No Projects Found!");
                }
                Log.d("responseWorkList", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }
            if ("InspectionListBlockWise".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Insert_inspectionList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    // Utils.showAlert(this, "No Record Found");
                }
                Log.d("InspectionListBlockWise", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }
            if ("InspectionListBlockWise_Images".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Insert_inspectionList_Images(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    // Utils.showAlert(this, "No Record Found");
                    Log.d("responseInspect_Action", jsonObject.getString("MESSAGE"));
                }
                Log.d("response_Images", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }
            if ("InspectionListBlockWise_Action".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Insert_inspectionList_Action(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    // Utils.showAlert(this, "No Record Found");
                    Log.d("responseInspect_Action", jsonObject.getString("MESSAGE"));
                }
                Log.d("responseInspect_Action", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadSchemeList(JSONArray jsonArray) {
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
            loadOfflineSchemeListDBValues();
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }

    public void loadOfflineSchemeListDBValues() {
        String query = "SELECT * FROM " + DBHelper.SCHEME_TABLE_NAME + " Where fin_year in " + prefManager.getFinYearJson().toString().replace("[", "(").replace("]", ")") + " order by fin_year";
        Cursor SchemeList = getRawEvents(query, null);
        Log.d("SchemeQuery", "" + query);

        Scheme.clear();


        if (SchemeList.getCount() > 0) {
            if (SchemeList.moveToFirst()) {
                do {
                    BlockListValue schemeList = new BlockListValue();
                    String schemeSequentialID = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_SEQUENTIAL_ID));
                    String schemeName = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_NAME));
                    String fin_year = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.FINANCIAL_YEAR));
                    schemeList.setSchemeSequentialID(schemeSequentialID);
                    schemeList.setSchemeName(schemeName);
                    schemeList.setFinancialYear(fin_year);
                    Scheme.add(schemeList);

                } while (SchemeList.moveToNext());
            }
        }
        for (int i = 0; i < Scheme.size(); i++) {
            mySchemelist.add(Scheme.get(i).getSchemeName());
        }
        if (clicked) {
            showMultipleSchemes(mySchemelist);
        }
    }

    public void showMultipleSchemes(ArrayList<String> mySchemelist) {
        clicked  = false;
        schemeStrings = mySchemelist.toArray(new String[mySchemelist.size()]);
        schemeCheckedItems = new boolean[schemeStrings.length];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.scheme_dialog_title);
        mBuilder.setMultiChoiceItems(schemeStrings, schemeCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mSchemeItems.contains(position)) {
                        mSchemeItems.add(position);
                    }
                } else if (mSchemeItems.contains(position)) {
                    mSchemeItems.remove(position);
                }
//                if (isChecked) {
//                    mVillageItems.add(position);
//                } else {
//                    mVillageItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);
        final String[] finalschemeStrings = schemeStrings;
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mSchemeItems.size(); i++) {
                    item = item + finalschemeStrings[mSchemeItems.get(i)];
                    if (i != mSchemeItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                select_scheme_layout.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                selected_scheme_tv.setText(item);
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < schemeCheckedItems.length; i++) {
                    schemeCheckedItems[i] = false;
                    mSchemeItems.clear();
                    selected_scheme_tv.setText("");
                    select_scheme_layout.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void workListOptionalS(JSONArray jsonArray) {
        try {
            db.delete(DBHelper.WORK_LIST_OPTIONAL, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String SelectedBlockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String schemeID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_ID);
                    String workGroupID = jsonArray.getJSONObject(i).getString(AppConstant.WORK_GROUP_ID);
                    String workTypeID = jsonArray.getJSONObject(i).getString(AppConstant.WORK_TYPE_ID);
                    String finYear = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);
                    String workID = jsonArray.getJSONObject(i).getString(AppConstant.WORK_ID);
                    String workName = jsonArray.getJSONObject(i).getString(AppConstant.WORK_NAME);
                    String asAmount = jsonArray.getJSONObject(i).getString(AppConstant.AS_AMOUNT);
                    String tsAmount = jsonArray.getJSONObject(i).getString(AppConstant.TS_AMOUNT);
                    String currentStage = jsonArray.getJSONObject(i).getString(AppConstant.CURRENT_STAGE);
                    String isHighValueProject = jsonArray.getJSONObject(i).getString(AppConstant.IS_HIGH_VALUE_PROJECT);
                    String pvCode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);

                    ContentValues workListOptional = new ContentValues();
                    workListOptional.put(AppConstant.DISTRICT_CODE, dcode);
                    workListOptional.put(AppConstant.BLOCK_CODE, SelectedBlockCode);
                    workListOptional.put(AppConstant.SCHEME_ID, schemeID);
                    workListOptional.put(AppConstant.WORK_GROUP_ID, workGroupID);
                    workListOptional.put(AppConstant.WORK_TYPE_ID, workTypeID);
                    workListOptional.put(AppConstant.FINANCIAL_YEAR, finYear);
                    workListOptional.put(AppConstant.WORK_ID, workID);
                    workListOptional.put(AppConstant.WORK_NAME, workName);
                    workListOptional.put(AppConstant.AS_AMOUNT, asAmount);
                    workListOptional.put(AppConstant.TS_AMOUNT, tsAmount);
                    workListOptional.put(AppConstant.CURRENT_STAGE, currentStage);
                    workListOptional.put(AppConstant.IS_HIGH_VALUE_PROJECT, isHighValueProject);
                    workListOptional.put(AppConstant.PV_CODE, pvCode);

                    LoginScreen.db.insert(DBHelper.WORK_LIST_OPTIONAL, null, workListOptional);
                }
            } else {
                Utils.showAlert(this, "No Record Found for Corrsponding Financial Year");
            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }

    }


    private void Insert_inspectionList(JSONArray jsonArray) {
        try {
            //db.rawQuery("DELETE FROM "+DBHelper.INSPECTION+" WHERE delete_flag =1",null);
            db.delete(DBHelper.INSPECTION, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String workID = jsonArray.getJSONObject(i).getString(AppConstant.WORK_ID);
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String stageOfWorkOnInspection = jsonArray.getJSONObject(i).getString(AppConstant.STAGE_OF_WORK_ON_INSPECTION);
                    String dateOfInspection = jsonArray.getJSONObject(i).getString(AppConstant.DATE_OF_INSPECTION);
                    String inspectedBy = jsonArray.getJSONObject(i).getString(AppConstant.INSPECTED_BY);
                    String observation = jsonArray.getJSONObject(i).getString(AppConstant.OBSERVATION);
                    String inspectionRemark = jsonArray.getJSONObject(i).getString(AppConstant.INSPECTION_REMARK);


                    ContentValues getInspectionList = new ContentValues();

                    getInspectionList.put(AppConstant.WORK_ID, workID);
                    getInspectionList.put("id", id);
                    getInspectionList.put(AppConstant.STAGE_OF_WORK_ON_INSPECTION, stageOfWorkOnInspection);
                    getInspectionList.put(AppConstant.DATE_OF_INSPECTION, dateOfInspection);
                    getInspectionList.put(AppConstant.INSPECTED_BY, inspectedBy);
                    getInspectionList.put(AppConstant.OBSERVATION, observation);
                    getInspectionList.put(AppConstant.INSPECTION_REMARK, inspectionRemark);
                    getInspectionList.put("delete_flag", 1);


                    LoginScreen.db.insert(DBHelper.INSPECTION, null, getInspectionList);
                }
            } else {
                Utils.showAlert(this, "No Record Found!");
            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }

    }


    private void Insert_inspectionList_Images(JSONArray jsonArray) {
        try {
            // db.delete(DBHelper.CAPTURED_PHOTO, null, null);
            db.execSQL(String.format("DELETE FROM "+DBHelper.CAPTURED_PHOTO+" WHERE pending_flag IS NULL OR trim(pending_flag) = '';", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String inspection_id = jsonArray.getJSONObject(i).getString(AppConstant.INSPECTION_ID);
                    String image = jsonArray.getJSONObject(i).getString(AppConstant.IMAGE);
                    String image_description = jsonArray.getJSONObject(i).getString("image_description");
                    String image_id = jsonArray.getJSONObject(i).getString(AppConstant.IMAGE_ID);


                    ContentValues Imageist = new ContentValues();
                    Imageist.put(AppConstant.IMAGE_ID, image_id);
                    Imageist.put(AppConstant.INSPECTION_ID, inspection_id);
                    Imageist.put(AppConstant.IMAGE, image);
                    Imageist.put(AppConstant.DESCRIPTION, image_description);

                    LoginScreen.db.insert(DBHelper.CAPTURED_PHOTO, null, Imageist);
                }
            } else {
                Utils.showAlert(this, "No Record Found");
            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
    }

    private void Insert_inspectionList_Action(JSONArray jsonArray) {
        try {
            db.execSQL(String.format("DELETE FROM " + DBHelper.INSPECTION_ACTION + " WHERE delete_flag=1;", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String workID = jsonArray.getJSONObject(i).getString(AppConstant.WORK_ID);
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String inspection_id = jsonArray.getJSONObject(i).getString(AppConstant.INSPECTION_ID);
                    String date_of_action = jsonArray.getJSONObject(i).getString(AppConstant.DATE_OF_ACTION);
                    String action_taken = jsonArray.getJSONObject(i).getString(AppConstant.ACTION_TAKEN);
                    String action_remark = jsonArray.getJSONObject(i).getString(AppConstant.ACTION_REMARK);
                    String dist_action = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_ACTION);
                    String state_action = jsonArray.getJSONObject(i).getString(AppConstant.STATE_ACTION);
                    String sub_div_action = jsonArray.getJSONObject(i).getString(AppConstant.SUB_DIV_ACTION);

                    ContentValues ActionList = new ContentValues();

                    ActionList.put(AppConstant.WORK_ID, workID);
                    //   ActionList.put("id", id);
                    ActionList.put(AppConstant.INSPECTION_ID, inspection_id);
                    ActionList.put(AppConstant.DATE_OF_ACTION, date_of_action);
                    ActionList.put(AppConstant.ACTION_TAKEN, action_taken);
                    ActionList.put(AppConstant.ACTION_REMARK, action_remark);
                    ActionList.put(AppConstant.DISTRICT_ACTION, dist_action);
                    ActionList.put(AppConstant.STATE_ACTION, state_action);
                    ActionList.put(AppConstant.SUB_DIV_ACTION, sub_div_action);
                    ActionList.put(AppConstant.DELETE_FLAG, "1");

                    LoginScreen.db.insert(DBHelper.INSPECTION_ACTION, null, ActionList);
                }
            } else {
                Utils.showAlert(this, "No Record Found!");
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

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    @Override
    public void onItemsSelected(boolean[] selected) {

    }
}
