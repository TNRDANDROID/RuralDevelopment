package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.Adapter.CommonAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
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
import static com.nic.RuralInspection.DataBase.DBHelper.BLOCK_TABLE_NAME;
import static com.nic.RuralInspection.DataBase.DBHelper.VILLAGE_TABLE_NAME;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class SelectBlockSchemeScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    private ImageView home;
    private Button done;
    private RadioGroup radioGroup;
    CheckBox all_block, all_village, all_scheme, high_value_projects, all_projects;
    private Spinner sp_block, sp_village, sp_scheme, sp_financialYear;
    private LinearLayout block_layout;
    String arr_block[], arr_scheme[], arr_financialYear[];
    private PrefManager prefManager;
    private JSONArray updatedJsonArray;
    private List<BlockListValue> Block = new ArrayList<>();
    private List<BlockListValue> Village = new ArrayList<>();
    private List<BlockListValue> Scheme = new ArrayList<>();
    private List<BlockListValue> FinYearList = new ArrayList<>();
    private ProgressHUD progressHUD;

    String pref_Block, pref_Village, pref_Scheme, pref_finYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_block_scheme);
        intializeUI();
        if (Utils.isOnline()) {
            db.delete(DBHelper.WORK_LIST_OPTIONAL, null, null);
        } else {
            //   Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }


    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        home = (ImageView) findViewById(R.id.home);
        done = (Button) findViewById(R.id.btn_save);
        high_value_projects = (CheckBox) findViewById(R.id.high_value_projects);
        all_projects = (CheckBox) findViewById(R.id.all_projects);
        sp_block = (Spinner) findViewById(R.id.block);
        sp_village = (Spinner) findViewById(R.id.village);
        sp_scheme = (Spinner) findViewById(R.id.scheme);
        sp_financialYear = (Spinner) findViewById(R.id.financialYear);
        all_block = (CheckBox) findViewById(R.id.all_block);
        all_village = (CheckBox) findViewById(R.id.all_village);
        all_scheme = (CheckBox) findViewById(R.id.all_scheme);
        block_layout = (LinearLayout) findViewById(R.id.block_layout);

        done.setOnClickListener(this);
        home.setOnClickListener(this);

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
        sp_block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    all_block.setChecked(true);
                    prefManager.setBlockName("All");
                } else {
                    all_block.setChecked(false);
                    pref_Block = Block.get(position).getBlockName();
                    prefManager.setBlockName(pref_Block);
                    prefManager.setKeySpinnerSelectedBlockcode(Block.get(position).getBlockCode());
                    villageFilterSpinner(Block.get(position).getBlockCode());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    all_village.setChecked(true);
                    prefManager.setVillageListPvName("All");
                } else {
                    all_village.setChecked(false);
                    pref_Village = Village.get(position).getVillageListPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setKeySpinnerSelectedPvcode(Village.get(position).getVillageListPvCode());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        all_block.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp_block.setSelection(0);
                    all_village.setChecked(true);
                    sp_block.setEnabled(false);
                    loadOfflineVillgeListDBValues();
                } else {
                    sp_block.setEnabled(true);
                }
            }
        });

        all_village.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp_village.setSelection(0);
                    sp_village.setEnabled(false);
                } else {
                    sp_village.setEnabled(true);
                }
            }
        });

        sp_scheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    all_scheme.setChecked(true);
                    prefManager.setSchemeName("All");
                } else {
                    all_scheme.setChecked(false);
                    pref_Scheme = Scheme.get(position).getSchemeName();
                    prefManager.setSchemeName(pref_Scheme);
                    prefManager.setKeySpinnerSelectedSchemeSeqId(Scheme.get(position).getSchemeID());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        all_scheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp_scheme.setSelection(0);
                    sp_scheme.setEnabled(false);
                } else {
                    sp_scheme.setEnabled(true);
                }
            }
        });
        sp_financialYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    pref_finYear = FinYearList.get(position).getFinancialYear();
                    prefManager.setFinancialyearName(pref_finYear);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadOfflineDBValues();
    }

    public void villageFilterSpinner(String filterVillage) {
        String villageSql = "SELECT * FROM " + VILLAGE_TABLE_NAME + " WHERE bcode = " + filterVillage;
        Log.d("villageSql", "" + villageSql);
        Cursor VillageList = getRawEvents(villageSql, null);
        Log.d("villagelistincur", "" + VillageList);
        Village.clear();
        BlockListValue villageListValue = new BlockListValue();
        villageListValue.setVillageListPvName("Select Village");
        Village.add(villageListValue);
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
                    Log.d("spinnersize", "" + Village.size());
                } while (VillageList.moveToNext());
            }
        }
        sp_village.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                dashboard();
                break;
            case R.id.btn_save:
                if (Utils.isOnline()) {
                    projectListScreen();
                } else {
                    projectListScreen_offline();
                }

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

//    public void projectListScreen() {
//
//        if (!"Select Financial year".equalsIgnoreCase(FinYearList.get(sp_financialYear.getSelectedItemPosition()).getFinancialYear())) {
//            if (!"Select Block".equalsIgnoreCase(Block.get(sp_block.getSelectedItemPosition()).getBlockName()) || (all_block.isChecked())) {
//                if (!"Select Village".equalsIgnoreCase(Village.get(sp_village.getSelectedItemPosition()).getVillageListPvName()) || (all_village.isChecked())) {
//                    if (!"Select Scheme".equalsIgnoreCase(Scheme.get(sp_scheme.getSelectedItemPosition()).getSchemeName()) || (all_scheme.isChecked())) {
//                        String blockCode = Block.get(sp_block.getSelectedItemPosition()).getBlockCode();
//                        String pvCode = Village.get(sp_village.getSelectedItemPosition()).getVillageListPvCode();
//                        String sequentialID = Scheme.get(sp_scheme.getSelectedItemPosition()).getSchemeSequentialID();
//
//                        String highValueProject = null;
//
//
//                        if (high_value_projects.isChecked()) {
//                            highValueProject = "Y";
//                        }
//                        //getWorkListOptional();
//
//                        Intent intent = new Intent(this, ProjectListScreen.class);
//                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra(AppConstant.BLOCK_CODE, blockCode);
//                        intent.putExtra(AppConstant.PV_CODE, pvCode);
//                        intent.putExtra(AppConstant.SCHEME_SEQUENTIAL_ID, sequentialID);
//                        intent.putExtra(AppConstant.IS_HIGH_VALUE_PROJECT, highValueProject);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//
//                    } else {
//                        Utils.showAlert(this, "Select Scheme");
//                    }
//                } else {
//                    Utils.showAlert(this, "Select Village");
//                }
//            } else {
//                Utils.showAlert(this, "Select Block");
//            }
//        } else {
//            Utils.showAlert(this, "Select Financial year");
//        }
//    }

    public void projectListScreen() {

        if (!"Select Financial year".equalsIgnoreCase(FinYearList.get(sp_financialYear.getSelectedItemPosition()).getFinancialYear())) {
            if (!"Select Block".equalsIgnoreCase(Block.get(sp_block.getSelectedItemPosition()).getBlockName()) || (all_block.isChecked())) {
                if (!"Select Village".equalsIgnoreCase(Village.get(sp_village.getSelectedItemPosition()).getVillageListPvName()) || (all_village.isChecked())) {
                    if (!"Select Scheme".equalsIgnoreCase(Scheme.get(sp_scheme.getSelectedItemPosition()).getSchemeName()) || (all_scheme.isChecked())) {
                        if(Utils.isOnline()) {
                            getWorkListOptional();
                        }
                        else {
                            goto_next();
                        }

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

    public void projectListScreen_offline() {

        Cursor worklist = getRawEvents("SELECT * FROM " + DBHelper.WORK_LIST_OPTIONAL, null);
        if (worklist.getCount() > 0) {
           projectListScreen();
           // goto_next();
        } else {
            Utils.showAlert(this, "Please TurnOn Your Network");
        }
    }
    public void goto_next() {

        String blockCode = Block.get(sp_block.getSelectedItemPosition()).getBlockCode();
        String pvCode = Village.get(sp_village.getSelectedItemPosition()).getVillageListPvCode();
        String sequentialID = Scheme.get(sp_scheme.getSelectedItemPosition()).getSchemeSequentialID();

        String highValueProject = null;


        if (high_value_projects.isChecked()) {
            highValueProject = "Y";
        }

        Intent intent = new Intent(this, ProjectListScreen.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstant.BLOCK_CODE, blockCode);
        intent.putExtra(AppConstant.PV_CODE, pvCode);
        intent.putExtra(AppConstant.SCHEME_SEQUENTIAL_ID, sequentialID);
        intent.putExtra(AppConstant.IS_HIGH_VALUE_PROJECT, highValueProject);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void loadOfflineDBValues() {
        if (prefManager.getLevels().equalsIgnoreCase("D")) {
            loadOfflineBlockListDBValues();
        } else {
            block_layout.setVisibility(View.GONE);
        }

        loadOfflineVillgeListDBValues();
        loadOfflineSchemeListDBValues();
        loadOfflineFinYearListDBValues();
    }

    public void loadOfflineBlockListDBValues() {

        Cursor BlockList = getRawEvents("SELECT * FROM " + BLOCK_TABLE_NAME, null);
        Block.clear();
        BlockListValue blockListValue = new BlockListValue();
        blockListValue.setBlockName("Select Block");
        Block.add(blockListValue);
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
        sp_block.setAdapter(new CommonAdapter(this, Block, "BlockList"));
    }

    public void loadOfflineVillgeListDBValues() {

        Cursor VillageList = getRawEvents("SELECT * FROM " + VILLAGE_TABLE_NAME, null);
        Village.clear();
        BlockListValue villageListValue = new BlockListValue();
        villageListValue.setVillageListPvName("Select Village");
        Village.add(villageListValue);
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
        sp_village.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }

    public void loadOfflineSchemeListDBValues() {

        Cursor SchemeList = getRawEvents("SELECT * FROM " + DBHelper.SCHEME_TABLE_NAME, null);

        Scheme.clear();
        BlockListValue schemeListValue = new BlockListValue();
        schemeListValue.setSchemeName("Select Scheme");
        Scheme.add(schemeListValue);
        if (SchemeList.getCount() > 0) {
            if (SchemeList.moveToFirst()) {
                do {
                    BlockListValue schemeList = new BlockListValue();
                    String schemeSequentialID = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_SEQUENTIAL_ID));
                    String schemeName = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_NAME));
                    schemeList.setSchemeSequentialID(schemeSequentialID);
                    schemeList.setSchemeName(schemeName);
                    Scheme.add(schemeList);

                } while (SchemeList.moveToNext());
            }
        }
        sp_scheme.setAdapter(new CommonAdapter(this, Scheme, "SchemeList"));
    }

    public void loadOfflineFinYearListDBValues() {

        Cursor FinYear = getRawEvents("SELECT fin_year FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);

        BlockListValue finYearListValue = new BlockListValue();
        finYearListValue.setFinancialYear("Select Financial year");
        FinYearList.add(finYearListValue);
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

        sp_financialYear.setAdapter(new CommonAdapter(this, FinYearList, "FinYearList"));
    }

    public void getWorkListOptional() {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkListOptional", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), workListOptionalJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject workListOptionalJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.workListOptional(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkListOptional", "" + authKey);
        return dataSet;
    }

    @Override
    public void onBackPressed() {
//        if (Utils.isOnline()) {
//            try{
//                db.delete(DBHelper.BLOCK_TABLE_NAME,null,null);
//                db.delete(DBHelper.VILLAGE_TABLE_NAME,null,null);
//                db.delete(DBHelper.SCHEME_TABLE_NAME,null,null);
//                db.delete(DBHelper.FINANCIAL_YEAR_TABLE_NAME,null ,null);
//                db.delete(DBHelper.WORK_STAGE_TABLE,null ,null);
//                db.delete(DBHelper.WORK_LIST_OPTIONAL,null,null);
//                db.delete(DBHelper.INSPECTION,null,null);
//                db.delete(DBHelper.CAPTURED_PHOTO,null,null);
//
//
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {

            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("WorkListOptional".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListOptionalS(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }else
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this,"No Record Found");
                }
                Log.d("responseWorkList", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }

        } catch (JSONException e) {

            e.printStackTrace();
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
            if(jsonArray.length() > 0) {
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
            }
            else {
                Utils.showAlert(this,"No Record Found for Corrsponding Financial Year");
            }

        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        goto_next();
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }
}
