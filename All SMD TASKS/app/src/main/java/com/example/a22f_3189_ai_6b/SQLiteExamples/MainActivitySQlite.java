package com.example.a22f_3189_ai_6b.SQLiteExamples;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivitySQlite extends AppCompatActivity {
    ListView listView;

    DbQueries dbQueries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_sqlite);
        listView = findViewById(R.id.lstmaincontactlist);
        dbQueries = new DbQueries(getApplicationContext());
        ArrayList<HashMap<String,String>> contactList = dbQueries.getAllContacts();
        SimpleAdapter adapter = new SimpleAdapter(this,contactList,R.layout.allcontactslayout, new String[]{"_id","firstName","secondName"}, new int[]{R.id.textViewID,R.id.textViewFirstName,R.id.textViewLastName} );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivitySQlite.this, EditContactActivity.class);
                intent.putExtra("id",String.valueOf(id+1));
                startActivity(intent);
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void AddContact(View view) {
        Intent intent = new Intent(this,NewContactEntery.class);
        startActivity(intent);
    }
}