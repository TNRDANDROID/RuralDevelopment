package com.nic.RuralInspection.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.session.PrefManager;

import java.util.List;

/**
 * Created by NIC on 15-02-2019.
 */

public class InspectionListAdapter extends RecyclerView.Adapter<InspectionListAdapter.MyViewHolder> {
    private PrefManager prefManager;
    private Context context;

    public InspectionListAdapter(Context context) {

        this.context = context;
        prefManager = new PrefManager(context);
    }
    @Override
    public InspectionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspection_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InspectionListAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
