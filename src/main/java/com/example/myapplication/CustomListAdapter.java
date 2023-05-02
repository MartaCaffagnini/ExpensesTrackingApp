package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private final ArrayList<ListItem> listData;
    private final LayoutInflater layoutInflater;

    public CustomListAdapter(Context context, ArrayList<ListItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.list_expense, null);
            holder = new ViewHolder();
            holder.title = v.findViewById(R.id.title);
            holder.category = v.findViewById(R.id.category);
            holder.amount = v.findViewById(R.id.amount);
            holder.date = v.findViewById(R.id.date);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.title.setText(listData.get(position).getTitle());
        holder.category.setText(listData.get(position).getCategory());
        holder.amount.setText(listData.get(position).getAmount());
        holder.date.setText(listData.get(position).getDate());

        return v;
    }

    static class ViewHolder {
        TextView title;
        TextView category;
        TextView amount;
        TextView date;
    }
}
