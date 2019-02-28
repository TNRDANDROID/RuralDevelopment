package com.nic.RuralInspection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hanks.htextview.rainbow.RainbowTextView;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
import com.nic.RuralInspection.constant.AppConstant;
import com.nic.RuralInspection.session.PrefManager;

import java.util.List;

public class ViewActionAdapter extends RecyclerView.Adapter<ViewActionAdapter.MyViewHolder> {

    private PrefManager prefManager;
    private Context context;
    private List<BlockListValue> actionListValues;

    public ViewActionAdapter(Context context, List<BlockListValue> actionListValues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.actionListValues = actionListValues;
    }

    @Override
    public ViewActionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_actions, parent, false);
        return new ViewActionAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyCustomTextView date_of_action,action_remark,action_result_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            date_of_action = (MyCustomTextView) itemView.findViewById(R.id.date_of_action);
            action_remark = (MyCustomTextView) itemView.findViewById(R.id.action_remark);
            action_result_tv = (MyCustomTextView) itemView.findViewById(R.id.action_result_tv);
        }


        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onBindViewHolder(final ViewActionAdapter.MyViewHolder holder, final int position) {
        holder.date_of_action.setText(actionListValues.get(position).getDate_of_Action());
        holder.action_remark.setText(actionListValues.get(position).getAction_remark());
        holder.action_result_tv.setText(actionListValues.get(position).getActionresult());

    }

    @Override
    public int getItemCount() {
        return actionListValues.size();
    }

}
