package com.example.class_smd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class datapassing extends AppCompatActivity {
    private EditText inputtxt;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datapassing);
        inputtxt = findViewById(R.id.id1);
         button = findViewById(R.id.btnid1);
        button.setOnClickListener(v->{
            String passtxt = inputtxt.getText().toString().trim();
            Intent intent = new Intent(this, datareceving.class);
            intent.putExtra("EXTRA_TEXT",passtxt);
            startActivity(intent);

        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}