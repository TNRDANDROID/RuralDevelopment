package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nic.RuralInspection.Adapter.ViewActionImageAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.util.ArrayList;
import java.util.List;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

public class ViewActionImageScreen extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private ViewActionImageAdapter  viewActionImageAdapter;
    private List<BlockListValue> imagePreviewlistvalues;
    private ImageView home;
    private RecyclerView view_action_image_recyclerview;
    private ImageView back_img;
    private MyCustomTextView title_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_action_image_screen);
        intializeUI();


    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        imagePreviewlistvalues = new ArrayList<>();
        viewActionImageAdapter = new ViewActionImageAdapter(this, imagePreviewlistvalues);
        view_action_image_recyclerview = (RecyclerView) findViewById(R.id.view_action_image_recyclerview);
        home = (ImageView) findViewById(R.id.homeimg);
//        done = (Button) findViewById(R.id.btn_save);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        view_action_image_recyclerview.setLayoutManager(mLayoutManager);
        view_action_image_recyclerview.setItemAnimator(new DefaultItemAnimator());
        view_action_image_recyclerview.setHasFixedSize(true);
        view_action_image_recyclerview.setNestedScrollingEnabled(false);
        view_action_image_recyclerview.setFocusable(false);
        view_action_image_recyclerview.setAdapter(viewActionImageAdapter);
        title_tv = (MyCustomTextView) findViewById(R.id.title_tv);
        back_img = (ImageView) findViewById(R.id.backimg);
        back_img.setOnClickListener(this);
        home.setOnClickListener(this);
        title_tv.setText("View Action Image");
        retriveImageWithDescription();
    }

    public void retriveImageWithDescription() {
        imagePreviewlistvalues.clear();
        String inspection_id = getIntent().getStringExtra(AppConstant.INSPECTION_ID);
        String  action_id = getIntent().getStringExtra(AppConstant.ACTION_ID);
        String  delete_flag = getIntent().getStringExtra(AppConstant.DELETE_FLAG);

        String image_sql=null;
        if(delete_flag.equals("Offline")) {
            image_sql = "SELECT * FROM " + DBHelper.CAPTURED_PHOTO + " WHERE inspection_id = " + inspection_id+" and action_id ="+action_id;
        }
        else if(delete_flag.equals("Online")) {
            image_sql = "SELECT * FROM " + DBHelper.ACTION_PHOTO + " WHERE inspection_id = " + inspection_id+" and action_id ="+action_id;
        }
        Log.d("Action_image_sql", image_sql);
        Cursor imageListPreview = getRawEvents(image_sql, null);

        if (imageListPreview.getCount() > 0) {
            if (imageListPreview.moveToFirst()) {
                do {
                    String work_id = imageListPreview.getString(imageListPreview.getColumnIndexOrThrow(AppConstant.WORK_ID));
                    String latitude = imageListPreview.getString(imageListPreview.getColumnIndexOrThrow(AppConstant.LATITUDE));
                    String longitude = imageListPreview.getString(imageListPreview.getColumnIndexOrThrow(AppConstant.LONGITUDE));
                    String description = imageListPreview.getString(imageListPreview.getColumnIndexOrThrow(AppConstant.DESCRIPTION));

                    byte[] photo = imageListPreview.getBlob(imageListPreview.getColumnIndexOrThrow(AppConstant.IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    //  byte[] image =  imageListPreview.getBlob(imageListPreview.getColumnIndexOrThrow(AppConstant.IMAGE));


                    BlockListValue imageValue = new BlockListValue();

                    imageValue.setWorkID(work_id);
                    imageValue.setLatitude(latitude);
                    imageValue.setLongitude(longitude);
                    imageValue.setDescription(description);
                    imageValue.setImage(decodedByte);

                    imagePreviewlistvalues.add(imageValue);

                } while (imageListPreview.moveToNext());
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backimg:
                onBackPress();
                break;
            case R.id.homeimg:
                dashboard();
                break;

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

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }
}


