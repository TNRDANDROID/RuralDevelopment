package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.Adapter.CommonAdapter;
import com.nic.RuralInspection.Adapter.MyAdapter;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.nic.RuralInspection.DataBase.DBHelper.BLOCK_TABLE_NAME;
import static com.nic.RuralInspection.DataBase.DBHelper.FINANCIAL_YEAR_TABLE_NAME;
import static com.nic.RuralInspection.DataBase.DBHelper.SCHEME_TABLE_NAME;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class SelectBlockSchemeScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    private ImageView home;
    private Button done;
    private RadioGroup radioGroup;
    CheckBox all_block, all_scheme, high_value_projects, all_projects;
    private Spinner sp_block, sp_scheme, sp_financialYear;
    private LinearLayout block_layout;
    String arr_block[], arr_scheme[], arr_financialYear[], arr_blockcode[];
    private PrefManager prefManager;
    private JSONArray updatedJsonArray;
    private List<BlockListValue> Block = new ArrayList<>();
    private List<BlockListValue> Scheme = new ArrayList<>();
    private List<BlockListValue> FinYearList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_block_scheme);
        if (Utils.isOnline()) {
            intializeUI();
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }


    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        home = (ImageView) findViewById(R.id.home);
        done = (Button) findViewById(R.id.btn_save);
        high_value_projects = (CheckBox) findViewById(R.id.high_value_projects);
        all_projects = (CheckBox) findViewById(R.id.all_projects);
        sp_block = (Spinner) findViewById(R.id.block);
        sp_scheme = (Spinner) findViewById(R.id.scheme);
        sp_financialYear = (Spinner) findViewById(R.id.financialYear);
        all_block = (CheckBox) findViewById(R.id.all_block);
        all_scheme = (CheckBox) findViewById(R.id.all_scheme);
        block_layout = (LinearLayout) findViewById(R.id.block_layout);

        done.setOnClickListener(this);
        home.setOnClickListener(this);

        high_value_projects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all_projects.setChecked(false);
                    Toast.makeText(SelectBlockSchemeScreen.this, "high value projects", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SelectBlockSchemeScreen.this, "All projects", Toast.LENGTH_SHORT).show();
                } else {
                    high_value_projects.setChecked(true);
                }

            }
        });
        //radioGroup.clearCheck();

        loadOnlineValues();
        loadOfflineDBValues();
//        fetchBlockSchemeFinYearValueInDB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                //DashboardFragment.setViolation();
                dashboard();
                break;
            case R.id.btn_save:
                if (Utils.isOnline()) {

                    fetchBlockSchemeFinYearValueInDB();
                } else {
                    Utils.showAlert(this, getResources().getString(R.string.no_internet));
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

    public void projectListScreen() {
        Intent intent = new Intent(this, ProjectListScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void loadOnlineValues() {
        if (prefManager.getLevels().equalsIgnoreCase("D")) {
            getBlockList();
        } else {
            block_layout.setVisibility(View.GONE);
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


    public void getWorkListDistrictFinYearWiseService() {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkListDistrictFinYearWise", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), workListDistrictFinYearWiseJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject workListDistrictFinYearWiseJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.workListDistrictFinYearWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkListDistFinYearWise", "" + authKey);
        return dataSet;
    }

    public void loadOfflineDBValues() {
        if (prefManager.getLevels().equalsIgnoreCase("D")) {
            loadOfflineBlockListDBValues();
        } else {
            block_layout.setVisibility(View.GONE);
        }

        loadOfflineSchemeListDBValues();
        loadOfflineFinYearListDBValues();
    }

    public void loadOfflineBlockListDBValues() {

        Cursor BlockList = getRawEvents("SELECT * FROM " + BLOCK_TABLE_NAME, null);

        arr_block = new String[BlockList.getCount() + 1];
        arr_block[0] = "Select Block";
        int i = 1;
        Block.clear();
        BlockListValue blockListValue = new BlockListValue();
        blockListValue.setBlockName("Select Block");
        Block.add(blockListValue);
        while (BlockList.moveToNext()) {
            BlockListValue blockList = new BlockListValue();
            String districtCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
            String blockCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
            String blockName = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_NAME));
            blockList.setDistictCode(districtCode);
            blockList.setBlockCode(blockCode);
            blockList.setBlockName(blockName);
            Block.add(blockList);
            arr_block[i] = blockList.getBlockName();
            i++;
        }
        sp_block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    all_block.setChecked(true);
                } else {
                    all_block.setChecked(false);
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
                    sp_block.setEnabled(false);
                } else {
                    sp_block.setEnabled(true);
                }
            }
        });

        sp_block.setAdapter(new MyAdapter(SelectBlockSchemeScreen.this, R.layout.spinner_value, arr_block));

    }

    public void loadOfflineSchemeListDBValues() {

        Cursor SchemeList = getRawEvents("SELECT * FROM " + DBHelper.SCHEME_TABLE_NAME, null);
        arr_scheme = new String[SchemeList.getCount() + 1];
        arr_scheme[0] = "Select Scheme";
        int i = 1;
        Scheme.clear();
        BlockListValue schemeListValue = new BlockListValue();
        schemeListValue.setSchemeName("Select Scheme");
        Scheme.add(schemeListValue);
        while (SchemeList.moveToNext()) {

            BlockListValue schemeList = new BlockListValue();
            String schemeSequentialID = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_SEQUENTIAL_ID));
            String schemeName = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_NAME));
            schemeList.setSchemeSequentialID(schemeSequentialID);
            schemeList.setSchemeName(schemeName);
            Block.add(schemeList);
            arr_scheme[i] = schemeList.getSchemeName();
            i++;
        }
        sp_scheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    all_scheme.setChecked(true);
                } else {
                    all_scheme.setChecked(false);
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
        sp_scheme.setAdapter(new MyAdapter(SelectBlockSchemeScreen.this, R.layout.spinner_value, arr_scheme));
    }

    public void loadOfflineFinYearListDBValues() {

        Cursor FinYear = getRawEvents("SELECT fin_year FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);
        StringBuffer stringBuffer = new StringBuffer();
        arr_financialYear = new String[FinYear.getCount() + 1];
        arr_financialYear[0] = "Select Financial year";
        int i = 1;
        BlockListValue finYearListValue = new BlockListValue();
        finYearListValue.setFinancialYear("Select Financial year");
        FinYearList.add(finYearListValue);
        while (FinYear.moveToNext()) {
            BlockListValue finyearList = new BlockListValue();
            String financialYear = FinYear.getString(FinYear.getColumnIndexOrThrow(AppConstant.FINANCIAL_YEAR));
            finyearList.setFinancialYear(financialYear);
            FinYearList.add(finyearList);
            arr_financialYear[i] = finyearList.getFinancialYear();
            Log.d("finyeardb", "" + finyearList);
            i++;
        }
        sp_financialYear.setAdapter(new MyAdapter(SelectBlockSchemeScreen.this, R.layout.spinner_value, arr_financialYear));
    }

    public void fetchBlockSchemeFinYearValueInDB() {
        if (!"Select Block".equalsIgnoreCase(Block.get(sp_block.getSelectedItemPosition()).getBlockName()) || (all_block.isChecked())) {
            if (!"Select Scheme".equalsIgnoreCase(Scheme.get(sp_scheme.getSelectedItemPosition()).getSchemeName() )||( all_scheme.isChecked())) {
                if (!"Select Financial year".equalsIgnoreCase(FinYearList.get(sp_financialYear.getSelectedItemPosition()).getFinancialYear())) {
                    String blockCode = Block.get(sp_block.getSelectedItemPosition()).getBlockCode();
                    String sequentialID = Scheme.get(sp_scheme.getSelectedItemPosition()).getSchemeSequentialID();;
                    String financialYear = FinYearList.get(sp_financialYear.getSelectedItemPosition()).getFinancialYear();
                    prefManager.setKeySpinnerSelectedBlockcode(blockCode);
                    prefManager.setKeySpinnerSelectedSchemeSeqId(sequentialID);
                    prefManager.setKeySpinnerSelectedFinyear(financialYear);
                    getWorkListDistrictFinYearWiseService();
                } else {
                    Utils.showAlert(this, "Select Financial year");
                }
            } else {
                Utils.showAlert(this, "Select Scheme");
            }
        } else {
            Utils.showAlert(this, "Select Block");
        }
//        String blockCode = BlockList.get(sp_block.getSelectedItemPosition()).getBlockCode();
//        String sequentialID = SchemeList.get(sp_scheme.getSelectedItemPosition()).getSchemeSequentialID();
//        String financialYear = FinYearList.get(sp_financialYear.getSelectedItemPosition()).getFinancialYear();
//        Cursor fetchBlocks = getRawEvents("SELECT * FROM " + BLOCK_TABLE_NAME + "WHERE bcode = " + blockCode,null);
//        Cursor fetchSchemeId = getRawEvents("SELECT * FROM " + SCHEME_TABLE_NAME + "WHERE scheme_seq_id = " + sequentialID,null);
//        Cursor fetchFinYear = getRawEvents("SELECT * FROM " + FINANCIAL_YEAR_TABLE_NAME + "WHERE fin_year = " + financialYear,null);
//        Log.d("spinnerdata", "" + sp_block.getSelectedItemPosition() + sequentialID + prefManager.getPvCode() + financialYear);

    }

    public void callworkListDistrictFinYearWise() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
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
                        loadOnlineBlockList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                    }
                    Log.d("BlockList", "" + responseDecryptedBlockKey);
                }

            } else {
                block_layout.setVisibility(View.GONE);
            }
            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadOnlineSchemeList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("schemeAll", "" + responseDecryptedSchemeKey);
            }
            if ("FinYearList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadOnlineFinYearList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("FinYear", "" + responseDecryptedSchemeKey);
            }
            if ("WorkListDistrictFinYearWise".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListDistFinYearWise(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("responseWorkList", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadOnlineBlockList(JSONArray jsonArray) {
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            Block.clear();
            BlockListValue blockListValue = new BlockListValue();
            blockListValue.setBlockName("Select Block");
            Block.add(blockListValue);
            for (int i = 0; i < jsonArray.length(); i++) {
                BlockListValue blockList = new BlockListValue();
                String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                String blockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                String blockName = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME);
                blockList.setDistictCode(districtCode);
                blockList.setBlockCode(blockCode);
                blockList.setBlockName(blockName);
                Block.add(blockList);
            }
            sp_block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        all_block.setChecked(true);
                    } else {
                        all_block.setChecked(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sp_block.setAdapter(new CommonAdapter(this, Block, "BlockList"));
            Log.d("blocklist", "" + updatedJsonArray);
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
    }

    private void loadOnlineSchemeList(JSONArray jsonArray) {
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            Scheme.clear();
            BlockListValue schemeListValue = new BlockListValue();
            schemeListValue.setSchemeName("Select Scheme");
            Scheme.add(schemeListValue);
            for (int i = 0; i < jsonArray.length(); i++) {
                BlockListValue schemeList = new BlockListValue();
                String schemeSequentialID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_SEQUENTIAL_ID);
                String schemeName = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_NAME);
                schemeList.setSchemeSequentialID(schemeSequentialID);
                schemeList.setSchemeName(schemeName);
                Scheme.add(schemeList);
            }
            sp_scheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        all_scheme.setChecked(true);
                    } else {
                        all_scheme.setChecked(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sp_scheme.setAdapter(new CommonAdapter(this, Scheme, "SchemeList"));
            Log.d("SchemeList", "" + updatedJsonArray);
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
    }

    private void loadOnlineFinYearList(JSONArray jsonArray) {
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            FinYearList.clear();
            BlockListValue finYearListValue = new BlockListValue();
            finYearListValue.setFinancialYear("Select Financial year");
            FinYearList.add(finYearListValue);
            for (int i = 0; i < jsonArray.length(); i++) {
                BlockListValue finyearList = new BlockListValue();
                String financialYear = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);
                finyearList.setFinancialYear(financialYear);
                FinYearList.add(finyearList);
            }
            sp_financialYear.setAdapter(new CommonAdapter(this, FinYearList, "FinYearList"));
            Log.d("FinYearList", "" + updatedJsonArray);
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
    }


    private void workListDistFinYearWise(JSONArray jsonArray) {
        try {
            updatedJsonArray = new JSONArray();
            updatedJsonArray = jsonArray;
            Block.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                BlockListValue blockList = new BlockListValue();
                String SelectedBlockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                String schemeID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_ID);
                String workID = jsonArray.getJSONObject(i).getString(AppConstant.WORD_ID);
                String workName = jsonArray.getJSONObject(i).getString(AppConstant.WORK_NAME);
                String asAmount = jsonArray.getJSONObject(i).getString(AppConstant.AS_AMOUNT);
                String tsAmount = jsonArray.getJSONObject(i).getString(AppConstant.TS_AMOUNT);
                String isHighValueProject = jsonArray.getJSONObject(i).getString(AppConstant.IS_HIGH_VALUE_PROJECT);

                ContentValues workListDistFinYear = new ContentValues();
                workListDistFinYear.put(AppConstant.BLOCK_CODE, SelectedBlockCode);
                workListDistFinYear.put(AppConstant.SCHEME_ID, schemeID);
                workListDistFinYear.put(AppConstant.WORD_ID, workID);
                workListDistFinYear.put(AppConstant.WORK_NAME, workName);
                workListDistFinYear.put(AppConstant.AS_AMOUNT, asAmount);
                workListDistFinYear.put(AppConstant.TS_AMOUNT, tsAmount);
                workListDistFinYear.put(AppConstant.IS_HIGH_VALUE_PROJECT, isHighValueProject);

                LoginScreen.db.insert(DBHelper.WORK_LIST_DISTRICT_FINYEAR_WISE, null, workListDistFinYear);


                blockList.setSelectedBlockCode(SelectedBlockCode);
                blockList.setSchemeID(schemeID);
                blockList.setWorkID(workID);
                blockList.setWorkName(workName);
                blockList.setAsAmount(asAmount);
                blockList.setTsAmount(tsAmount);
                blockList.setIsHighValue(isHighValueProject);
                Block.add(blockList);
                projectListScreen();
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

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = LoginScreen.db.rawQuery(sql, null);
        return cursor;
    }
}
