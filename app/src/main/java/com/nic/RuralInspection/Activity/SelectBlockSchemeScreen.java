package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.nic.RuralInspection.Adapter.MyAdapter;
import com.nic.RuralInspection.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class SelectBlockSchemeScreen extends AppCompatActivity implements View.OnClickListener {

    private ImageView home;
    private Button done;
    private RadioGroup radioGroup;
    CheckBox all_block, all_scheme, high_value_projects, all_projects;
    private Spinner sp_block, sp_scheme, sp_financialYear;
    String arr_block[], arr_scheme[], arr_financialYear[];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_block_scheme);
        intializeUI();

        // viewFinancialYear();

//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                CheckBox checkBox = (CheckBox) group.findViewById(checkedId);
////                boolean isChecked = radioButton.isChecked();
////                if(isChecked) {
//                if (checkBox.isChecked())
//                    if (null != checkBox) {
//                        Toast.makeText(SelectBlockSchemeScreen.this, checkBox.getText(), Toast.LENGTH_SHORT).show();
//                        checkBox.setChecked(true);
//                    }
//            }
//        });


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
        all_block.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp_block.setSelection(0);

                }


            }
        });
        all_scheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp_scheme.setSelection(0);
                }
            }
        });
//        String[] value={"aa","bb"};
        ArrayList<String> financial_years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 2007; i--) {
            financial_years.add(Integer.toString(i) + "-" + Integer.toString(i + 1));

        }
        sp_financialYear.setAdapter(new ArrayAdapter(SelectBlockSchemeScreen.this, R.layout.spinner_value, R.id.spinner_list_value, financial_years));

    }

    public void intializeUI() {
        home = (ImageView) findViewById(R.id.home);
        done = (Button) findViewById(R.id.btn_save);
        high_value_projects = (CheckBox) findViewById(R.id.high_value_projects);
        all_projects = (CheckBox) findViewById(R.id.all_projects);
        sp_block = (Spinner) findViewById(R.id.block);
        sp_scheme = (Spinner) findViewById(R.id.scheme);
        sp_financialYear = (Spinner) findViewById(R.id.financialYear);
        all_block = (CheckBox) findViewById(R.id.all_block);
        all_scheme = (CheckBox) findViewById(R.id.all_scheme);

        done.setOnClickListener(this);
        home.setOnClickListener(this);
        viewBlock();
        viewScheme();
        high_value_projects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all_projects.setChecked(false);
                    Toast.makeText(SelectBlockSchemeScreen.this, "high value projects", Toast.LENGTH_SHORT).show();
                }
                else{
                    all_projects.setChecked(true);
                }

            }
        });all_projects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    high_value_projects.setChecked(false);
                    Toast.makeText(SelectBlockSchemeScreen.this, "All projects", Toast.LENGTH_SHORT).show();
                }
                else{
                    high_value_projects.setChecked(true);
                }

            }
        });
        //radioGroup.clearCheck();
    }

    private void viewBlock() {
        JSONArray cursors = new JSONArray();
        cursors.put("Block1");
        cursors.put("Block2");
        cursors.put("Block2");
        cursors.put("Block2");
        cursors.put("Block2");
        arr_block = new String[cursors.length() + 1];
        arr_block[0] = "-- Select Block --";
        for (int i = 0; i <= cursors.length(); i++) {

        }

        sp_block.setAdapter(new MyAdapter(SelectBlockSchemeScreen.this, R.layout.spinner_value, arr_block));
    }

    private void viewScheme() {
        JSONArray cursors = new JSONArray();
        cursors.put("Scheme1");
        cursors.put("Scheme2");
        cursors.put("Scheme2");
        cursors.put("Scheme2");
        cursors.put("Scheme2");
        arr_scheme = new String[cursors.length() + 1];
        arr_scheme[0] = "-- Select Scheme --";

        sp_scheme.setAdapter(new MyAdapter(SelectBlockSchemeScreen.this, R.layout.spinner_value, arr_scheme));
    }

    private void viewFinancialYear() {

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

    public void saveDetails(){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
//    public cursor getRawEvents(String sql,String string){
//        Cursor cursor=db.rawQuery("","");
//        return cursor;
//    }

}
