package com.example.cardviewfilehandling_mam.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardviewfilehandling_mam.R;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private final List<Students> studentsList;

    public StudentAdapter(List<Students> studentsList) {
        this.studentsList = studentsList;
    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {
        Students students = studentsList.get(position);
        holder.nameTxt.setText(students.getName());
        holder.img.setImageResource(students.getImageResId());
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.img);
        }
    }
}
