package com.example.mid2_sir_tahir.RecyclerView;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mid2_sir_tahir.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewExampleOneMainActivity extends AppCompatActivity {
RecyclerView recyclerView;
    List<Mobile> mobileList=new ArrayList<>();
    MyAdapterRecyclerView myAdapterRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recycler_view_example_one_main);
        recyclerView=findViewById(R.id.recyclerviewexample1);
            myAdapterRecyclerView=new MyAdapterRecyclerView(mobileList);
  RecyclerView.LayoutManager layoutManager=
          new LinearLayoutManager(getApplicationContext());
  recyclerView.setLayoutManager(layoutManager);
  recyclerView.setItemAnimator(new DefaultItemAnimator());
  recyclerView.setAdapter(myAdapterRecyclerView);
   LoadData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void LoadData()
    {
        Mobile mobile=new Mobile("Note 1 ","Samsung","5421");
        mobileList.add(mobile);
        mobile=new Mobile("Note 2 ","Samsung","5421");
        mobileList.add(mobile);
        myAdapterRecyclerView.notifyDataSetChanged();
    }
}