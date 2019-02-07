package com.nic.RuralInspection.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.Utils.CameraUtils;
import com.nic.RuralInspection.Utils.FontCache;
import com.nic.RuralInspection.Utils.Utils;
import com.nic.RuralInspection.api.Api;
import com.nic.RuralInspection.api.ServerResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIC on 23-01-2019.
 */

public class AddInspectionReportScreen extends AppCompatActivity implements View.OnClickListener , Api.ServerResponseListener {

    private ScrollView scrollView;
    private MyCustomTextView take_photo;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_inspection_with_toolbar);

        intializeUI();
    }

    public void intializeUI() {
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        take_photo = (MyCustomTextView) findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                imageWithDescription(take_photo, "mobile", scrollView);
                break;
        }
    }

    public void imageWithDescription(final MyCustomTextView action_tv, final String type, final ScrollView scrollView) {


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
                int childCount = mobileNumberLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View vv = mobileNumberLayout.getChildAt(i);
                    EditText myEditTextView = (EditText) vv.findViewById(R.id.description);

// ImageView imageView = (ImageView) findViewById(R.id.image_view);
// Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
// ByteArrayOutputStream baos = new ByteArrayOutputStream();
// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
// byte[] imageInByte = baos.toByteArray();


                    String str=myEditTextView.getText().toString();
                    Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
                }
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialog.dismiss();
                focusOnView(scrollView, action_tv);
            }
        });
        final String values = action_tv.getText().toString().replace("NA", "");
        Button btnAddMobile = (Button) dialog.findViewById(R.id.btn_add);
        btnAddMobile.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
//        if ("Mobile".equalsIgnoreCase(type)) {
//            toolBarTitle.setText("Enter Mobile");
//            tv_create_asset_title.setText("You can enter upto 5 Mobile numbers");
//            btnAddMobile.setText("Add Mobile");
//        } else {
//            btnAddMobile.setText("Add Email");
//            toolBarTitle.setText("Enter Email");
//            tv_create_asset_title.setText("You can enter upto 5 Emails");
//        }

        viewArrayList.clear();
        btnAddMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewArrayList.size() < 10) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    updateView(AddInspectionReportScreen.this, mobileNumberLayout, "", type);
                } else {
                    if ("Mobile".equalsIgnoreCase(type))
                        Utils.showAlert(AddInspectionReportScreen.this, "You can add upto 5 mobile numbers");
                    else
                        Utils.showAlert(AddInspectionReportScreen.this, "You can add upto 5 emails");

                }
            }
        });

//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    scrollView.fullScroll(View.FOCUS_DOWN);
//                    mobileList.clear();
//                    int childCount = mobileNumberLayout.getChildCount();
//                    for (int i = 0; i < childCount; i++) {
//                        View vv = mobileNumberLayout.getChildAt(i);
//                        MyEditTextView myEditTextView = (MyEditTextView) vv.findViewById(R.id.email_edit_text);
//
//                        String emailOrMobile = myEditTextView.getText().toString();
//
//                        if ("Email".equalsIgnoreCase(type)) {
//                            if (emailOrMobile.length() > 0) {
//                                if (Utils.isEmailValid(emailOrMobile) && !Utils.contains(mobileList, emailOrMobile)) {
//                                    mobileList.add(emailOrMobile);
//                                    isValidEmailOrMobile = true;
//                                } else {
//                                    isValidEmailOrMobile = false;
//                                }
//                            } else {
//                                isValidEmailOrMobile = false;
//                            }
//                        }
//                    }
//
//                    if (mobileList.size() > 0) {
//                        if (isValidEmailOrMobile) {
//                            dialog.dismiss();
//                            mobileNumberTextView.setText(Utils.emailOrNumberValues(mobileList));
//                        } else {
//                            if ("Mobile".equalsIgnoreCase(type)) {
//                                Utils.showAlert(activity, "Mobile Number can't be left blank or Mobile Number already exist!");
//                            } else {
//                                Utils.showAlert(activity, "Email field can't be left blank or Email already exist or Invalid Email Adderess!");
//                            }
//                        }
//                    } else {
//                        if ("Email".equalsIgnoreCase(type)) {
//                            Utils.showAlert(activity, "Email field can't be left blank or Email already exist!");
//                        } else {
//                            Utils.showAlert(activity, "Mobile Number can't be left blank or Mobile Number already exist!");
//                        }
//                    }
//
//                    /*if (mobileList.size() >= 0) {
//                        if(isValidEmailOrMobile){
//                            dialog.dismiss();
//                            mobileNumberTextView.setText(Utils.emailOrNumberValues(mobileList));
//                        } else{
//                            if ("Mobile".equalsIgnoreCase(type)) {
//                                Utils.showAlert(activity, "Invalid Mobile Number!");
//                            } else {
//                                Utils.showAlert(activity, "Invalid Email Address!");
//                            }
//                        }
//                    } else {
//                        if ("Mobile".equalsIgnoreCase(type)) {
//                            if(!isValidEmailOrMobile){
//                                Utils.showAlert(activity, "Invalid Mobile Number!");
//                            } else{
//                                Utils.showAlert(activity, "Mobile Number can't be left blank or Mobile Number already exist!");
//                            }
//                        } else {
//                            if(!isValidEmailOrMobile){
//                                Utils.showAlert(activity, "Invalid Email Address!");
//                            } else {
//                                Utils.showAlert(activity, "Email field can't be left blank or Email already exist!");
//                            }
//                        }
//                    }*/
//                } catch (ArrayIndexOutOfBoundsException a) {
//                    a.printStackTrace();
//                }
//
//            }
//        });

        if (!values.isEmpty()) {
            if (values.contains(",")) {
                String[] mobileOrEmail = values.split(",");
                for (int i = 0; i < mobileOrEmail.length; i++) {
                    if (viewArrayList.size() < 5) {
                        updateView( this, mobileNumberLayout, mobileOrEmail[i], type);
                    }
                }
            } else {
                if (viewArrayList.size() < 5) {
                    updateView( this, mobileNumberLayout, values, type);
                }
            }
        } else {
            updateView( this, mobileNumberLayout, values, type);
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
                    if (viewArrayList.size() != 1) {
                        ((LinearLayout) hiddenInfo.getParent()).removeView(hiddenInfo);
                        viewArrayList.remove(hiddenInfo);
                    }

                } catch (IndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CameraUtils.checkPermissions( AddInspectionReportScreen.this)) {
                    captureImage();
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE);
                }


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
        Dexter.withActivity( this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder( this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings( AddInspectionReportScreen.this);
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        //Intent intent = new Intent(this, HomeScreenActivity.class);
        //startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
}
