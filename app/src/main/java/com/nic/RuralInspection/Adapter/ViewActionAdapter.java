package com.nic.RuralInspection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hanks.htextview.rainbow.RainbowTextView;
import com.nic.RuralInspection.Activity.ViewActionImageScreen;
import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Support.MyCustomTextView;
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
        public MyCustomTextView date_of_action,action_remark,action_result_tv,action_on_off;
        public RainbowTextView rainbowTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            date_of_action = (MyCustomTextView) itemView.findViewById(R.id.date_of_action);
            rainbowTextView = (RainbowTextView) itemView.findViewById(R.id.view_action_image);
            action_remark = (MyCustomTextView) itemView.findViewById(R.id.action_remark);
            action_result_tv = (MyCustomTextView) itemView.findViewById(R.id.action_result_tv);
            action_on_off = (MyCustomTextView) itemView.findViewById(R.id.action_on_off);

            rainbowTextView.animateText(context.getString(R.string.view_action_image));

        }


        @Override
        public void onClick(View v) {

        }
    }

    public void actionImagePreviewScreen(int position) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ViewActionImageScreen.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBindViewHolder(final ViewActionAdapter.MyViewHolder holder, final int position) {
        holder.rainbowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionImagePreviewScreen(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return actionListValues.size();
    }

}
