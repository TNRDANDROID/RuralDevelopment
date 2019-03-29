package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.DataBase.DBHelper;
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
import java.util.Calendar;
import java.util.List;

import static com.nic.RuralInspection.Activity.LoginScreen.db;
import static com.nic.RuralInspection.DataBase.DBHelper.BLOCK_TABLE_NAME;
import static com.nic.RuralInspection.DataBase.DBHelper.SCHEME_TABLE_NAME;

public class DownloadActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private Button done, btn_view_finyear, btn_view_block, btn_view_village, btn_view_scheme;


    public MyCustomTextView title_tv, selected_finyear_tv, selected_block_tv, selected_village_tv, selected_scheme_tv;
    public static MyCustomTextView start_date_tv, end_date_tv;
    private PrefManager prefManager;

    private ImageView back_img,homeimg;
    private List<BlockListValue> Block = new ArrayList<>();
    private List<BlockListValue> Village = new ArrayList<>();
    private List<BlockListValue> Scheme = new ArrayList<>();
    private List<BlockListValue> FinYearList = new ArrayList<>();
    public LinearLayout select_fin_year_layout, select_block_layout, select_village_layout, select_scheme_layout, block_hide_layout, download_values_inspection_layout, download_values_action_layout, start_date_layout, end_date_layout;
    private View view;
    final ArrayList<Integer> mVillageItems = new ArrayList<>();
    final ArrayList<Integer> mUserItems = new ArrayList<>();
    final ArrayList<Integer> mFinYearItems = new ArrayList<>();
    final ArrayList<Integer> mSchemeItems = new ArrayList<>();
//        final ArrayList<String> mySchemelist = new ArrayList<String>();

    /*It is Temporarly hide scheme is empty in the multiple choice dialog to unhide */
    String[] blockStrings;
    String[] blockCodeStrings;
    String[] villageStrings;
    String[] villageCodeStrings;
    String[] schemeStrings;
    String[] schemeCodeStrings;
    String[] finyearStrings;
    String[] errorSoon;
    boolean[] blockcheckedItems;
    boolean[] FinYearcheckedItems;
    boolean[] villageCheckedItems;
    boolean[] schemeCheckedItems;
    boolean workListInsert = false;
    boolean inspectionListInsert = false;
    boolean inspectionListImagesInsert = false;
    boolean inspectionListActionInsert = false;
    String pref_Block, pref_Village, pref_Scheme, pref_finYear;
    private ProgressHUD progressHUD;
    private JSONArray updatedJsonArray;
    boolean clicked = false;
    ArrayList<JSONArray> myVillageCodelist;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_parent_layout);
        intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        homeimg = (ImageView) findViewById(R.id.homeimg);
        select_fin_year_layout = (LinearLayout) findViewById(R.id.select_fin_year_layout);
        select_block_layout = (LinearLayout) findViewById(R.id.select_block_layout);
        select_village_layout = (LinearLayout) findViewById(R.id.select_village_layout);
        select_scheme_layout = (LinearLayout) findViewById(R.id.select_scheme_layout);
        block_hide_layout = (LinearLayout) findViewById(R.id.block_hide_layout);
        download_values_inspection_layout = (LinearLayout) findViewById(R.id.download_values_inspection_layout);
        download_values_action_layout = (LinearLayout) findViewById(R.id.download_values_action_layout);
        start_date_layout = (LinearLayout) findViewById(R.id.start_date_layout);
        end_date_layout = (LinearLayout) findViewById(R.id.end_date_layout);
        done = (Button) findViewById(R.id.btn_download);
        btn_view_finyear = (Button) findViewById(R.id.btn_view_finyear);
        btn_view_block = (Button) findViewById(R.id.btn_view_block);
        btn_view_village = (Button) findViewById(R.id.btn_view_village);
        btn_view_scheme = (Button) findViewById(R.id.btn_view_scheme);
        back_img = (ImageView) findViewById(R.id.backimg);
        title_tv = (MyCustomTextView) findViewById(R.id.title_tv);
        selected_finyear_tv = (MyCustomTextView) findViewById(R.id.selected_finyear_tv);
        selected_block_tv = (MyCustomTextView) findViewById(R.id.selected_block_tv);
        selected_village_tv = (MyCustomTextView) findViewById(R.id.selected_village_tv);
        selected_scheme_tv = (MyCustomTextView) findViewById(R.id.selected_scheme_tv);
        start_date_tv = (MyCustomTextView) findViewById(R.id.start_date_tv);
        end_date_tv = (MyCustomTextView) findViewById(R.id.end_date_tv);
        view = (View) findViewById(R.id.scheme_view);
        back_img.setOnClickListener(this);
        homeimg.setOnClickListener(this);
        done.setOnClickListener(this);
        title_tv.setText("Download");

        btn_view_finyear.setOnClickListener(this);
        btn_view_block.setOnClickListener(this);
        btn_view_village.setOnClickListener(this);
        btn_view_scheme.setOnClickListener(this);
        start_date_layout.setOnClickListener(this);
        end_date_layout.setOnClickListener(this);
        done.setOnClickListener(this);

//        home.setOnClickListener(this);
        if (prefManager.getLevels().equalsIgnoreCase("B")) {
            block_hide_layout.setVisibility(View.GONE);
            download_values_action_layout.setVisibility(View.VISIBLE);
            download_values_inspection_layout.setVisibility(View.GONE);
        } else {
            download_values_action_layout.setVisibility(View.GONE);
            download_values_inspection_layout.setVisibility(View.VISIBLE);
        }
        loadOfflineFinYearListDBValues();
        loadOfflineBlockListDBValues();
        if (prefManager.getLevels().equalsIgnoreCase("B")) {
            loadOfflineVillgeListDBValues();
        }

    }

    public void loadOfflineFinYearListDBValues() {
        Cursor FinYear = getRawEvents("SELECT fin_year FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);
        FinYearList.clear();
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
        String[] mStringArray = new String[myFinYearlist.size()];
        finyearStrings = myFinYearlist.toArray(mStringArray);
        FinYearcheckedItems = new boolean[finyearStrings.length];
    }
    public void finYearCheckbox(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.finyear_dialog_title);
        mBuilder.setMultiChoiceItems(finyearStrings, FinYearcheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mFinYearItems.contains(position)) {
                        mFinYearItems.add(position);

                    }
                } else if (mFinYearItems.contains(position)) {
                    mFinYearItems.remove(Integer.valueOf(position));
                }
                JSONArray finyearJsonArray = new JSONArray();

                for (int i = 0; i < mFinYearItems.size(); i++) {
                    finyearJsonArray.put(finyearStrings[mFinYearItems.get(i)]);
                    prefManager.setFinYearJson(finyearJsonArray);
                    Log.d("FinYearArray", "" + finyearJsonArray);
                }
            }
        });

        mBuilder.setCancelable(false);
        //   final String[] finalFinYearStrings = finyearStrings;
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";

                for (int i = 0; i < mFinYearItems.size(); i++) {
                    item = item + finyearStrings[mFinYearItems.get(i)];
                    if (i != mFinYearItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                if(mFinYearItems.size() > 0){
                    select_fin_year_layout.setVisibility(View.VISIBLE);
                }else {
                    select_fin_year_layout.setVisibility(View.GONE);
                }
                selected_finyear_tv.setText(item);
                if (Utils.isOnline()) {
                    try {
                        db.delete(DBHelper.SCHEME_TABLE_NAME, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }if(mFinYearItems.size() > 0) {
                        getSchemeList();
                    }
                } else {
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
        final ArrayList<String> myBlockList = new ArrayList<String>();
        final ArrayList<String> myBlockCodeList = new ArrayList<String>();
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
            myBlockList.add(Block.get(i).getBlockName());
            myBlockCodeList.add(Block.get(i).getBlockCode());
        }

        blockStrings = myBlockList.toArray(new String[myBlockList.size()]);
        blockCodeStrings = myBlockCodeList.toArray(new String[myBlockCodeList.size()]);
        blockcheckedItems= new boolean[blockStrings.length];

    }

    public void blockCheckbox() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.block_dialog_title);
        mBuilder.setMultiChoiceItems(blockStrings, blockcheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {


                    if (!mUserItems.contains(position)) {
                        mUserItems.add(position);
                    }
                } else if (mUserItems.contains(position)) {
                    mUserItems.remove(Integer.valueOf(position));
                }
                JSONArray blockCodeJsonArray = new JSONArray();

                for (int i = 0; i < mUserItems.size(); i++) {
                    blockCodeJsonArray.put(blockCodeStrings[mUserItems.get(i)]);
                }
                prefManager.setBlockCodeJson(blockCodeJsonArray);
                Log.d("blockcode", "" + blockCodeJsonArray);

                loadOfflineVillgeListDBValues();

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
                if(mUserItems.size() > 0) {
                    select_block_layout.setVisibility(View.VISIBLE);
                }else {
                    select_block_layout.setVisibility(View.GONE);
                }
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
                for (int i = 0; i < blockcheckedItems.length; i++) {
                    blockcheckedItems[i] = false;
                    mUserItems.clear();
                    selected_block_tv.setText("");
                    select_block_layout.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void loadOfflineVillgeListDBValues() {

//        String villageSql = "SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " WHERE bcode in" + filterVillage.toString().replace("[", "(").replace("]", ")") + " order by bcode";
        String villageSql = null;

        if (prefManager.getLevels().equalsIgnoreCase("D")){
            JSONArray filterVillage = prefManager.getBlockCodeJson();
            villageSql = "SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " WHERE bcode in" + filterVillage.toString().replace("[", "(").replace("]", ")") + " order by bcode";
        }
        else if (prefManager.getLevels().equalsIgnoreCase("B")){
            String filterVillage = prefManager.getBlockCode();
            villageSql = "SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " WHERE bcode ="+filterVillage;
        }

        Log.d("villageSql", "" + villageSql);
        Cursor VillageList = getRawEvents(villageSql, null);
        Village.clear();
        final ArrayList<String> myVillageList = new ArrayList<String>();
         myVillageCodelist = new ArrayList<JSONArray>();



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
            myVillageList.add(Village.get(i).getVillageListPvName());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(Village.get(i).getVillageListBlockCode());
            jsonArray.put(Village.get(i).getVillageListPvCode());

            myVillageCodelist.add(jsonArray);
        }

        villageStrings = myVillageList.toArray(new String[myVillageList.size()]);
       // villageCodeStrings = myVillageCodelist.toArray(new String[myVillageCodelist.size()]);
        villageCheckedItems = new boolean[villageStrings.length];
    }

    public void villageCheckbox() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.village_dialog_title);
        mBuilder.setMultiChoiceItems(villageStrings, villageCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mVillageItems.contains(position)) {
                        mVillageItems.add(position);
                    }
                } else if (mVillageItems.contains(position)) {
                    mVillageItems.remove((Integer.valueOf(position)));
                }
                JSONArray villageCodeJsonArray = new JSONArray();

                for (int i = 0; i < mVillageItems.size(); i++) {
                    villageCodeJsonArray.put(myVillageCodelist.get(mVillageItems.get(i)));
                }
                prefManager.setVillagePvCodeJson(villageCodeJsonArray);
                Log.d("villagecode", "" + villageCodeJsonArray);
//                if (isChecked) {
//                    mVillageItems.add(position);
//                } else {
//                    mVillageItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mVillageItems.size(); i++) {
                    item = item + villageStrings[mVillageItems.get(i)];
                    if (i != mVillageItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                if(mVillageItems.size() > 0) {
                    select_village_layout.setVisibility(View.VISIBLE);
                }else {
                    select_village_layout.setVisibility(View.GONE);
                }
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
        if(mUserItems.size() > 0 || prefManager.getLevels().equalsIgnoreCase("B")) {/*Used for Block level Login*/
            mDialog.show();
        }
        else {
            Utils.showAlert(this,"Please Select Block!");
            select_village_layout.setVisibility(View.GONE);
        }

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
                finYearCheckbox();
                break;
            case R.id.btn_view_block:
                blockCheckbox();
                break;
            case R.id.btn_view_village:
                villageCheckbox();;
                break;
            case R.id.btn_view_scheme:
                schemeCheckbox();
                break;
            case R.id.btn_download:
                download();
                break;
            case R.id.backimg:
                onBackPress();
                break;
            case R.id.start_date_layout:
                showStartDatePickerDialog();
                break;
            case R.id.end_date_layout:
                showEndDatePickerDialog();
                break;
            case R.id.homeimg:
                dashboard();
                break;
        }
    }

    public void download() {
        if (Utils.isOnline()) {
            if (!prefManager.getLevels().equalsIgnoreCase("B")) {
                projectListScreenDistrictUser();
            } else {
                projectListScreenBlockUser();
            }
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }
    }

    public void projectListScreenDistrictUser() {
        if (!selected_finyear_tv.getText().equals("")) {
            if (!selected_block_tv.getText().equals("")) {
                if (!selected_village_tv.getText().equals("")) {
                    if (!selected_scheme_tv.getText().equals("")) {
                        getWorkListOptional();
                        getInspectionList_blockwise();
                        getInspectionList_Images_blockwise();
                        getAction_ForInspection();
                    } else {
                        Utils.showAlert(this, "Select Scheme");
                    }
                } else {
                    Utils.showAlert(this, "Select Village");
                }
            } else {
                Utils.showAlert(this, "Select Block");
            }
        } else {
            Utils.showAlert(this, "Select Financial year");
        }
    }

    public void projectListScreenBlockUser() {
        if (!start_date_tv.getText().equals("")) {
            if (!end_date_tv.getText().equals("")) {
                    getWorkListOptional();
                    getInspectionList_blockwise();
                    getInspectionList_Images_blockwise();
                    getAction_ForInspection();

            } else {
                Utils.showAlert(this, "Select To Date");
            }
        } else {
            Utils.showAlert(this, "Select From Date");
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
               // Log.d("schemeAll", "" + responseDecryptedSchemeKey);
//                int maxLogSize = 1000;
//                        for(int i = 0; i <= responseDecryptedSchemeKey.length() / maxLogSize; i++) {
//                            int start = i * maxLogSize;
//                            int end = (i+1) * maxLogSize;
//                            end = end > responseDecryptedSchemeKey.length() ? responseDecryptedSchemeKey.length() : end;
//                            Log.v("schemeAll", responseDecryptedSchemeKey.substring(start, end));
//                     }
            }
            if ("WorkListOptional".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListOptionalS(jsonObject.getJSONArray(AppConstant.JSON_DATA));
//                    Utils.showAlert(this, "Your Data will be Downloaded");
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    workListInsert = false;
                    Utils.showAlert(this, "No Projects Found! for your selected items");
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
        final ArrayList<String> mySchemelist = new ArrayList<>();
        final ArrayList<String> mySchemeCodelist = new ArrayList<>();


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
            mySchemeCodelist.add(Scheme.get(i).getSchemeSequentialID());
        }
        schemeStrings = mySchemelist.toArray(new String[mySchemelist.size()]);
        schemeCodeStrings = mySchemeCodelist.toArray(new String[mySchemeCodelist.size()]);
        schemeCheckedItems = new boolean[schemeStrings.length];
    }

    public void schemeCheckbox() {

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
                    mSchemeItems.remove((Integer.valueOf(position)));
                }
                JSONArray SchemeSeqIdJsonArray = new JSONArray();

                for (int i = 0; i < mSchemeItems.size(); i++) {
                    SchemeSeqIdJsonArray.put(schemeCodeStrings[mSchemeItems.get(i)]);
                }
                prefManager.setSchemeSeqIdJson(SchemeSeqIdJsonArray);
                Log.d("schemeSeqId", "" + SchemeSeqIdJsonArray);

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
                if(mSchemeItems.size() > 0) {
                    select_scheme_layout.setVisibility(View.VISIBLE);
                }else {
                    select_scheme_layout.setVisibility(View.GONE);
                }
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
        if(mFinYearItems.size() > 0) {
            mDialog.show();
        }
        else {
            Utils.showAlert(this,"Please Select Financial Year!");
            select_scheme_layout.setVisibility(View.GONE);
        }

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
                workListInsert = true;
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
                callAlert();

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
            if (jsonArray.length() > 0) {
                inspectionListInsert = true;
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
            db.execSQL(String.format("DELETE FROM " + DBHelper.CAPTURED_PHOTO + " WHERE pending_flag IS NULL OR trim(pending_flag) = '';", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            if (jsonArray.length() > 0) {
                inspectionListImagesInsert = true;
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
                inspectionListActionInsert = true;
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

    public void callAlert() {
        if (workListInsert) {
            Utils.showAlert(this, "Your Data Will be Downloaded Sucessfully!");
            workListInsert = false;
            inspectionListInsert = false;
            inspectionListImagesInsert = false;
            inspectionListActionInsert = false;
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

    public void dashboard() {
        Intent intent = new Intent(this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void showStartDatePickerDialog() {
        DialogFragment newFragment = new fromDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void showEndDatePickerDialog() {
        DialogFragment newFragment = new toDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class fromDatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        static Calendar cldr = Calendar.getInstance();
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog;
            datePickerDialog = new DatePickerDialog(getActivity(), this, year,
                    month, day);
            cldr.set(year,month,day);
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the date chosen by the user
            start_date_tv.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            cldr.set(Calendar.YEAR, year);
            cldr.set(Calendar.MONTH, (monthOfYear));
            cldr.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Log.d("startdate", "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }

    }

    public static class toDatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            String getfromdate = start_date_tv.getText().toString().trim();
            String getfrom[] = getfromdate.split("-");
            int year, month, day;
            year = Integer.parseInt(getfrom[2]);
            month = Integer.parseInt(getfrom[1]);
            day = Integer.parseInt(getfrom[0]);
            Calendar c = Calendar.getInstance();
            c.set(year, (month - 1), day + 1);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, (month - 1), day);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            end_date_tv.setText(day + "-" + (month + 1) + "-" + year);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, (month - 1));
            c.set(Calendar.DAY_OF_MONTH, (day + 1));
        }
    }
}
