package com.nic.RuralInspection.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.session.PrefManager;

import java.util.List;

/**
 * Created by NIC on 15-02-2019.
 */

public class InspectionListAdapter extends RecyclerView.Adapter<InspectionListAdapter.MyViewHolder> {
    private PrefManager prefManager;
    private Context context;
    private List<BlockListValue> inspectionlistvalues;

    public InspectionListAdapter(Context context,List<BlockListValue> inspectionlistvalues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.inspectionlistvalues = inspectionlistvalues;
    }
    @Override
    public InspectionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspection_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InspectionListAdapter.MyViewHolder holder, int position) {
        holder.date_of_inspection.setText(inspectionlistvalues.get(position).getDate_of_inspection());
        holder.remark.setText(inspectionlistvalues.get(position).getInspection_remark());
        holder.observation.setText(inspectionlistvalues.get(position).getObservation());

    }

    @Override
    public int getItemCount() {
        return inspectionlistvalues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyCustomTextView date_of_inspection,remark,observation;

        public MyViewHolder(View itemView) {

            super(itemView);
            date_of_inspection = (MyCustomTextView) itemView.findViewById(R.id.date_of_inspection);
            remark = (MyCustomTextView) itemView.findViewById(R.id.remark);
            observation = (MyCustomTextView) itemView.findViewById(R.id.observation);
        }


    }
}
