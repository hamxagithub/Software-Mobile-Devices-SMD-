package com.example.mid2_sir_tahir.SQLiteExample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mid2_sir_tahir.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivitySQLite extends AppCompatActivity {
ListView listView;
DBQueries dbQueries;
ArrayList<HashMap<String,String>> allcontacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_sqlite);
        listView=findViewById(R.id.lstmaincontactlist);
        dbQueries=new DBQueries(getApplicationContext());
        allcontacts=dbQueries.getAllContacts();
        SimpleAdapter adapter=new SimpleAdapter(this,allcontacts,R.layout.allcontactslayout,new String[]{
                "_id","firstName","secondName"},new int []{R.id.textViewID,R.id.textViewFirstName,R.id.textViewLastName} );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> selectedContact = allcontacts.get(position);
            String contactId = selectedContact.get("_id");

            Intent intent = new Intent(MainActivitySQLite.this, editActivity.class);
            intent.putExtra("contact_id", contactId);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void AddContact(View view) {
        Intent intent=new Intent(this, NewContactActivity.class);
        startActivity(intent);
    }
}