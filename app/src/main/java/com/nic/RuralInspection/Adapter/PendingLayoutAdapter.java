package com.nic.RuralInspection.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nic.RuralInspection.R;

/**
 * Created by NIC on 21-02-2019.
 */

public class PendingLayoutAdapter extends RecyclerView.Adapter<PendingLayoutAdapter.MyViewHolder> {
    @Override
    public PendingLayoutAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_layout, parent, false);
        return new  MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PendingLayoutAdapter.MyViewHolder holder, int position) {

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
