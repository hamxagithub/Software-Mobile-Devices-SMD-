package com.example.myapplication.intentExamples;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.android.material.snackbar.Snackbar;

public class intentFirstActivity extends AppCompatActivity {
TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intent_first);

        textview = findViewById(R.id.txtaddone);

        textview.setText("0");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }


    public void Add_One(View view) {
        int value = Integer.parseInt(String.valueOf(textview.getText()));
        value=value+1;
        textview.setText(String.valueOf(value));



    }

    public void sendDataTo_secondActivity(View view) {
        Intent intent = new Intent(this, intentSecondActivity.class);
        intent.putExtra("Key1","50");
        intent.putExtra("Key2", "20");

        Log.d("IntentData", "Sending Key1: 50, key2: 20");
        startActivityForResult(intent, 1);

    }

    public void Show_Toast(View view)
    {
        Toast.makeText(this, "My Toast", Toast.LENGTH_SHORT).show();

        Snackbar.make(view,"MY TOAST",Snackbar.LENGTH_SHORT).show();


    }
}