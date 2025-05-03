package com.example.a22f_3189_ai_6b.SharedPrefrenceExamples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

public class SharedPrefExampleOne extends AppCompatActivity {
    EditText editText;
    TextView textview;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shared_pref_example_one);
        editText = findViewById(R.id.edtsharedpref);
        textview = findViewById(R.id.txtsharedpref);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("MyPrefFile", 0);
    }

    public void Save_pref(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
       /* editor.putString("K1", "First Value");
        editor.putString("K2", "Second Value");
        editor.commit();*/
        String value = editText.getText().toString();
        editor.putString("K4", value);
        editor.commit();
    }

    public void Show_pref(View view) {
        //String value1 = sharedPreferences.getString("K1", "No Value");
        //String value2 = sharedPreferences.getString("K2", "No Value");
        //textview.setText(value1 + "" + value2);
        textview.setText(sharedPreferences.getString("K4", "No Value"));
    }
}