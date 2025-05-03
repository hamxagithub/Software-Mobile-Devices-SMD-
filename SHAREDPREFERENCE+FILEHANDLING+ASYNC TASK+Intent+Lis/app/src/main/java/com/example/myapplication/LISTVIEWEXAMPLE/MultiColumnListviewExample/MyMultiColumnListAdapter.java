package com.example.myapplication.LISTVIEWEXAMPLE.MultiColumnListviewExample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MyMultiColumnListAdapter extends BaseAdapter {
    public ArrayList<HashMap<String,String>> list;
    Activity activity;

    public MyMultiColumnListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        this.activity = activity;
        this.list = list;
    }

    public static final String First_column="First";
    public static final String Second_column="Second";
    public static final String Third_column="Third";
    public static final String Forth_column="Forth";

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = activity.getLayoutInflater();
        if(view == null)
        {
            view = inflater.inflate(R.layout.multicolumnlistview,null);
            viewHolder.textViewfirst =view.findViewById(R.id.txtlistfirst);
            viewHolder.textViewsecond = view.findViewById(R.id.txtlistsecond);
            viewHolder.textViewthird =view.findViewById(R.id.txtlistthird);
            viewHolder.textViewforth=view.findViewById(R.id.txtlistforth);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)  view.getTag();
        }
        HashMap<String,String> hashMap =list.get(position);
        viewHolder.textViewfirst.setText(hashMap.get(First_column));
        viewHolder.textViewsecond.setText(hashMap.get(Second_column));
        viewHolder.textViewthird.setText(hashMap.get(Third_column));
        viewHolder.textViewforth.setText(hashMap.get(Forth_column));
           return  view;
    }

    public  class    ViewHolder{
        TextView textViewfirst,textViewsecond,textViewthird,textViewforth;
    }
}
