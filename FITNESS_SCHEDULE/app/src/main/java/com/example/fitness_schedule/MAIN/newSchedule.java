package com.example.fitness_schedule.MAIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitness_schedule.R;

import java.io.Serializable;

// Make schClass Serializable
class schClass implements Serializable {
    String day;
    String sport;

    public schClass(String day, String sport) {
        this.day = day;
        this.sport = sport;
    }
}

public class newSchedule extends AppCompatActivity {
    EditText txt1;
    EditText txt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_schedule);

        txt1 = findViewById(R.id.dayinput);
        txt2 = findViewById(R.id.sportinput);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Submit_form(View view) {
        // Create an instance of schClass with user input
        schClass obj = new schClass(
                txt1.getText().toString().trim(),
                txt2.getText().toString().trim()
        );


        Intent intent = new Intent(this, MAINPAGE.class);
        intent.putExtra("scheduleData", obj);
        startActivity(intent);
    }
}
