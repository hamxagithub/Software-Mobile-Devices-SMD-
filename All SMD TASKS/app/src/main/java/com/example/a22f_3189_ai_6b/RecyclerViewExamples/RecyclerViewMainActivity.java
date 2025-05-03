package com.example.a22f_3189_ai_6b.RecyclerViewExamples;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22f_3189_ai_6b.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewMainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<MyMobile> myMobileList = new ArrayList<>();
    MyAdapterRecyclerView adapterobj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recycler_view_main);
        recyclerView = findViewById(R.id.myrecyclerviewone);
        adapterobj = new MyAdapterRecyclerView(myMobileList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterobj);
        MakeMobileObject();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void MakeMobileObject(){
        MyMobile obj = new MyMobile("Note 1", "Samsung", "50000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 2", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 3", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 4", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 5", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 6", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 7", "Samsung", "70000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 8", "Samsung", "80000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 9", "Samsung", "90000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 10", "Samsung", "100000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 11", "Samsung", "110000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 12", "Samsung", "120000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 13", "Samsung", "130000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 14", "Samsung", "140000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 15", "Samsung", "150000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 16", "Samsung", "160000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 17", "Samsung", "170000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 18", "Samsung", "180000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 19", "Samsung", "190000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 20", "Samsung", "200000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 2", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 2", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 2", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 2", "Samsung", "60000");
        myMobileList.add(obj);
        obj = new MyMobile("Note 2", "Samsung", "60000");
        myMobileList.add(obj);


        adapterobj.notifyDataSetChanged();

    }
}