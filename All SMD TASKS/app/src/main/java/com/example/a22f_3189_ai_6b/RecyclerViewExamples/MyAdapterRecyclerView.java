package com.example.a22f_3189_ai_6b.RecyclerViewExamples;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22f_3189_ai_6b.R;

import java.util.List;

public class MyAdapterRecyclerView extends RecyclerView.Adapter<MyAdapterRecyclerView.ViewHolder>{
    private List<MyMobile> myMobileList;

    public MyAdapterRecyclerView(List<MyMobile> myMobileList) {
        this.myMobileList = myMobileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mymobilelayout,parent, false);
        ViewHolder obj = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        MyMobile obj = myMobileList.get(position);

        holder.mobileName.setText(obj.getName());
        holder.mobileCompany.setText(obj.getCompany());
        holder.mobilePrice.setText(obj.getPrice());

    }

    @Override
    public int getItemCount() {

        return myMobileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mobileName, mobileCompany, mobilePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mobileName = itemView.findViewById(R.id.txtmobilename);
            mobileCompany = itemView.findViewById(R.id.txtmobilecompany);
            mobilePrice = itemView.findViewById(R.id.txtmobileprice);
        }
    }

}
