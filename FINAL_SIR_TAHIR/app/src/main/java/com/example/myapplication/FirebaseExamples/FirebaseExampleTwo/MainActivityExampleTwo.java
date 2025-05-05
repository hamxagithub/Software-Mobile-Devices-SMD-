package com.example.myapplication.FirebaseExamples.FirebaseExampleTwo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityExampleTwo extends AppCompatActivity {
RecyclerView recyclerView;
DatabaseReference myReference;
ArrayList<Student_BSAI> studentlist;
    FirebaseDatabase database;
FirebaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_example_two);
        recyclerView=findViewById(R.id.firebaserecyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        studentlist=new ArrayList<>();
        database= FirebaseDatabase.getInstance("https://my-application-37a96-default-rtdb.firebaseio.com/");
       
       myReference=database.getReference("Student");
       insertData();
       ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void insertData() {
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Student_BSAI obj=new Student_BSAI();
                    obj.setName(dataSnapshot.child("Name").getValue().toString());
                    obj.setPicture(dataSnapshot.child("Picture").getValue().toString());

               studentlist.add(obj);
                }
                adapter =new FirebaseAdapter(getApplicationContext(),studentlist);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}