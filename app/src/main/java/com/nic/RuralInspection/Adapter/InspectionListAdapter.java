package com.nic.RuralInspection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hanks.htextview.rainbow.RainbowTextView;
import com.nic.RuralInspection.Activity.ImagePreviewActionScreen;
import com.nic.RuralInspection.Activity.ImagePreviewScreen;
import com.nic.RuralInspection.Activity.ViewActions;
import com.nic.RuralInspection.Activity.ViewInspectionInActionScreen;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.util.List;

/**
 * Created by NIC on 15-02-2019.
 */

public class InspectionListAdapter extends RecyclerView.Adapter<InspectionListAdapter.MyViewHolder> {
    private PrefManager prefManager;
    private Context context;
    private List<BlockListValue> inspectionlistvalues;

    public InspectionListAdapter(Context context, List<BlockListValue> inspectionlistvalues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.inspectionlistvalues = inspectionlistvalues;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspection_list, parent, false);
        return new MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyCustomTextView date_of_inspection, remark, observation, view_image, action_result_tv,view_action;
        public RainbowTextView rainbowTextView;
        private RelativeLayout add_action_layout;
        private LinearLayout action_part_visible_layout;

        public MyViewHolder(View itemView) {

            super(itemView);
            date_of_inspection = (MyCustomTextView) itemView.findViewById(R.id.date_of_inspection);
            rainbowTextView = (RainbowTextView) itemView.findViewById(R.id.view_image);
//            view_image = (MyCustomTextView) itemView.findViewById(R.id.view_image);
            add_action_layout = (RelativeLayout) itemView.findViewById(R.id.add_action_layout);
            remark = (MyCustomTextView) itemView.findViewById(R.id.remark);
            observation = (MyCustomTextView) itemView.findViewById(R.id.observation);
            action_result_tv = (MyCustomTextView) itemView.findViewById(R.id.action_result_tv);
            view_action = (MyCustomTextView) itemView.findViewById(R.id.view_action);

            add_action_layout.setOnClickListener(this);

            if (prefManager.getLevels().equalsIgnoreCase("B")) {
                rainbowTextView.animateText(context.getString(R.string.take_action));
            } else {
                rainbowTextView.animateText(context.getString(R.string.view_image));
            }
        }


        @Override
        public void onClick(View v) {

        }
    }

    public void addActionScreen(int position) {
        String actionWorkid = inspectionlistvalues.get(position).getWorkID();
        String actionProjectName = inspectionlistvalues.get(position).getWorkName();
        String actionStageLevel = inspectionlistvalues.get(position).getWorkStageName();
        String actionAmount = inspectionlistvalues.get(position).getAsAmount();
        String actionDateOfInspection = inspectionlistvalues.get(position).getDate_of_inspection();
        String actionRemark = inspectionlistvalues.get(position).getInspection_remark();
        String actionObservatuion = inspectionlistvalues.get(position).getObservation();


        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ViewInspectionInActionScreen.class);

        intent.putExtra(AppConstant.WORK_ID, actionWorkid);
        intent.putExtra(AppConstant.WORK_NAME, actionProjectName);

        intent.putExtra(AppConstant.WORK_SATGE_NAME, actionStageLevel);
        intent.putExtra(AppConstant.AS_AMOUNT, actionAmount);

        intent.putExtra(AppConstant.DATE_OF_INSPECTION, actionDateOfInspection);
        intent.putExtra(AppConstant.INSPECTION_REMARK, actionRemark);
        intent.putExtra(AppConstant.OBSERVATION, actionObservatuion);

        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void imagePreviewScreen(int position) {
        //String inspection_id = String.valueOf(inspectionlistvalues.get(position).getInspectionID());
        String Online_inspect_id = String.valueOf(inspectionlistvalues.get(position).getOnlineInspectID());

        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ImagePreviewScreen.class);
        intent.putExtra(AppConstant.INSPECTION_ID, Online_inspect_id);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void imagePreviewActionScreen(int position) {
        //String inspection_id = String.valueOf(inspectionlistvalues.get(position).getInspectionID());
        String Online_inspect_id = String.valueOf(inspectionlistvalues.get(position).getOnlineInspectID());

        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ImagePreviewActionScreen.class);
        intent.putExtra(AppConstant.INSPECTION_ID, Online_inspect_id);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.date_of_inspection.setText(inspectionlistvalues.get(position).getDate_of_inspection());
        holder.remark.setText(inspectionlistvalues.get(position).getInspection_remark());
        holder.observation.setText(inspectionlistvalues.get(position).getObservation());
        holder.action_result_tv.setText(inspectionlistvalues.get(position).getDetail());

        holder.rainbowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prefManager.getLevels().equalsIgnoreCase("B")) {
                    imagePreviewScreen(position);
                } else {
//                    imagePreviewActionScreen(position);
                    addActionScreen(position);

                }
            }
        });

        holder.view_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAction(position);
            }
        });

        holder.add_action_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


    }

    @Override
    public int getItemCount() {
        return inspectionlistvalues.size();
    }

    public void viewAction(int position){
        String work_id = inspectionlistvalues.get(position).getWorkID();
        String inspect_id = String.valueOf(inspectionlistvalues.get(position).getOnlineInspectID());

        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ViewActions.class);
        intent.putExtra(AppConstant.INSPECTION_ID, inspect_id);
        intent.putExtra(AppConstant.WORK_ID, work_id);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
