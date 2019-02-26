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
import android.widget.Toast;

import com.nic.RuralInspection.Adapter.ImageDescriptionAdapter;
import com.nic.RuralInspection.Adapter.ImagePreviewActionAdapter;
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
    private ImagePreviewActionAdapter imagePreviewActionAdapter;
    private List<BlockListValue> imagelistvalues;
    private GridView gridView;
//    private ImageView back_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_main_layout);
intializeUI();


    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        imagelistvalues = new ArrayList<>();
        imagePreviewActionAdapter = new ImagePreviewActionAdapter(this, imagelistvalues);
        gridView=(GridView)findViewById(R.id.grid);

//        back_img = (ImageView) findViewById(R.id.backimg);
//        back_img.setOnClickListener(this);

        gridView.setAdapter(imagePreviewActionAdapter);
        retriveImageWithDescription();
    }

    public void retriveImageWithDescription() {
        imagelistvalues.clear();
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

                    imagelistvalues.add(imageValue);

                } while (imageListPreview.moveToNext());
            }
        }

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.backimg:
//                onBackPress();
//                break;
//        }
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
