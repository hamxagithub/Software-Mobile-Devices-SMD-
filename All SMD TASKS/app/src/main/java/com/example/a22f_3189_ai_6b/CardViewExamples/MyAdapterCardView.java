package com.example.a22f_3189_ai_6b.CardViewExamples;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a22f_3189_ai_6b.R;

import java.util.List;

public class MyAdapterCardView extends RecyclerView.Adapter<MyAdapterCardView.ViewHolder> {
    public Context context;
    public List<MyModelCardView> list;

    public MyAdapterCardView(Context context, List<MyModelCardView> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycard,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyModelCardView obj = list.get(position);
        holder.name.setText(obj.getName());
        holder.totalDownloads.setText( String.valueOf(obj.getTotal_Download()));
        Glide.with(context).load(obj.getThumbnail()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView name, totalDownloads;

        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.txtcardviewone);
            totalDownloads = view.findViewById(R.id.txtcardviewtow);
            thumbnail = view.findViewById(R.id.imgcardview);
        }
    }
}
