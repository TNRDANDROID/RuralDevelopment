package com.nic.RuralInspection.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.Utils.CameraUtils;
import com.nic.RuralInspection.Utils.FontCache;
import com.nic.RuralInspection.Utils.Utils;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AchanthiSundar on 08-01-2019.
 */

public class ViewInspectionReportScreen extends AppCompatActivity implements View.OnClickListener {
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
    private MyCustomTextView district_tv,scheme_name_tv,block_name_tv,fin_year_tv;
    private MyCustomTextView projectName, amountTv, levelTv;
    PrefManager prefManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_inspection_report_with_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);

        district_tv = (MyCustomTextView) findViewById(R.id.district_tv);
        scheme_name_tv = (MyCustomTextView) findViewById(R.id.scheme_name_tv);
        block_name_tv = (MyCustomTextView) findViewById(R.id.block_name_tv);
        fin_year_tv = (MyCustomTextView) findViewById(R.id.fin_year_tv);

        projectName = (MyCustomTextView) findViewById(R.id.project_title_tv);
        amountTv = (MyCustomTextView) findViewById(R.id.amount_tv);
        levelTv = (MyCustomTextView) findViewById(R.id.level_tv);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        action_tv = (MyCustomTextView) findViewById(R.id.action_tv);
        back_img = (ImageView) findViewById(R.id.backimg);

        district_tv.setText(prefManager.getDistrictName());
        scheme_name_tv.setText(prefManager.getSchemeName());
        block_name_tv.setText(prefManager.getBlockName());
        fin_year_tv.setText(prefManager.getFinancialyearName());

        projectName.setText(getIntent().getStringExtra(AppConstant.WORK_NAME));
        amountTv.setText(getIntent().getStringExtra(AppConstant.AS_AMOUNT));
        levelTv.setText(getIntent().getStringExtra(AppConstant.WORK_SATGE_NAME));

        back_img.setOnClickListener(this);
        action_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_tv:
//                imageWithDescription(action_tv, "mobile", scrollView);
                break;
            case R.id.backimg:
                onBackPress();
                break;
        }
    }

    private final void focusOnView(final ScrollView your_scrollview, final MyCustomTextView your_EditBox) {
        your_scrollview.post(new Runnable() {
            @Override
            public void run() {
                your_scrollview.fullScroll(View.FOCUS_DOWN);
                //your_scrollview.scrollTo(0, your_EditBox.getY());
            }
        });
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        //Intent intent = new Intent(this, HomeScreenActivity.class);
        //startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

    }


}
