package com.example.fitness_schedule.MAIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitness_schedule.R;

public class viewschedule extends AppCompatActivity {
    TextView txt1, txt2;
    schClass obj1; // Declare obj1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_viewschedule);

        txt1 = findViewById(R.id.displayday);
        txt2 = findViewById(R.id.displaysport);

        // Retrieve the schClass object from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedSchedule")) {
            obj1 = (schClass) intent.getSerializableExtra("selectedSchedule");

            // Ensure obj1 is not null before accessing its properties
            if (obj1 != null) {
                txt1.setText(obj1.day);
                txt2.setText(obj1.sport);
            }
        }

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void GO_TO_BACK(View view) {
        Intent intent = new Intent(this, MAINPAGE.class);
        startActivity(intent);
    }
}
