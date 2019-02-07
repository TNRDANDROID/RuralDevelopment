package com.nic.RuralInspection.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.nic.RuralInspection.Model.BlockListValue;
import com.nic.RuralInspection.R;
import com.nic.RuralInspection.Utils.FontCache;

import java.util.List;

/**
 * Created by shanmugapriyan on 25/05/16.
 */
public class CommonAdapter extends BaseAdapter {
    private List<BlockListValue> BlockList;
    private Context mContext;
    private String type;


    public CommonAdapter(Context mContext, List<BlockListValue> BlockListValue, String type) {
        this.BlockList = BlockListValue;
        this.mContext = mContext;
        this.type = type;
    }


    public int getCount() {
        return BlockList.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_drop_down_item, parent, false);
        TextView tv_type = (TextView) view.findViewById(R.id.tv_spinner_item);
        BlockListValue BlockListValue = BlockList.get(position);
        if(type.equalsIgnoreCase("BlockList")) {
            tv_type.setText(BlockListValue.getBlockName());
        }else if(type.equalsIgnoreCase("SchemeList")){
            tv_type.setText(BlockListValue.getSchemeName());
        }else if(type.equalsIgnoreCase("FinYearList")){
            tv_type.setText(BlockListValue.getFinancialYear());
        }
        return view;
    }
}
