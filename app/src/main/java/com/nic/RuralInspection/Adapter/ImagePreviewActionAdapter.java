package com.nic.RuralInspection.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.session.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActionAdapter extends BaseAdapter {

    private Context context;
    private PrefManager prefManager;
    private List<BlockListValue> imagePreviewActionlistvalues;

    public ImagePreviewActionAdapter(Context context, List<BlockListValue> imagePreviewActionlistvalues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.imagePreviewActionlistvalues = imagePreviewActionlistvalues;
    }

    @Override
    public int getCount() {
        return imagePreviewActionlistvalues.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePreviewActionlistvalues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(context);
            grid = inflater.inflate(R.layout.grid_image, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            textView.setText( imagePreviewActionlistvalues.get(position).getDescription());
            imageView.setImageBitmap(imagePreviewActionlistvalues.get(position).getImage());
        } else {
            grid = (View) convertView;
        }

        return grid;
    }


}
