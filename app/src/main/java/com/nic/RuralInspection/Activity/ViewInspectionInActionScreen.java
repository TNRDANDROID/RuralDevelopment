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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.RuralInspection.Adapter.AddActionAdapter;
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
 * Created by NIC on 21-02-2019.
 */

public class ViewInspectionInActionScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {
    private ScrollView scrollView;
    private MyCustomTextView action_tv;
    private List<View> viewArrayList = new ArrayList<>();

    private Context context;

    final int CAMERA_REQUEST = 1888;

    ImageView imageView;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;
    LocationManager mlocManager = null;
    LocationListener mlocListener;


    private static String imageStoragePath;
    private ImageView back_img, image_view_preview;
    private MyCustomTextView district_tv, scheme_name_tv, block_name_tv, block_user_tv, village_name_tv, fin_year_tv, take_photo, title_tv;
    private MyCustomTextView projectName, amountTv, levelTv, inspected_date, remark, observation;
    private LinearLayout village_layout, block_layout;
    private AddActionAdapter addActionAdapter;
    private RecyclerView imageRecyclerView, inspectionListRecyclerView;
    PrefManager prefManager;
    EditText remark_action_tv;


    String offlatTextValue, offlanTextValue;
    String work_id;
    EditText remarkTv;
    static JSONObject dataset;

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
        block_layout = (LinearLayout) findViewById(R.id.block_user_layout);
        block_user_tv = (MyCustomTextView) findViewById(R.id.block_user_tv);
        district_tv = (MyCustomTextView) findViewById(R.id.district_tv);
        scheme_name_tv = (MyCustomTextView) findViewById(R.id.scheme_name_tv);
        block_name_tv = (MyCustomTextView) findViewById(R.id.block_name_tv);
        village_name_tv = (MyCustomTextView) findViewById(R.id.village_name_tv);
        fin_year_tv = (MyCustomTextView) findViewById(R.id.fin_year_tv);
        title_tv = (MyCustomTextView) findViewById(R.id.title_tv);

        projectName = (MyCustomTextView) findViewById(R.id.project_title_tv);
        amountTv = (MyCustomTextView) findViewById(R.id.amount_tv);
        levelTv = (MyCustomTextView) findViewById(R.id.level_tv);
        take_photo = (MyCustomTextView) findViewById(R.id.take_photo);
        inspected_date = (MyCustomTextView) findViewById(R.id.action_inspected_date);
        remark = (MyCustomTextView) findViewById(R.id.action_remark);
        observation = (MyCustomTextView) findViewById(R.id.action_observation);
        remark_action_tv = (EditText) findViewById(R.id.remark_action_tv);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
//        action_tv = (MyCustomTextView) findViewById(R.id.action_tv);
        back_img = (ImageView) findViewById(R.id.backimg);

        district_tv.setText(prefManager.getDistrictName());
        scheme_name_tv.setText(prefManager.getSchemeName());
        block_name_tv.setText(prefManager.getBlockName());
        fin_year_tv.setText(prefManager.getFinancialyearName());

        projectName.setText(prefManager.getKeyActionProjectName());
        amountTv.setText(prefManager.getKeyActionAmount());
        levelTv.setText(prefManager.getKeyActionStageLevel());
        inspected_date.setText(getIntent().getStringExtra(AppConstant.DATE_OF_INSPECTION));
        remark.setText(getIntent().getStringExtra(AppConstant.INSPECTION_REMARK));
        observation.setText(getIntent().getStringExtra(AppConstant.OBSERVATION));
        title_tv.setText("Take Action");
        back_img.setOnClickListener(this);
        take_photo.setOnClickListener(this);

//        addActionAdapter = new AddActionAdapter(this, actionlistvalues);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        inspectionListRecyclerView.setLayoutManager(mLayoutManager);
//        inspectionListRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        inspectionListRecyclerView.setHasFixedSize(true);
//        inspectionListRecyclerView.setFocusable(false);
//        inspectionListRecyclerView.setNestedScrollingEnabled(false);

        if (prefManager.getLevels().equalsIgnoreCase("B")) {
            village_layout.setVisibility(View.VISIBLE);
            village_name_tv.setText(prefManager.getVillageListPvName());
            block_layout.setVisibility(View.VISIBLE);
            block_user_tv.setText(prefManager.getBlockName());
        }

//        inspectionListRecyclerView.setAdapter(addActionAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                if (!remark_action_tv.getText().toString().isEmpty()) {
                    imageWithDescription(take_photo, "mobile", scrollView);
                } else {
                    Utils.showAlert(this, "Enter Remark");
                }

                break;
            case R.id.backimg:
                onBackPress();
                break;
        }
    }


    public void imageWithDescription(final MyCustomTextView action_tv, final String type, final ScrollView scrollView) {
        dataset = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        work_id = getIntent().getStringExtra(AppConstant.WORK_ID);
        final String inspection_id = getIntent().getStringExtra(AppConstant.INSPECTION_ID);

        String date_of_action = sdf.format(new Date());
        String action_remark = remark_action_tv.getText().toString();

        if (!Utils.isOnline()) {
            ContentValues actionValue = new ContentValues();
            actionValue.put(AppConstant.WORK_ID, work_id);
            actionValue.put(AppConstant.INSPECTION_ID, inspection_id);
            actionValue.put(AppConstant.DATE_OF_ACTION, date_of_action);
            actionValue.put(AppConstant.ACTION_REMARK, action_remark);
            actionValue.put(AppConstant.DELETE_FLAG, 0);

            long rowInserted = LoginScreen.db.insert(DBHelper.INSPECTION_ACTION, null, actionValue);
            Log.d("rowInserted", String.valueOf(rowInserted));
        } else {
            try {
                dataset.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_HIGH_VALUE_PROJECT_ACTION_SAVE);
                dataset.put(AppConstant.WORK_ID, work_id);
                dataset.put(AppConstant.INSPECTION_ID, inspection_id);
                dataset.put(AppConstant.CREATED_IMEI_NO, prefManager.getIMEI());
                dataset.put(AppConstant.DATE_OF_ACTION, date_of_action);
                dataset.put(AppConstant.ACTION_REMARK, action_remark);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        int actionID = 0;

        if (!Utils.isOnline()) {
            Cursor action_Cursor = getRawEvents("SELECT MAX(id) FROM " + DBHelper.INSPECTION_ACTION, null);
            Log.d("cursor_count", String.valueOf(action_Cursor.getCount()));
            if (action_Cursor.getCount() > 0) {
                if (action_Cursor.moveToFirst()) {
                    do {
                        actionID = action_Cursor.getInt(0);
                        Log.d("actionID", "" + actionID);
                    } while (action_Cursor.moveToNext());
                }
            }
        }
        String imagelist_sql = "select * from " + DBHelper.CAPTURED_PHOTO + " where inspection_id=" + inspection_id;
        Log.d("sql", imagelist_sql);
        Cursor imagelist = getRawEvents(imagelist_sql, null);

        if (imagelist.getCount() > 0) {
            JSONArray group = new JSONArray();
            if (imagelist.moveToFirst()) {
                do {
                    String image_id = imagelist.getString(imagelist.getColumnIndexOrThrow("id"));
                    group.put(Integer.parseInt(image_id));
                } while (imagelist.moveToNext());
            }
            ContentValues grouped_array = new ContentValues();
            grouped_array.put("action_id",actionID);
            grouped_array.put("grouping", String.valueOf(group));
            Log.d("grouping",group.toString());
            LoginScreen.db.insert(DBHelper.IMAGE_GROUP_ID,null,grouped_array);
        }

        String list_sql = "select * from " + DBHelper.IMAGE_GROUP_ID;
        Log.d("sql", imagelist_sql);
        Cursor list = getRawEvents(list_sql, null);

        if (list.getCount() > 0) {
            JSONArray group_info = new JSONArray();
            JSONObject element;
            if (list.moveToFirst()) {
                do {
                    element = new JSONObject();

                    String id = list.getString(list.getColumnIndexOrThrow("id"));
                    String grouping = list.getString(list.getColumnIndexOrThrow("grouping"));

                    try {
                        element.put("id",id);
                        element.put("grouping",grouping);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    group_info.put(element);
                } while (list.moveToNext());

            }
            try {
                dataset.put("grouping_details", group_info);
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

        final int finalActionID = actionID;
        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int image_group_id = 1;
                JSONArray imageJson = new JSONArray();

                Cursor image_Cursor = getRawEvents("SELECT MAX(id) FROM " + DBHelper.IMAGE_GROUP_ID, null);
                Log.d("cursor_count", String.valueOf(image_Cursor.getCount()));
                if (image_Cursor.getCount() > 0) {
                    if (image_Cursor.moveToFirst()) {
                        do {
                            image_group_id = image_Cursor.getInt(0);
                            Log.d("inspectionID", "" + image_group_id);
                        } while (image_Cursor.moveToNext());
                    }
                }
                int childCount = mobileNumberLayout.getChildCount();
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        JSONArray imageArray = new JSONArray();

                        View vv = mobileNumberLayout.getChildAt(i);
                        EditText myEditTextView = (EditText) vv.findViewById(R.id.description);

                        ImageView imageView = (ImageView) vv.findViewById(R.id.image_view);
                        byte[] imageInByte = new byte[0];
                        String image_str = "";
                        try {
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                            imageInByte = baos.toByteArray();
                            image_str = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                            // String string = new String(imageInByte);
                            //Log.d("imageInByte_string",string);
                            Log.d("image_str", image_str);
                        } catch (Exception e) {
                            Utils.showAlert(ViewInspectionInActionScreen.this, "Atleast Capture one Photo");
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

                            imageValue.put(AppConstant.INSPECTION_ID, inspection_id);
                            imageValue.put(AppConstant.WORK_ID, work_id);
                            imageValue.put(AppConstant.LATITUDE, offlatTextValue);
                            imageValue.put(AppConstant.LONGITUDE, offlanTextValue);
                            imageValue.put(AppConstant.IMAGE, image_str.trim());
                            imageValue.put(AppConstant.DESCRIPTION, description);
                            imageValue.put("action_id", finalActionID);
                            imageValue.put("image_group_id", image_group_id);

                            long rowInserted = LoginScreen.db.insert(DBHelper.CAPTURED_PHOTO, null, imageValue);

                            if (rowInserted != -1) {
                                Toast.makeText(ViewInspectionInActionScreen.this, "New Action added", Toast.LENGTH_SHORT).show();
                                Dashboard.getPendingCount();
                                finish();
                            } else {
                                Toast.makeText(ViewInspectionInActionScreen.this, "Something wrong", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            imageArray.put(work_id);
                            imageArray.put(image_group_id);
                            imageArray.put(offlatTextValue);
                            imageArray.put(offlanTextValue);
                            imageArray.put(image_str.trim());
                            imageArray.put(description);
                            imageJson.put(imageArray);
                        }
                    }
                    try {
                        dataset.put("image_details", imageJson);

                        Log.d("post_dataset_action", dataset.toString());
//                       String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), dataset.toString());
//                       // String authKey = dataset.toString();
//                        int maxLogSize = 4000;
//                        for(int i = 0; i <= authKey.length() / maxLogSize; i++) {
//                            int start = i * maxLogSize;
//                           int end = (i+1) * maxLogSize;
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
                    updateView(ViewInspectionInActionScreen.this, mobileNumberLayout, "", type);
                } else {
                    Utils.showAlert(ViewInspectionInActionScreen.this, "Capture Image!");
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

    private void getLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(ViewInspectionInActionScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(ViewInspectionInActionScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewInspectionInActionScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(ViewInspectionInActionScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewInspectionInActionScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ViewInspectionInActionScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(ViewInspectionInActionScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(ViewInspectionInActionScreen.this, "Satellite communication not available to get GPS Co-ordination Please Capture Photo in Open Area..");
            }
        } else {
            Utils.showAlert(ViewInspectionInActionScreen.this, "GPS is not turned on...");
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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
                        CameraUtils.openSettings(ViewInspectionInActionScreen.this);
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
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            Utils.showAlert(this, "Saved");
            finish();
            JSONObject responseObj = serverResponse.getJsonResponse();
            Utils.showAlert(this, "Saved");
            if (prefManager.getLevels().equalsIgnoreCase("D")) {
                if ("save_data".equals(urlType) && responseObj != null) {
                    String key = responseObj.getString(AppConstant.ENCODE_DATA);
                    String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                    JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                    if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                        // loadBlockList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                        Utils.showAlert(this, "Saved");
                        finish();
                    }
                    Log.d("saved_response", "" + responseDecryptedBlockKey);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), dataset.toString().replaceAll(" ", ""));
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("saving", "" + authKey);
        return dataSet;
    }
}
