package com.nic.RuralInspection.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.session.PrefManager;

import java.util.List;

/**
 * Created by NIC on 13-02-2019.
 */

public class ImageDescriptionAdapter extends RecyclerView.Adapter<ImageDescriptionAdapter.MyViewHolder> {

    private Context context;
    private PrefManager prefManager;
    private List<BlockListValue> imagelistvalues;

    public ImageDescriptionAdapter(Context context,List<BlockListValue> imagelistvalues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.imagelistvalues = imagelistvalues;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_image_with_description, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.description.setText(imagelistvalues.get(position).getDescription());
        holder.imageView.setImageBitmap(imagelistvalues.get(position).getImage());
    }

    @Override
    public int getItemCount() {
       return imagelistvalues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private MyCustomTextView description;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            description = (MyCustomTextView) itemView.findViewById(R.id.description);
        }


    }
}
