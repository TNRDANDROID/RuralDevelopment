package com.nic.RuralInspection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.nic.RuralInspection.Activity.AddInspectionReportScreen;
import com.nic.RuralInspection.Activity.ViewInspectionReportScreen;
import com.nic.RuralInspection.Model.ProjectListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AchanthiSundar on 04-01-2019.
 */

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<ProjectListValue> projectList;
    private List<ProjectListValue> projectListFiltered;
    private ProjectsAdapterListener listener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list, parent, false);
        return new MyViewHolder(itemView);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyCustomTextView projectName, amountTv, levelTv, viewReport, addReport;

        public MyViewHolder(View itemView) {
            super(itemView);
            projectName = (MyCustomTextView) itemView.findViewById(R.id.project_title_tv);
            amountTv = (MyCustomTextView) itemView.findViewById(R.id.amount_tv);
            levelTv = (MyCustomTextView) itemView.findViewById(R.id.level_tv);
            viewReport = (MyCustomTextView) itemView.findViewById(R.id.view_inspection_report);
            addReport = (MyCustomTextView) itemView.findViewById(R.id.add_inspection_report);
            viewReport.setOnClickListener(this);
            addReport.setOnClickListener(this);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // send selected contact in callback
//                    listener.setProjectList(projectListFiltered.get(getAdapterPosition()));
//                }
//            });
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.view_inspection_report:
                    viewInspectionReport();
                    break;
                case R.id.add_inspection_report:
                    addInspectionReport();

            }


        }

    }

    public ProjectListAdapter(Context context, List<ProjectListValue> projectList, ProjectsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.projectList = projectList;
        this.projectListFiltered = projectList;
    }

    public void viewInspectionReport() {
        Intent intent = new Intent(context, ViewInspectionReportScreen.class);
        Activity activity = (Activity) context;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void addInspectionReport() {
        Intent intent = new Intent(context, AddInspectionReportScreen.class);
        Activity activity = (Activity) context;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }



    @Override
    public void onBindViewHolder(ProjectListAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    projectListFiltered = projectList;
                } else {
                    List<ProjectListValue> filteredList = new ArrayList<>();
                    for (ProjectListValue row : projectList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getProjectName().toLowerCase().contains(charString.toLowerCase()) || row.getProjectName().contains(charSequence)) {
                            filteredList.add(row);
                        } else {

                        }
                    }

                    projectListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = projectListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                projectListFiltered = (ArrayList<ProjectListValue>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ProjectsAdapterListener {
        void setProjectList(ProjectListValue projectList);
    }

}
