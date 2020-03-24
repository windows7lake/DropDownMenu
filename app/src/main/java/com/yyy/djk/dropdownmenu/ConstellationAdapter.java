package com.yyy.djk.dropdownmenu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ConstellationAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private List<String> checkList;

    public void setCheckItem(int position) {
        if (checkList.contains("风格不限")) {
            checkList.clear();
            checkList.add(list.get(position));
        } else if (list.get(position).equals("风格不限")) {
            checkList.clear();
            checkList.add(list.get(position));
        } else if (checkList.contains(list.get(position))) {
            checkList.remove(list.get(position));
        } else if (checkList.size() >= 2) {
            Toast.makeText(context, "最多选择两项", Toast.LENGTH_SHORT).show();
        } else {
            checkList.add(list.get(position));
        }
        notifyDataSetChanged();
    }

    public ConstellationAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        this.checkList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_constellation_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.mText.setText(list.get(position));
        if (checkList.contains(list.get(position))) {
            viewHolder.mText.setTextColor(context.getResources().getColor(R.color.drop_down_selected));
            viewHolder.mText.setBackgroundResource(R.drawable.check_bg);
        } else {
            viewHolder.mText.setTextColor(context.getResources().getColor(R.color.drop_down_unselected));
            viewHolder.mText.setBackgroundResource(R.drawable.uncheck_bg);
        }
    }

    static class ViewHolder {
        TextView mText;

        ViewHolder(View view) {
            mText = view.findViewById(R.id.text);
        }
    }
}
