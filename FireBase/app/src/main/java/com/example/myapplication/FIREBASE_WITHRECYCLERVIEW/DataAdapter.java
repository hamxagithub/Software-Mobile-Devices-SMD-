package com.example.myapplication.FIREBASE_WITHRECYCLERVIEW;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<DataModel> dataList;

    public DataAdapter(List<DataModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        holder.tvName.setText(data.getName());
        holder.tvTitle.setText(data.getTitle());
        holder.tvDescription.setText(data.getDescription());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<DataModel> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTitle, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
