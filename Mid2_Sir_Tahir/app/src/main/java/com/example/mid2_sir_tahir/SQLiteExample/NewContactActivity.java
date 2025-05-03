package com.example.mid2_sir_tahir.SQLiteExample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mid2_sir_tahir.R;

import java.util.HashMap;

public class NewContactActivity extends AppCompatActivity {
EditText firstName,secondName,phoneNumber,homeAddress,emailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_contact);
        firstName=findViewById(R.id.edtfirstname);
        secondName=findViewById(R.id.edtsecondname);
        phoneNumber=findViewById(R.id.edtphonenumber);
        emailAddress=findViewById(R.id.edtemail);
        homeAddress=findViewById(R.id.edthomeaddress);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void AddToDataBase(View view) {
        HashMap<String,String >contact=new HashMap<>();
        contact.put("firstName",firstName.getText().toString());
        contact.put("secondName",secondName.getText().toString());
        contact.put("emailAddress",emailAddress.getText().toString());
        contact.put("phoneNumber",phoneNumber.getText().toString());
        contact.put("homeaAddress",homeAddress.getText().toString());
        DBQueries dbQueries=new DBQueries(getApplicationContext());
        dbQueries.Insert(contact);
        Intent intent=new Intent(this,MainActivitySQLite.class);
        startActivity(intent);
    }
}