package com.example.weekend.INTENTF;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weekend.R;

public class INTENTFIRST extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intentfirst);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btn = findViewById(R.id.abd);

        // Set an OnClickListener for the Button
        btn.setOnClickListener(v -> Toast.makeText(this, "Click by you toaster dgu", Toast.LENGTH_SHORT).show());
        Intent intent;
        intent = new Intent(this, INTENTSECOND.class);
        startActivity(intent);

    }
}

