package com.example.a22f_3189_ai_6b.ListViewExamples;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

public class ListViewExampleOne extends AppCompatActivity {
    ListView listview;
    String[] list =  {"Hashim Yaseen", "Asif Ameer", "Ali", "Ahmad", "Zubair","Saad","Numan","Faizan","Iftikhar", "Nawaz Sharif", "Bilawal Zardari", "Mariam Nawaz", "Asim Muneer"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view_example_one);
        listview = findViewById(R.id.listexampleone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListViewExampleOne.this, "Item "+position+" clicked", Toast.LENGTH_SHORT).show();
            }
        });
        listview.setAdapter(adapter);
    }
}