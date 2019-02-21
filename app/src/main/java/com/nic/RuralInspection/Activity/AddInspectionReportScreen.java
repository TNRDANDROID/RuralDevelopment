package com.nic.RuralInspection.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.RuralInspection.Adapter.CommonAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.Support.MyLocationListener;
import com.nic.RuralInspection.Utils.CameraUtils;
import com.nic.RuralInspection.Utils.FontCache;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static com.nic.RuralInspection.Activity.LoginScreen.db;

/**
 * Created by NIC on 23-01-2019.
 */

public class AddInspectionReportScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    private ScrollView scrollView;
    private MyCustomTextView take_photo;
    private MyCustomTextView district_tv, scheme_name_tv, block_name_tv, fin_year_tv;
    private List<View> viewArrayList = new ArrayList<>();
    PrefManager prefManager;

    private Context context;

    final int CAMERA_REQUEST = 1888;

    ImageView imageView, image_view_preview;
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
    private MyCustomTextView projectName, amountTv, levelTv;
    private Spinner sp_observation, sp_stage;
    private List<BlockListValue> stageListValues = new ArrayList<>();
    private List<BlockListValue> observationList = new ArrayList<>();
    private ImageView back_img;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int PERMISSION_REQUEST_CODE = 200;

    static ArrayList<String> latitude = new ArrayList<String>();
    static ArrayList<String> longitude = new ArrayList<String>();
    String offlatTextValue, offlanTextValue;
    String work_id;
    EditText remarkTv;
    static JSONObject dataset;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_inspection_with_toolbar);
        intializeUI();
        viewStage();
        viewObservation();
    }

    public void intializeUI() {
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        take_photo = (MyCustomTextView) findViewById(R.id.take_photo);
        prefManager = new PrefManager(this);

        district_tv = (MyCustomTextView) findViewById(R.id.district_tv);
        scheme_name_tv = (MyCustomTextView) findViewById(R.id.scheme_name_tv);
        block_name_tv = (MyCustomTextView) findViewById(R.id.block_name_tv);
        fin_year_tv = (MyCustomTextView) findViewById(R.id.fin_year_tv);

        projectName = (MyCustomTextView) findViewById(R.id.project_title_tv);
        amountTv = (MyCustomTextView) findViewById(R.id.amount_tv);
        levelTv = (MyCustomTextView) findViewById(R.id.level_tv);
        sp_observation = (Spinner) findViewById(R.id.observation);
        sp_stage = (Spinner) findViewById(R.id.stageSelect);
        remarkTv = (EditText) findViewById(R.id.remark) ;

        back_img = (ImageView) findViewById(R.id.backimg);
        back_img.setOnClickListener(this);

        take_photo.setOnClickListener(this);

        district_tv.setText(prefManager.getDistrictName());
        scheme_name_tv.setText(prefManager.getSchemeName());
        block_name_tv.setText(prefManager.getBlockName());
        fin_year_tv.setText(prefManager.getFinancialyearName());

        projectName.setText(getIntent().getStringExtra(AppConstant.WORK_NAME));
        amountTv.setText(getIntent().getStringExtra(AppConstant.AS_AMOUNT));
        levelTv.setText(getIntent().getStringExtra(AppConstant.WORK_SATGE_NAME));
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                if (!"Select Stage of Work".equalsIgnoreCase(stageListValues.get(sp_stage.getSelectedItemPosition()).getWorkStageName())) {
                    if (!"Select Observation".equalsIgnoreCase(sp_observation.getSelectedItem().toString())) {
                        imageWithDescription(take_photo, "mobile", scrollView);
                    } else {
                        Utils.showAlert(this, "Select Observation");
                    }
                } else {
                    Utils.showAlert(this, "Select Stage of Work");
                }

                break;
            case R.id.backimg:
                onBackPress();
                break;
        }
    }

    public void viewStage() {
        stageListValues.clear();
        String workGroupId = getIntent().getStringExtra(AppConstant.WORK_GROUP_ID);
        String workTypeid = getIntent().getStringExtra(AppConstant.WORK_TYPE_ID);

        String sql = "select * from " + DBHelper.WORK_STAGE_TABLE + "  where (work_group_id = " + workGroupId + " and work_type_id = " + workTypeid + ") order by work_stage_order asc";
        Cursor stages = getRawEvents(sql, null);
        Log.d("work_stage_sql",sql);

        BlockListValue stagelist = new BlockListValue();
        stagelist.setWorkStageName("Select Stage of Work");
        stageListValues.add(stagelist);

        if (stages.getCount() > 0) {
            if (stages.moveToFirst()) {
                do {
                    BlockListValue stagelistval = new BlockListValue();
                    String workGroupID = stages.getString(stages.getColumnIndexOrThrow(AppConstant.WORK_GROUP_ID));
                    String workTypeId = stages.getString(stages.getColumnIndexOrThrow(AppConstant.WORK_TYPE_ID));
                    String workStageOrder = stages.getString(stages.getColumnIndexOrThrow(AppConstant.WORK_STAGE_ORDER));
                    String workStageCode = stages.getString(stages.getColumnIndexOrThrow(AppConstant.WORK_STAGE_CODE));
                    String workstageName = stages.getString(stages.getColumnIndexOrThrow(AppConstant.WORK_SATGE_NAME));
                    stagelistval.setWorkGroupID(workGroupID);
                    stagelistval.setWorkTypeID(workTypeId);
                    stagelistval.setWorkStageOrder(workStageOrder);
                    stagelistval.setWorkStageCode(workStageCode);
                    stagelistval.setWorkStageName(workstageName);
                    stageListValues.add(stagelistval);
                } while (stages.moveToNext());
            }
        }
        sp_stage.setAdapter(new CommonAdapter(this, stageListValues, "StageList"));
    }

    public void viewObservation() {
        observationList.clear();
        String sql = "select * from "+DBHelper.OBSERVATION_TABLE;
        Cursor observation = getRawEvents(sql, null);

        BlockListValue observationValue = new BlockListValue();
        observationValue.setObservationName("Select Observation");
        observationList.add(observationValue);

        if (observation.getCount() > 0) {
            if (observation.moveToFirst()) {
                do {
                    BlockListValue observationlistval = new BlockListValue();
                    int obseravtionID = observation.getInt(observation.getColumnIndexOrThrow(AppConstant.OBSERVATION_ID));
                    String observationName = observation.getString(observation.getColumnIndexOrThrow(AppConstant.OBSERVATION_NAME));

                    observationlistval.setObservationID(obseravtionID);
                    observationlistval.setObservationName(observationName);

                    observationList.add(observationlistval);
                } while (observation.moveToNext());
            }
        }

        sp_observation.setAdapter(new CommonAdapter(this, observationList,"ObservationList"));

    }

    public void imageWithDescription(final MyCustomTextView action_tv, final String type, final ScrollView scrollView) {
        dataset = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        work_id = getIntent().getStringExtra(AppConstant.WORK_ID);
        String stage_of_work_on_inspection = stageListValues.get(sp_stage.getSelectedItemPosition()).getWorkStageCode();
        String date_of_inspection = sdf.format(new Date());
        String inspected_by = "inspected_by";
        int observation = observationList.get(sp_observation.getSelectedItemPosition()).getObservationID();
        String inspection_remark = remarkTv.getText().toString();
        String created_date = date_of_inspection;
        String created_ipaddress = "123214124";
        String created_username = "test";

        if(!Utils.isOnline()) {
            ContentValues inspectionValue = new ContentValues();
            inspectionValue.put(AppConstant.WORK_ID,work_id);
            inspectionValue.put(AppConstant.STAGE_OF_WORK_ON_INSPECTION,stage_of_work_on_inspection);
            inspectionValue.put(AppConstant.DATE_OF_INSPECTION,date_of_inspection);
          //  inspectionValue.put(AppConstant.INSPECTED_BY,inspected_by);
            inspectionValue.put(AppConstant.OBSERVATION,observation);
            inspectionValue.put(AppConstant.INSPECTION_REMARK,inspection_remark);
            inspectionValue.put(AppConstant.CREATED_DATE,created_date);
            inspectionValue.put(AppConstant.CREATED_IP_ADDRESS,prefManager.getIMEI());
            inspectionValue.put(AppConstant.CREATED_USER_NAME,prefManager.getUserName());

            LoginScreen.db.insert(DBHelper.INSPECTION,null,inspectionValue);
        } else {
            try {
                dataset.put(AppConstant.KEY_SERVICE_ID, "high_value_project_inspection_save");
                dataset.put(AppConstant.WORK_ID,work_id);
                dataset.put(AppConstant.STAGE_OF_WORK_ON_INSPECTION,stage_of_work_on_inspection);
                dataset.put(AppConstant.DATE_OF_INSPECTION,date_of_inspection);
                dataset.put(AppConstant.INSPECTED_BY,inspected_by);
                dataset.put(AppConstant.OBSERVATION,observation);
                dataset.put(AppConstant.INSPECTION_REMARK,inspection_remark);
                dataset.put(AppConstant.CREATED_DATE,created_date);
                dataset.put(AppConstant.CREATED_IP_ADDRESS,prefManager.getIMEI());
                dataset.put(AppConstant.CREATED_USER_NAME,prefManager.getUserName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        final Dialog dialog = new Dialog(this,
                R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_photo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();


        final LinearLayout mobileNumberLayout = (LinearLayout) dialog.findViewById(R.id.mobile_number_layout);

        Button done = (Button) dialog.findViewById(R.id.btn_save);
        done.setGravity(Gravity.CENTER);
        done.setVisibility(View.VISIBLE);
        done.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.HEAVY));

        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int inspectionID = 0;
                JSONArray imageJson = new JSONArray();

                Cursor inpection_Cursor = getRawEvents("SELECT MAX(inspection_id) FROM "+DBHelper.INSPECTION ,null);
                Log.d("cursor_count", String.valueOf(inpection_Cursor.getCount()));
                if (inpection_Cursor.getCount() > 0) {
                    if (inpection_Cursor.moveToFirst()) {
                        do {
                            inspectionID = inpection_Cursor.getInt(0);
                            Log.d("inspectionID", "" + inspectionID);
                        } while (inpection_Cursor.moveToNext());
                    }
                }
                int childCount = mobileNumberLayout.getChildCount();
                if(childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        JSONArray imageArray = new JSONArray();

                        View vv = mobileNumberLayout.getChildAt(i);
                        EditText myEditTextView = (EditText) vv.findViewById(R.id.description);

                        ImageView imageView = (ImageView)vv. findViewById(R.id.image_view);
                        byte[] imageInByte = new byte[0];
                        String image_str = "";
                        try{
                            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                            imageInByte = baos.toByteArray();
                            image_str = Base64.encodeToString(imageInByte,Base64.DEFAULT);
                            // String string = new String(imageInByte);
                            //Log.d("imageInByte_string",string);
                            Log.d("image_str",image_str);
                        }catch (Exception e) {
                            Utils.showAlert(AddInspectionReportScreen.this,"Atleast Capture one Photo");
                            break;
                            //e.printStackTrace();
                        }

                        String description = myEditTextView.getText().toString();

                        if (MyLocationListener.latitude > 0) {
                            offlatTextValue = "" + MyLocationListener.latitude;
                            offlanTextValue = "" + MyLocationListener.longitude;
                        }

                        // Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();

                        if (!Utils.isOnline()) {

                            ContentValues imageValue = new ContentValues();

                            imageValue.put(AppConstant.INSPECTION_ID,inspectionID);
                            imageValue.put(AppConstant.WORK_ID,work_id);
                            imageValue.put(AppConstant.LATITUDE,offlatTextValue);
                            imageValue.put(AppConstant.LONGITUDE,offlanTextValue);
                            imageValue.put(AppConstant.IMAGE,image_str.trim());
                            imageValue.put(AppConstant.DESCRIPTION,description);

                            long rowInserted = LoginScreen.db.insert(DBHelper.CAPTURED_PHOTO,null,imageValue);

                            if(rowInserted != -1) {
                                Toast.makeText(AddInspectionReportScreen.this, "New Inspection added", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(AddInspectionReportScreen.this, "Something wrong", Toast.LENGTH_SHORT).show(); }
                        }
                        else {
                            imageArray.put(i);
                            imageArray.put(work_id);
                            imageArray.put(offlatTextValue);
                            imageArray.put(offlanTextValue);
                            imageArray.put(image_str.trim());
                            imageArray.put(description);
                            imageJson.put(imageArray);
                        }
                    }
                    try {
                        dataset.put("image_details",imageJson);

                        Log.d("post_dataset",dataset.toString());
                        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector),dataset.toString());
//                        int maxLogSize = 1000;
//                        for(int i = 0; i <= authKey.length() / maxLogSize; i++) {
//                            int start = i * maxLogSize;
//                            int end = (i+1) * maxLogSize;
//                            end = end > authKey.length() ? authKey.length() : end;
//                            Log.v("to_send", authKey.substring(start, end));
//                        }

                      sync_data();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    if (Utils.isOnline()) {
//                        sync_data();
//                    }
                }
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialog.dismiss();
                focusOnView(scrollView, action_tv);


            }
        });
        final String values = action_tv.getText().toString().replace("NA", "");
        Button btnAddMobile = (Button) dialog.findViewById(R.id.btn_add);
        btnAddMobile.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
        viewArrayList.clear();
        btnAddMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() != null) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    updateView(AddInspectionReportScreen.this, mobileNumberLayout, "", type);
                } else {
                    Utils.showAlert(AddInspectionReportScreen.this, "Capture Image!");
                }
            }
        });
        if (!values.isEmpty()) {
            if (values.contains(",")) {
                String[] mobileOrEmail = values.split(",");
                for (int i = 0; i < mobileOrEmail.length; i++) {
                    if (viewArrayList.size() < 5) {
                        updateView(this, mobileNumberLayout, mobileOrEmail[i], type);
                    }
                }
            } else {
                if (viewArrayList.size() < 5) {
                    updateView(this, mobileNumberLayout, values, type);
                }
            }
        } else {
            updateView(this, mobileNumberLayout, values, type);
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

    //Method for update single view based on email or mobile type
    public View updateView(final Activity activity, final LinearLayout emailOrMobileLayout, final String values, final String type) {
        final View hiddenInfo = activity.getLayoutInflater().inflate(R.layout.image_with_description, emailOrMobileLayout, false);
        final ImageView imageView_close = (ImageView) hiddenInfo.findViewById(R.id.imageView_close);
        imageView = (ImageView) hiddenInfo.findViewById(R.id.image_view);
        image_view_preview = (ImageView) hiddenInfo.findViewById(R.id.image_view_preview);
        final EditText myEditTextView = (EditText) hiddenInfo.findViewById(R.id.description);


        Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Avenir-Roman.ttf");

        myEditTextView.setSelection(0);
        if ("Mobile".equalsIgnoreCase(type)) {

            if (!values.isEmpty()) {
                if (values.length() > 0 && values.contains("-")) {
                    String[] mobile = values.split("-");
                    if (mobile.length == 2) {
                        myEditTextView.setText(values.split("-")[1]);
                        int countryCode = Integer.parseInt(values.split("-")[0]);

                    }
                } /*else {
                    myEditTextView.setText(values);
                }*/
            }
        }
        imageView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageView.setVisibility(View.VISIBLE);
                    if (viewArrayList.size() != 1) {
                        ((LinearLayout) hiddenInfo.getParent()).removeView(hiddenInfo);
                        viewArrayList.remove(hiddenInfo);
                    }

                } catch (IndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
            }
        });
        image_view_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getLatLong();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatLong();
            }
        });
        emailOrMobileLayout.addView(hiddenInfo);

        View vv = emailOrMobileLayout.getChildAt(viewArrayList.size());
        EditText myEditTextView1 = (EditText) vv.findViewById(R.id.description);
        //myEditTextView1.setSelection(myEditTextView1.length());
        myEditTextView1.requestFocus();
        viewArrayList.add(hiddenInfo);
        return hiddenInfo;
    }

    private void getLatLong(){
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(AddInspectionReportScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(AddInspectionReportScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddInspectionReportScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(AddInspectionReportScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddInspectionReportScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddInspectionReportScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(AddInspectionReportScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(AddInspectionReportScreen.this, "Satellite communication not available to get GPS Co-ordination Please Capture Photo in Open Area..");
            }
        } else {
            Utils.showAlert(AddInspectionReportScreen.this, "GPS is not turned on...");
        }
    }

    private void captureImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(AddInspectionReportScreen.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview


            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            image_view_preview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
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
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public static Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    public void sync_data() {
        try {
            new ApiService(this).makeJSONObjectRequest("save_data", Api.Method.POST, UrlGenerator.getInspectionServicesListUrl(), dataTobeSavedJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject dataTobeSavedJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector),dataset.toString().replaceAll(" ",""));
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("saving", "" + authKey);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if (prefManager.getLevels().equalsIgnoreCase("D")) {
                if ("save_data".equals(urlType) && responseObj != null) {
                    String key = responseObj.getString(AppConstant.ENCODE_DATA);
                    String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                    JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                    if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                       // loadBlockList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                        Utils.showAlert(this,"Saved");
                    }
                    Log.d("saved_response", "" + responseDecryptedBlockKey);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
