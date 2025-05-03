package com.example.myapplication.intentExamples;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class intentSecondActivity extends AppCompatActivity {
    TextView txtvalue1, txtvalue2;
Button btn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intent_second);

        txtvalue1 = findViewById(R.id.txtintentvalue1);
        txtvalue2 = findViewById(R.id.txtintentvalue2);

       
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Calling method to handle intent data
        receiveIntentData();
    }


    private void receiveIntentData() {
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
            Log.e("IntentData", "Intent extras are null!");
            return;
        } else {
            Log.d("IntentData", "Intent received correctly");
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d("IntentData", "Key: " + key + " Value: " + extras.get(key));
            }
        }

        // Retrieve values using the correct keys
        int value1 = extras.getInt("Key1",-1);  // Ensure case matches from FirstActivity
        int value2 = extras.getInt("key2",-1);  // Ensure case matches from FirstActivity

        Log.d("IntentData", "Received Key1: " + value1 + ", key2: " + value2);

        // Display the received values
        txtvalue1.setText(String.valueOf(value1));
        txtvalue2.setText(String.valueOf(value2));
    }


}
