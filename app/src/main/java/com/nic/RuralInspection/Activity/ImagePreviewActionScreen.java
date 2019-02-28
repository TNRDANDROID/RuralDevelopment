package com.nic.RuralInspection.Activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nic.RuralInspection.Adapter.ImagePreviewAdapter;
import com.nic.RuralInspection.DataBase.DBHelper;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nic.RuralInspection.Activity.LoginScreen.db;

public class ImagePreviewActionScreen extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private ImagePreviewAdapter imagePreviewAdapter;
    private List<BlockListValue> imagePreviewlistvalues;
    private ImageView home;
    private Button done;
    private RecyclerView image_preview_recyclerview;
    private RelativeLayout add_action_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_main_layout);
        intializeUI();


    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        imagePreviewlistvalues = new ArrayList<>();
        imagePreviewAdapter = new ImagePreviewAdapter(this, imagePreviewlistvalues);
        image_preview_recyclerview = (RecyclerView) findViewById(R.id.image_preview_action_recyclerview);
        home = (ImageView) findViewById(R.id.home);
        done = (Button) findViewById(R.id.btn_save);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        image_preview_recyclerview.setLayoutManager(mLayoutManager);
        image_preview_recyclerview.setItemAnimator(new DefaultItemAnimator());
        image_preview_recyclerview.setHasFixedSize(true);
        image_preview_recyclerview.setNestedScrollingEnabled(false);
        image_preview_recyclerview.setFocusable(false);
        image_preview_recyclerview.setAdapter(imagePreviewAdapter);
        done.setText("Take Action");
        done.setBackgroundResource(R.drawable.login_button);
        done.setOnClickListener(this);
        home.setOnClickListener(this);
        retriveImageWithDescription();
    }

    public void retriveImageWithDescription() {
        imagePreviewlistvalues.clear();
        String id = getIntent().getStringExtra(AppConstant.INSPECTION_ID);
        int inspectionId = Integer.parseInt(id);


        String image_sql = "SELECT * FROM " + DBHelper.CAPTURED_PHOTO + " WHERE inspection_id = " + inspectionId;
        Log.d("image_sql", image_sql);
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
            case R.id.btn_save:

                break;
        }
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
