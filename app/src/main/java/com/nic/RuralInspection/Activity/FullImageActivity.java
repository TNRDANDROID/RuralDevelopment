package com.nic.RuralInspection.Activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;

import java.io.InputStream;
import java.net.URL;

public class FullImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private MyCustomTextView toolBarTitle, toolBarRight;
    private ImageView toolBarLeft_icon, toolBarRight_icon;
    Toolbar toolbar;
    private int id = 0, position = 0, quesPosition = 0;
    private LinearLayout toolBarLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        imageView = (ImageView) findViewById(R.id.imageview);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    void closeActivity() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.back_layout:
//                closeActivity();
//                break;
//
//        }
//    }


}
