package com.nic.RuralInspection.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> {

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list, parent, false);
        return new MyViewHolder(itemView);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyCustomTextView projectName, amountTv, levelTv, viewReport, addReport;

        public MyViewHolder(View itemView) {
            super(itemView);
            projectName = (MyCustomTextView)itemView.findViewById(R.id.project_title_tv);
            amountTv = (MyCustomTextView)itemView.findViewById(R.id.amount_tv);
            levelTv = (MyCustomTextView)itemView.findViewById(R.id.level_tv);
            viewReport = (MyCustomTextView)itemView.findViewById(R.id.view_inspection_report);
            addReport = (MyCustomTextView)itemView.findViewById(R.id.add_inspection_report);
        }

        @Override
        public void onClick(View v) {

        }
    }


    @Override
    public void onBindViewHolder(ProjectListAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

}
