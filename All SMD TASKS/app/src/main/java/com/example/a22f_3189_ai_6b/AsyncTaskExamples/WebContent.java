package com.example.a22f_3189_ai_6b.AsyncTaskExamples;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebContent extends AppCompatActivity {
    EditText editText;
    TextView textView;
    String MyFileName = "MyFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_content);
        editText = findViewById(R.id.edtwebcontent);
        textView = findViewById(R.id.txtwebcontent);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Save_Inot_File(View view) throws IOException {
        String Data = editText.getText().toString();
        FileOutputStream FOS;
        try {
            FOS = openFileOutput("MyFileName", Context.MODE_PRIVATE);
            FOS.write(Data.getBytes());
            editText.setText("");
            Toast.makeText(this, "Data Successfully writen in the file.", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void Show_From_File(View view) throws IOException {
        String ReadData = "";
        try {
            FileInputStream inputStream = openFileInput(MyFileName);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp = bufferedReader.readLine();
            if(temp == null)
            {
                Toast.makeText(this, "File is empty.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                while(temp != null){
                    ReadData = ReadData + temp;
                    temp = bufferedReader.readLine();
                }
                bufferedReader.close();
                reader.close();
                inputStream.close();
            }
            textView.setText(ReadData);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}