package com.example.myapplication.CPFIREBASETASK;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CPMainActivity extends AppCompatActivity {
EditText name,cnic,roll,gpa;
Button btn1,btn2;
DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cpmain);
        name=findViewById(R.id.name);
        cnic=findViewById(R.id.cnic);
        roll=findViewById(R.id.roll);
        gpa=findViewById(R.id.cgpa);
        btn1=findViewById(R.id.submit);
        btn2=findViewById(R.id.show);
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://my-application-4e04a-default-rtdb.firebaseio.com/");
        databaseReference=firebaseDatabase.getReference("Student");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1,roll1,cnic1,gpa1;
                name1 = name.getText().toString();
                roll1=roll.getText().toString();
                cnic1=cnic.getText().toString();
                gpa1=gpa.getText().toString();
              Data data=new Data();
              data.setName(name1);
              data.setCnic(cnic1);
              data.setRoll(roll1);
              data.setGpa(gpa1);
                databaseReference.setValue(data);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Intent intent=new Intent(CPMainActivity.this, CPMainActivity2.class);

startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}