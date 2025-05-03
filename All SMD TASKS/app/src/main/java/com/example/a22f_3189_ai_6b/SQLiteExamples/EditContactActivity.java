package com.example.a22f_3189_ai_6b.SQLiteExamples;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.util.HashMap;

public class EditContactActivity extends AppCompatActivity {
    DbQueries dbQueries;
    EditText id,editTextFirstName,editTextSecondName,editTextPhoneNumber,editTextEmailAddress,editTextHomeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_contact);
        editTextFirstName = findViewById(R.id.editfirstname);
        editTextSecondName =findViewById(R.id.editsecondname);
        editTextPhoneNumber = findViewById(R.id.editphonenumber);
        editTextEmailAddress = findViewById(R.id.editemailaddress);
        editTextHomeAddress = findViewById(R.id.edithomeaddress);

        dbQueries = new DbQueries(getApplicationContext());
        Intent intent = getIntent();
        String id = intent.getStringExtra("_id");
        HashMap<String,String> hashMap = dbQueries.getSingleRecord(id);

        if(hashMap.size()!=0)
        {
            editTextFirstName.setText(hashMap.get("firstName"));
            editTextSecondName.setText(hashMap.get("secondName"));
            editTextPhoneNumber.setText(hashMap.get("phoneNumber"));
            editTextEmailAddress.setText(hashMap.get("emailAddress"));
            editTextHomeAddress.setText(hashMap.get("homeAddress"));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}