package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nic.RuralInspection.R;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class SelectBlockSchemeScreen extends AppCompatActivity implements View.OnClickListener {

    private ImageView home;
    private Button done;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_block_scheme);
        intializeUI();
    }

    public void intializeUI() {
        home = (ImageView) findViewById(R.id.home);
        done = (Button) findViewById(R.id.btn_save);
        done.setOnClickListener(this);
        home.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                //DashboardFragment.setViolation();
                dashboard();
                break;
            case R.id.btn_save:
                projectListScreen();
        }
    }

    public void dashboard() {
        Intent intent = new Intent( this,Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void projectListScreen() {
        Intent intent = new Intent( this,ProjectListScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
