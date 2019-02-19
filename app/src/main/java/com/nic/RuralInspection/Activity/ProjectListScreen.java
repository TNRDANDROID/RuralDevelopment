package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.android.volley.VolleyError;
import com.nic.RuralInspection.Adapter.ProjectListAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.api.Api;
import com.nic.RuralInspection.api.ServerResponse;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.util.ArrayList;
import java.util.List;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class ProjectListScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener, ProjectListAdapter.ProjectsAdapterListener {
    private RecyclerView recyclerView;
    private ArrayList<BlockListValue> projectListValues;
    private ProjectListAdapter mAdapter;
    private ImageView back_img;
    private NestedScrollView scrollView;
    private SearchView searchView;
    private MyCustomTextView district_tv, scheme_name_tv, block_name_tv, fin_year_tv, list_count, not_found_tv;
    PrefManager prefManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intializeUI();
        //  recycle();

    }

    public void intializeUI() {
        prefManager = new PrefManager(this);

        district_tv = (MyCustomTextView) findViewById(R.id.district_tv);
        scheme_name_tv = (MyCustomTextView) findViewById(R.id.scheme_name_tv);
        block_name_tv = (MyCustomTextView) findViewById(R.id.block_name_tv);
        fin_year_tv = (MyCustomTextView) findViewById(R.id.fin_year_tv);
        recyclerView = (RecyclerView) findViewById(R.id.project_list);
        not_found_tv = (MyCustomTextView) findViewById(R.id.not_found_tv);
        list_count = (MyCustomTextView) findViewById(R.id.count_list);
        projectListValues = new ArrayList<>();
        back_img = (ImageView) findViewById(R.id.backimg);
        back_img.setOnClickListener(this);

        district_tv.setText(prefManager.getDistrictName());
        scheme_name_tv.setText(prefManager.getSchemeName());
        block_name_tv.setText(prefManager.getBlockName());
        fin_year_tv.setText(prefManager.getFinancialyearName());

        mAdapter = new ProjectListAdapter(this, projectListValues, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(false);
        retrieve();
    }

    private void retrieve() {
        projectListValues.clear();
        String selectedBlock = getIntent().getStringExtra(AppConstant.BLOCK_CODE);
        String selectedVillage = getIntent().getStringExtra(AppConstant.PV_CODE);
        String selectedScheme = getIntent().getStringExtra(AppConstant.SCHEME_SEQUENTIAL_ID);
        String high_value = getIntent().getStringExtra(AppConstant.IS_HIGH_VALUE_PROJECT);

        String condition = "";

        if (selectedBlock != null || selectedVillage != null || selectedScheme != null || high_value != null) {
            condition += " where";

            if (high_value != null) {
                condition += " a.is_high_value = '" + high_value + "'";
            }
            if (selectedBlock != null) {
                if (high_value != null) {
                    condition += " and";
                }
                condition += " a.bcode = " + selectedBlock;

                if (selectedVillage != null) {
                    condition += " and a.pvcode = " + selectedVillage;
                }

            }
            if (selectedScheme != null) {
                if (high_value != null || selectedBlock != null) {
                    condition += " and";
                }
                condition += " a.scheme_id = " + selectedScheme;
            }

        }

        String worklist_sql = "select a.bcode as bcode,a.pvcode as pvcode,a.scheme_id as scheme_id,a.work_group_id as work_group_id,a.work_type_id as work_type_id,a.work_id as work_id,a.work_name as work_name,a.as_value as as_value,a.ts_value as ts_value,a.is_high_value as is_high_value,b.work_stage_code as work_stage_code,b.work_stage_order as work_stage_order,b.work_stage_name as  work_stage_name from (select * from " + DBHelper.WORK_LIST_OPTIONAL + " WHERE dcode = " + prefManager.getDistrictCode() + " AND fin_year = '" + prefManager.getFinancialyearName() + "')a left join (select * from " + DBHelper.WORK_STAGE_TABLE + ")b on a.work_group_id = b.work_group_id and a.work_type_id = b.work_type_id and a.current_stage_of_work=b.work_stage_code " + condition + "  order by b.work_stage_order ";
        Log.d("sql", worklist_sql);
        Cursor worklist = getRawEvents(worklist_sql, null);

        if (worklist.getCount() > 0) {
            if (worklist.moveToFirst()) {
                do {
                    String bcode = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvcode = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String scheme_id = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.SCHEME_ID));
                    String work_group_id = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.WORK_GROUP_ID));
                    String work_type_id = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.WORK_TYPE_ID));
                    String work_id = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.WORK_ID));
                    String work_name = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.WORK_NAME));
                    String as_value = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.AS_AMOUNT));
                    String is_high_value = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.IS_HIGH_VALUE_PROJECT));
                    String work_stage_code = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.WORK_STAGE_CODE));
                    String work_satge_order = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.WORK_STAGE_ORDER));
                    String work_stage_name = worklist.getString(worklist.getColumnIndexOrThrow(AppConstant.WORK_SATGE_NAME));

                    BlockListValue workListValue = new BlockListValue();
                    workListValue.setBlockCode(bcode);
                    workListValue.setPvCode(pvcode);
                    workListValue.setSchemeID(scheme_id);
                    workListValue.setWorkGroupID(work_group_id);
                    workListValue.setWorkTypeID(work_type_id);
                    workListValue.setWorkID(work_id);
                    workListValue.setWorkName(work_name);
                    workListValue.setAsAmount(as_value);
                    workListValue.setIsHighValue(is_high_value);
                    workListValue.setWorkStageCode(work_stage_code);
                    workListValue.setWorkStageOrder(work_satge_order);
                    workListValue.setWorkStageName(work_stage_name);
                    projectListValues.add(workListValue);

                } while (worklist.moveToNext());
            }
        }

        if (!(projectListValues.size() < 1)) {
            recyclerView.setAdapter(mAdapter);
            Log.d("size", String.valueOf(projectListValues.size()));
            list_count.setText(String.valueOf(projectListValues.size()));

        } else {
            list_count.setText("0");
            not_found_tv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backimg:
                //DashboardFragment.setViolation();
                onBackPress();
                break;

        }
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPress();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        }
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

//    @Override
//    public void setProjectList(BlockListValue projectList) {
//
//    }

    @Override
    public void addInspectionOnclick(View v, int position) {
        Log.d("pos", String.valueOf(position));
    }

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    private Cursor getRawEventhWere(String sql, String[] string) {
        Cursor cursor = LoginScreen.db.rawQuery(sql, string);
        return cursor;
    }

    public int getProjectlistSize() {
        return projectListValues.size();
    }
}
