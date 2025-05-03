package com.example.fitness_schedule.MAIN;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.example.fitness_schedule.R;

import java.util.ArrayList;

public class MAINPAGE extends AppCompatActivity {
    ListView listView;
    static ArrayList<String> scheduleList = new ArrayList<>(); // Static to persist across activities
    ArrayAdapter<String> arrayAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainpage);

        listView = findViewById(R.id.lstsimple);

        // Get the new schedule object from the Intent
        Intent intent = getIntent();
        schClass obj = (schClass) intent.getSerializableExtra("scheduleData");

        if (obj != null) {
            scheduleList.add(obj.day);
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleList);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Get the selected object
                Intent intent1 = new Intent(MAINPAGE.this, viewschedule.class);
                intent1.putExtra("selectedSchedule", obj); // Pass entire object
                startActivity(intent1);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void GO_TO_NEW_PAGE(View view) {
        Intent intent = new Intent(this, newSchedule.class);
        startActivity(intent);
    }
}
