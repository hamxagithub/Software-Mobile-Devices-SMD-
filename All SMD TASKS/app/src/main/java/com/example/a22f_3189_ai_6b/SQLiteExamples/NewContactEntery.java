package com.example.a22f_3189_ai_6b.SQLiteExamples;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.util.HashMap;

public class NewContactEntery extends AppCompatActivity {
    EditText firstName, secondName, phoneNumber, emailAdress, homeAdress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_contact_entery);
        firstName = findViewById(R.id.edtfirstname);
        secondName = findViewById(R.id.edtsecondname);
        phoneNumber = findViewById(R.id.edtphonenumber);
        emailAdress = findViewById(R.id.edtemail);
        homeAdress = findViewById(R.id.edthomeaddress);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void SaveIntoDatabase(View view) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("firstName", firstName.getText().toString());
        hashMap.put("secondName", secondName.getText().toString());
        hashMap.put("phoneNumber", phoneNumber.getText().toString());
        hashMap.put("emailAddress", emailAdress.getText().toString());
        hashMap.put("homeAddress", homeAdress.getText().toString());
        DbQueries dbQueries = new DbQueries(getApplicationContext());
        dbQueries.Insert(hashMap);

        Intent intent =new Intent(this, MainActivitySQlite.class);
        startActivity(intent);


    }
}