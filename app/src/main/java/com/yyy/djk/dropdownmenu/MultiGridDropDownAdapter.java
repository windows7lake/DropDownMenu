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

public class MultiGridDropDownAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private List<String> checkList;
    private List<String> confirmList;

    public void setCheckItem(int position) {
        if (checkList.contains(list.get(0))) {
            checkList.clear();
            checkList.add(list.get(position));
        } else if (position == 0) {
            checkList.clear();
            checkList.add(list.get(position));
        } else if (checkList.contains(list.get(position))) {
            checkList.remove(list.get(position));
        } else if (checkList.size() >= 2) {
            Toast.makeText(context, "最多選擇兩項", Toast.LENGTH_SHORT).show();
        } else {
            checkList.add(list.get(position));
        }
        notifyDataSetChanged();
    }

    // 获取选中的文本列表
    public List<String> getConfirmList() {
        confirmList.clear();
        confirmList.addAll(checkList);
        return confirmList;
    }

    public void resetList() {
        checkList.clear();
        checkList.addAll(confirmList);
        notifyDataSetChanged();
    }

    public MultiGridDropDownAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        this.checkList = new ArrayList<>();
        this.checkList.add(list.get(0));
        this.confirmList = new ArrayList<>();
        this.confirmList.add(list.get(0));
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_layout, null);
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
