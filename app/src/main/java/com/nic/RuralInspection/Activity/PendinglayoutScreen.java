package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nic.RuralInspection.Adapter.PendingLayoutAdapter;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.session.PrefManager;

/**
 * Created by NIC on 22-02-2019.
 */

public class PendinglayoutScreen extends AppCompatActivity implements View.OnClickListener {
    private PendingLayoutAdapter pendingLayoutAdapter;
    private RecyclerView pendingLayoutRecyclerView;
    private PrefManager prefManager;
    private ImageView back_img;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState );
        setContentView(R.layout.pending_layout_screen);
        intializeUI();
    }

    public void intializeUI(){
        prefManager = new PrefManager(this);
        pendingLayoutAdapter = new PendingLayoutAdapter();
        pendingLayoutRecyclerView = (RecyclerView)findViewById(R.id.pending_recycler_view);
        back_img = (ImageView) findViewById(R.id.backimg);
        back_img.setOnClickListener(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        pendingLayoutRecyclerView.setLayoutManager(mLayoutManager);
        pendingLayoutRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pendingLayoutRecyclerView.setHasFixedSize(true);
        pendingLayoutRecyclerView.setNestedScrollingEnabled(false);
        pendingLayoutRecyclerView.setFocusable(false);
        pendingLayoutRecyclerView.setAdapter(pendingLayoutAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backimg:
                onBackPress();
                break;
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
}
