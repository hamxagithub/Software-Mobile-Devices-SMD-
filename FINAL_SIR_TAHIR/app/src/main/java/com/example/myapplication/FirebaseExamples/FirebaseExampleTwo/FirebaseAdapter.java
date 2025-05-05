package com.example.myapplication.FirebaseExamples.FirebaseExampleTwo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.ArrayList;

public class FirebaseAdapter extends RecyclerView.Adapter<FirebaseAdapter.ViewHolder> {

    ArrayList<Student_BSAI> studentlist;

    public FirebaseAdapter(Context context, ArrayList<Student_BSAI> studentlist) {
        this.context = context;
        this.studentlist = studentlist;
    }

    Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.studentlistlayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
holder.textview.setText(studentlist.get(position).getName());
Glide.with(context).load(studentlist.get(position).getPicture()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return studentlist.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        TextView textview;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview=itemView.findViewById(R.id.textView);
            image=itemView.findViewById(R.id.imageView2);
        }
    }

}

