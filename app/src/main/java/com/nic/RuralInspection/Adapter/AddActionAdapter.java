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
 * Created by NIC on 21-02-2019.
 */

public class AddActionAdapter extends RecyclerView.Adapter<AddActionAdapter.MyViewHolder> {
    private PrefManager prefManager;
    private Context context;

    private List<BlockListValue> actionlistvalues;

    public AddActionAdapter(Context context, List<BlockListValue> actionlistvalues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.actionlistvalues = actionlistvalues;
    }

    @Override
    public AddActionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_action_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddActionAdapter.MyViewHolder holder, int position) {

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
