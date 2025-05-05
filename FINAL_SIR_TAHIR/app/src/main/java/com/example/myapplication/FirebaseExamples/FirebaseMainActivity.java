package com.example.myapplication.FirebaseExamples;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.FirebaseExamples.FirebaseExampleTwo.FirebaseAdapter;
import com.example.myapplication.FirebaseExamples.FirebaseExampleTwo.Student_BSAI;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseMainActivity extends AppCompatActivity {
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_firebase_main);
        FirebaseDatabase database=FirebaseDatabase.getInstance( "https://my-application-37a96-default-rtdb.firebaseio.com/");
        databaseReference=database.getReference();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
   //   databaseReference.child("BSAI(A)").child("android").setValue("SMD");
// Test_User testUser=new Test_User();
//  testUser.setName("Ali");
//  testUser.setEmail("abcd@cfd.nu.edu");
//  databaseReference.setValue(testUser);
//        databaseReference.child("BSAI").child("SMD").child("S1").setValue("ALi");
//  databaseReference.child("BSAI").child("Recommender System").child("S1").setValue("Zubair");

       // databaseReference.child("BSAI").child("SMD").child("S2").setValue("ALia");

        //databaseReference.child("BSAI").child("SMD").child("S3").setValue("Ahad");
//   databaseReference.child("BS-AI-6A").child("S1").child("Name").setValue("Ali1");
//        databaseReference.child("BS-AI-6A").child("S2").child("Name").setValue("Ali2");
//        databaseReference.child("BS-AI-6A").child("S3").child("Name").setValue("Ali3");
//        databaseReference.child("BS-AI-6A").child("S4").child("Name").setValue("Ali4");
//        databaseReference.child("BS-AI-6A").child("S5").child("Name").setValue("Al5");
//        databaseReference.child("BS-AI-6A").child("S1").child("Course").setValue("SMD1");
//       databaseReference.child("BS-AI-6A").child("S2").child("Course").setValue("SMD2");
//       databaseReference.child("BS-AI-6A").child("S3").child("Course").setValue("SMD3");
//        databaseReference.child("BS-AI-6A").child("S4").child("Course").setValue("SMD4");
      databaseReference.child("BS-AI-6A").child("S5").child("Course").setValue("SMD5");
        databaseReference.child("BS-AI-6A").child("S1").child("GPA").setValue("2.9");
        databaseReference.child("BS-AI-6A").child("S2").child("GPA").setValue("2.8");
        databaseReference.child("BS-AI-6A").child("S3").child("GPA").setValue("2.7");
       databaseReference.child("BS-AI-6A").child("S4").child("GPA").setValue("2.6");
        databaseReference.child("BS-AI-6A").child("S1").child("Email").setValue("ali@cfd.nu.edu.pk");
      databaseReference.child("BS-AI-6A").child("S2").child("Email").setValue("ahmad@cfd.nu.edu.pk");
      databaseReference.child("BS-AI-6A").child("S3").child("Email").setValue("s3@cfd.nu.edu.pk");
      databaseReference.child("BS-AI-6A").child("S4").child("Email").setValue("ali4@cfd.nu.edu.pk");

getRecord();

    }
    public void getRecord()
    {


//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    if(snapshot.getValue()!=null)
//                    {
//                        for(DataSnapshot datasnapshot : snapshot.getChildren() )
//                        {
//                            String value =datasnapshot.getValue().toString();
//                              Log.d("TAG"," "+value);
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}