package com.nic.RuralInspection.Activity;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nic.RuralInspection.Adapter.ImageDescriptionAdapter;
import com.nic.RuralInspection.Adapter.InspectionListAdapter;
import com.nic.RuralInspection.Adapter.ProjectListAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

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
    private MyCustomTextView district_tv, scheme_name_tv, block_name_tv, block_user_tv, village_name_tv, fin_year_tv;
    private MyCustomTextView projectName, amountTv, levelTv;
    private LinearLayout village_layout, block_layout;
    private ImageDescriptionAdapter imageAdapter;
    private InspectionListAdapter inspectionListAdapter;
    private RecyclerView imageRecyclerView, inspectionListRecyclerView;
    PrefManager prefManager;
    private ArrayList<BlockListValue> imagelistValues = new ArrayList<>();
    private ArrayList<BlockListValue> inspectionlistvalues = new ArrayList<>();



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
        village_layout = (LinearLayout) findViewById(R.id.village_layout);
        block_layout = (LinearLayout) findViewById(R.id.block_user_layout);
        block_user_tv = (MyCustomTextView) findViewById(R.id.block_user_tv);
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

        inspectionListAdapter = new InspectionListAdapter(this, inspectionlistvalues);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        inspectionListRecyclerView.setLayoutManager(mLayoutManager);
        inspectionListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        inspectionListRecyclerView.setHasFixedSize(true);
        inspectionListRecyclerView.setFocusable(false);
        inspectionListRecyclerView.setNestedScrollingEnabled(false);
        if (prefManager.getLevels().equalsIgnoreCase("B")) {
            village_layout.setVisibility(View.VISIBLE);
            village_name_tv.setText(prefManager.getVillageListPvName());
            block_layout.setVisibility(View.VISIBLE);
            block_user_tv.setText(prefManager.getBlockName());
        }
        // retrievedata();
        retrievedata_inspection();
        //  inspectionListRecyclerView.setAdapter(inspectionListAdapter);

    }

    private void retrievedata() {
        imagelistValues.clear();
        String workId = getIntent().getStringExtra(AppConstant.WORK_ID);

        String image_sql = "SELECT * FROM " + DBHelper.CAPTURED_PHOTO + " WHERE work_id = " + workId;
        Log.d("image_sql", image_sql);
        Cursor imageList = getRawEvents(image_sql, null);

        if (imageList.getCount() > 0) {
            if (imageList.moveToFirst()) {
                do {
                    String work_id = imageList.getString(imageList.getColumnIndexOrThrow(AppConstant.WORK_ID));
                    String latitude = imageList.getString(imageList.getColumnIndexOrThrow(AppConstant.LATITUDE));
                    String longitude = imageList.getString(imageList.getColumnIndexOrThrow(AppConstant.LONGITUDE));
                    String description = imageList.getString(imageList.getColumnIndexOrThrow(AppConstant.DESCRIPTION));

                    byte[] photo = imageList.getBlob(imageList.getColumnIndexOrThrow(AppConstant.IMAGE));
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
                    Bitmap image = BitmapFactory.decodeStream(imageStream);

                    //  byte[] image =  imageList.getBlob(imageList.getColumnIndexOrThrow(AppConstant.IMAGE));


                    BlockListValue imageValue = new BlockListValue();

                    imageValue.setWorkID(work_id);
                    imageValue.setLatitude(latitude);
                    imageValue.setLongitude(longitude);
                    imageValue.setDescription(description);
                    imageValue.setImage(image);

                    imagelistValues.add(imageValue);

                } while (imageList.moveToNext());
            }
        }
    }

    private void retrievedata_inspection() {
        inspectionlistvalues.clear();
        String workId = getIntent().getStringExtra(AppConstant.WORK_ID);

        // String inspection_sql = "select * from (select * from "+DBHelper.INSPECTION+" WHERE work_id="+workId+")a left join (select * from captured_photo)b on a.inspection_id=b.inspection_id and a.work_id=b.work_id group by a.inspection_id";
        String inspection_sql = "select * from(select * from INSPECTION WHERE inspection_id in (select inspection_id from captured_photo))a left join (select * from observation)b on a.observation = b.id where work_id ="+workId +" and a.delete_flag != 0";
        Log.d("inspection_sql", inspection_sql);
        Cursor inspectionList = getRawEvents(inspection_sql, null);

        if (inspectionList.getCount() > 0) {
            if (inspectionList.moveToFirst()) {
                do {
                    String work_id = inspectionList.getString(inspectionList.getColumnIndexOrThrow(AppConstant.WORK_ID));
                    String date_of_inspection = inspectionList.getString(inspectionList.getColumnIndexOrThrow(AppConstant.DATE_OF_INSPECTION));
                    String inspection_remark = inspectionList.getString(inspectionList.getColumnIndexOrThrow(AppConstant.INSPECTION_REMARK));
                    String observation = inspectionList.getString(inspectionList.getColumnIndexOrThrow(AppConstant.OBSERVATION));
                    int inspection_id = inspectionList.getInt(inspectionList.getColumnIndexOrThrow(AppConstant.INSPECTION_ID));

                    BlockListValue inspectionValue = new BlockListValue();
                    inspectionValue.setWorkID(work_id);
                    Log.d("inspectworkId",""+work_id);
                    inspectionValue.setDate_of_inspection(date_of_inspection);
                    inspectionValue.setInspection_remark(inspection_remark);
                    inspectionValue.setObservation(observation);
                    inspectionValue.setInspectionID(inspection_id);

                    inspectionlistvalues.add(inspectionValue);

                } while (inspectionList.moveToNext());
            }
        }

       // if ((!(inspectionlistvalues.size() < 1)) && (inspectionValue.getWorkID().equalsIgnoreCase(prefManager.getKeyActionWorkid()))) {
        if ((!(inspectionlistvalues.size() < 1))){
            inspectionListRecyclerView.setAdapter(inspectionListAdapter);
            Log.d("size", String.valueOf(inspectionlistvalues.size()));
        }
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

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }


}
