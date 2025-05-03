package com.example.a22f_3189_ai_6b.Intentexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

public class Mysecondactivity extends AppCompatActivity {
    TextView textView1, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mysecondactivity);
        textView1 = findViewById(R.id.textsecondactivity);
        textView2 = findViewById(R.id.texttwosecondactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        //textView1.setText(intent.getStringExtra("K1"));
        //textView2.setText(intent.getStringExtra("K2"));
        //int n1 = Integer.parseInt();
    }
}