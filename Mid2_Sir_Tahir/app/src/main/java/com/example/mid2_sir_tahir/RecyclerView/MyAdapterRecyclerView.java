package com.example.mid2_sir_tahir.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mid2_sir_tahir.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterRecyclerView extends  RecyclerView.Adapter<MyAdapterRecyclerView.ViewHolder>{
    private List<Mobile> mobileList;

    public MyAdapterRecyclerView(List<Mobile> mobilList) {
        this.mobileList = mobileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mymobilelayout,parent,false);
        ViewHolder obj=new ViewHolder(view);
        return obj;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          Mobile mobile=mobileList.get(position);
          holder.mobilePrice.setText(mobile.getMobilePrice());
          holder.mobileCompany.setText(mobile.getMobileCompany());
          holder.mobileName.setText(mobile.getMobileName());
    }

    @Override
    public int getItemCount() {
        return mobileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mobileName,mobileCompany,mobilePrice;
        public ViewHolder(@NonNull View view) {
            super(view);
            mobileName=view.findViewById(R.id.txtmobilename);
            mobileCompany=view.findViewById(R.id.txtmobilecompany);
            mobilePrice=view.findViewById(R.id.txtmobileprice);
        }
    }
}
