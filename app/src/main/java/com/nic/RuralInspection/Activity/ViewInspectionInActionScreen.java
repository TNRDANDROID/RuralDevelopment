package com.nic.RuralInspection.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nic.RuralInspection.Adapter.AddActionAdapter;
import com.nic.RuralInspection.Adapter.ImageDescriptionAdapter;
import com.nic.RuralInspection.Adapter.InspectionListAdapter;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIC on 21-02-2019.
 */

public class ViewInspectionInActionScreen extends AppCompatActivity implements View.OnClickListener {
    private ScrollView scrollView;
    private MyCustomTextView action_tv;
    private List<View> viewArrayList = new ArrayList<>();

    private Context context;

    final int CAMERA_REQUEST = 1888;

    ImageView imageView;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";

    private static String imageStoragePath;
    private ImageView back_img;
    private MyCustomTextView district_tv, scheme_name_tv, block_name_tv,block_user_tv, village_name_tv, fin_year_tv;
    private MyCustomTextView projectName, amountTv, levelTv;
    private LinearLayout village_layout,block_layout;
    private ImageDescriptionAdapter imageAdapter;
    private AddActionAdapter addActionAdapter;
    private RecyclerView imageRecyclerView, inspectionListRecyclerView;
    PrefManager prefManager;
    private ArrayList<BlockListValue> imagelistValues = new ArrayList<>();
    private ArrayList<BlockListValue> actionlistvalues = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_inspection_in_action_screen_with_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intializeUI();
    }
    public void intializeUI() {
        prefManager = new PrefManager(this);
        village_layout = (LinearLayout) findViewById(R.id.village_layout);
        block_layout = (LinearLayout)findViewById(R.id.block_user_layout);
        block_user_tv = (MyCustomTextView)findViewById(R.id.block_user_tv);
        district_tv = (MyCustomTextView) findViewById(R.id.district_tv);
        scheme_name_tv = (MyCustomTextView) findViewById(R.id.scheme_name_tv);
        block_name_tv = (MyCustomTextView) findViewById(R.id.block_name_tv);
        village_name_tv = (MyCustomTextView) findViewById(R.id.village_name_tv);
        fin_year_tv = (MyCustomTextView) findViewById(R.id.fin_year_tv);

        projectName = (MyCustomTextView) findViewById(R.id.project_title_tv);
        amountTv = (MyCustomTextView) findViewById(R.id.amount_tv);
        levelTv = (MyCustomTextView) findViewById(R.id.level_tv);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
//        action_tv = (MyCustomTextView) findViewById(R.id.action_tv);
        back_img = (ImageView) findViewById(R.id.backimg);
        inspectionListRecyclerView = (RecyclerView) findViewById(R.id.image_list_with_description);

        district_tv.setText(prefManager.getDistrictName());
        scheme_name_tv.setText(prefManager.getSchemeName());
        block_name_tv.setText(prefManager.getBlockName());
        fin_year_tv.setText(prefManager.getFinancialyearName());

        projectName.setText(getIntent().getStringExtra(AppConstant.WORK_NAME));
        amountTv.setText(getIntent().getStringExtra(AppConstant.AS_AMOUNT));
        levelTv.setText(getIntent().getStringExtra(AppConstant.WORK_SATGE_NAME));

        back_img.setOnClickListener(this);
//        action_tv.setOnClickListener(this);
        // imageAdapter = new ImageDescriptionAdapter(this,imagelistValues );

        addActionAdapter = new AddActionAdapter(this, actionlistvalues);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        inspectionListRecyclerView.setLayoutManager(mLayoutManager);
        inspectionListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        inspectionListRecyclerView.setHasFixedSize(true);
        inspectionListRecyclerView.setFocusable(false);
        inspectionListRecyclerView.setNestedScrollingEnabled(false);
        if(prefManager.getLevels().equalsIgnoreCase("B")){
            village_layout.setVisibility(View.VISIBLE);
            village_name_tv.setText(prefManager.getVillageListPvName());
            block_layout.setVisibility(View.VISIBLE);
            block_user_tv.setText(prefManager.getBlockName());
        }
        // retrievedata();
//        retrievedata_inspection();
        //  inspectionListRecyclerView.setAdapter(inspectionListAdapter);

    }
    @Override
    public void onClick(View v) {

    }
}
