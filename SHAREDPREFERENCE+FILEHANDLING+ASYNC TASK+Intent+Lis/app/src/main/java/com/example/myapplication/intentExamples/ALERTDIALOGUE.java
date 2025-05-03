package com.example.myapplication.intentExamples;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class ALERTDIALOGUE extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alertdialogue);
        textView = findViewById(R.id.txtshow);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void OPEN_ALERT_DIALOGUE(View view) {
        AlertDialog.Builder builder =new AlertDialog.Builder(ALERTDIALOGUE.this);
                builder.setMessage("Are You Sure ?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ALERTDIALOGUE.this, "ok  Click", Toast.LENGTH_SHORT).show();
                        textView.setText("0");
                        int value = Integer.parseInt(String.valueOf(textView.getText()));
                        value=value+1;
                        textView.setText(String.valueOf(value));
                    }
                }).setNegativeButton("Cancel",null);
                AlertDialog alert = builder.create();
                alert.show();
    }
}