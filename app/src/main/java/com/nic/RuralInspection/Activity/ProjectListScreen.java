package com.nic.RuralInspection.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nic.RuralInspection.Adapter.ProjectListAdapter;
import com.nic.RuralInspection.R;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class ProjectListScreen extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ProjectListAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list_activity);

        intializeUI();


    }

    public void intializeUI() {
        recyclerView = (RecyclerView) findViewById(R.id.project_list);
        mAdapter = new ProjectListAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {

    }
}
