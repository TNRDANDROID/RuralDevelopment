package com.nic.RuralInspection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
        public MyCustomTextView date_of_inspection, remark, observation, view_image, ins_result_tv_on_ff, view_action;
        public View view;
        public RainbowTextView rainbowTextView;
        private RelativeLayout add_action_layout, view_action_layout;
        private LinearLayout action_part_visible_layout;

        public MyViewHolder(View itemView) {

            super(itemView);
            date_of_inspection = (MyCustomTextView) itemView.findViewById(R.id.date_of_inspection);
            rainbowTextView = (RainbowTextView) itemView.findViewById(R.id.view_image);
//            view_image = (MyCustomTextView) itemView.findViewById(R.id.view_image);
            add_action_layout = (RelativeLayout) itemView.findViewById(R.id.add_action_layout);
            view_action_layout = (RelativeLayout) itemView.findViewById(R.id.view_action_layout);
            remark = (MyCustomTextView) itemView.findViewById(R.id.remark);
            observation = (MyCustomTextView) itemView.findViewById(R.id.observation);
            ins_result_tv_on_ff = (MyCustomTextView) itemView.findViewById(R.id.ins_result_tv_on_ff);
            view_action = (MyCustomTextView) itemView.findViewById(R.id.view_action);
            view = (View) itemView.findViewById(R.id.view);


            add_action_layout.setOnClickListener(this);
            rainbowTextView.animateText(context.getString(R.string.view_image));
            if (prefManager.getLevels().equalsIgnoreCase("B")) {
                view_action_layout.setBackgroundColor(Color.parseColor("#ECECEC"));
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
        String inspection_id = inspectionlistvalues.get(position).getOnlineInspectID();


        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ViewInspectionInActionScreen.class);

        intent.putExtra(AppConstant.WORK_ID, actionWorkid);
        intent.putExtra(AppConstant.INSPECTION_ID, inspection_id);
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
        prefManager.setAppKey(Online_inspect_id);

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
        if (!prefManager.getLevels().equalsIgnoreCase("B")) {
            holder.ins_result_tv_on_ff.setText(inspectionlistvalues.get(position).getDetail());

        } else {

            holder.view_action_layout.setPadding(15, 15, 15, 15);
            holder.view.setVisibility(View.GONE);

            RelativeLayout.LayoutParams view_action_params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams params1 = (ViewGroup.LayoutParams) holder.view_action.getLayoutParams();
            params1.width = 90;
            params1.height = 35;
            holder.view_action.setLayoutParams(params1);
            holder.view_action.setBackgroundResource(R.drawable.add_button);
            holder.view_action.setText("View Action");
            view_action_params.addRule(RelativeLayout.ALIGN_PARENT_START);
            holder.view_action.setPadding(15, 15, 15, 15);
            holder.view_action.setLayoutParams(view_action_params);


            RelativeLayout.LayoutParams ins_result_tv_on_ff_params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams params2 = (ViewGroup.LayoutParams) holder.ins_result_tv_on_ff.getLayoutParams();
            params2.width = 90;
            params2.height = 35;
            holder.ins_result_tv_on_ff.setLayoutParams(params2);
            holder.ins_result_tv_on_ff.setBackgroundResource(R.drawable.login_button);
            holder.ins_result_tv_on_ff.setText("Take Action");
            ins_result_tv_on_ff_params.addRule(RelativeLayout.ALIGN_PARENT_END);
            holder.ins_result_tv_on_ff.setPadding(15, 15, 15, 15);
            holder.ins_result_tv_on_ff.setLayoutParams(ins_result_tv_on_ff_params);
        }
        holder.rainbowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePreviewScreen(position);

//                if (!prefManager.getLevels().equalsIgnoreCase("B")) {
//                    imagePreviewScreen(position);
//                } else {
//                    imagePreviewActionScreen(position);
////
//
//                }
            }
        });
        if (prefManager.getLevels().equalsIgnoreCase("B")) {
            holder.ins_result_tv_on_ff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addActionScreen(position);
                }
            });
        }

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

    public void viewAction(int position) {
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
