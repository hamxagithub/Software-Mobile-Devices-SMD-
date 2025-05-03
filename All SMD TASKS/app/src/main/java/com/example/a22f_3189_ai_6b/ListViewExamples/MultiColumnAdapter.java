package com.example.a22f_3189_ai_6b.ListViewExamples;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a22f_3189_ai_6b.R;

import java.util.ArrayList;
import java.util.HashMap;
public class MultiColumnAdapter extends BaseAdapter {
    public static final String First_Column = "First";
    public static final String Second_Column= "Senond";
    public static final String Third_Column= "Third";
    Activity context;
    public ArrayList<HashMap<String , String>> list;

    public MultiColumnAdapter(Activity context, ArrayList<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
    }

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

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = context.getLayoutInflater();
        if(view == null)
        {
            view = inflater.inflate(R.layout.multicolumnlayout,null);
            viewHolder.textView1 = view.findViewById(R.id.txtmultione);
            viewHolder.textView2 = view.findViewById(R.id.txtmultitwo);
            viewHolder.textView3 = view.findViewById(R.id.txtmultithree);
            view.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) view.getTag();
        HashMap<String,String>hashMap = list.get(position);
        viewHolder.textView1.setText(hashMap.get(First_Column));
        viewHolder.textView2.setText(hashMap.get(Second_Column));
        viewHolder.textView3.setText(hashMap.get(Third_Column));
        return view;
    }
    public class ViewHolder{
        TextView textView1 , textView2 , textView3;
    }
}