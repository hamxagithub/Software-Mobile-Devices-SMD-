package com.example.a22f_3189_ai_6b.Intentexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

public class Myfirstactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myfirstactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Go_to_second_activity(View view) {
        Intent intent = new Intent(this,Mysecondactivity.class);
        //intent.putExtra("K1", "Ali");
        //intent.putExtra("K2", "Ahmad");
        //intent.putExtra("K1", "11");
        //intent.putExtra("K2", "22");
        //intent.putExtra("K3", "33");
        //intent.putExtra("K4", "44");
        //intent.putExtra("K5", "55");
        startActivity(intent);
    }
}