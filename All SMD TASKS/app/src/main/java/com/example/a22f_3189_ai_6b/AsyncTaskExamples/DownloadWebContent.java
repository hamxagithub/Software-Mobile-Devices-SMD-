package com.example.a22f_3189_ai_6b.AsyncTaskExamples;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class DownloadWebContent extends AppCompatActivity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_download_web_content);
        editText = findViewById(R.id.edtmultilinewebcontetn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Download_WebContent(View view)
    {
        WebContentDownload obj = new WebContentDownload();
        try {
            String myWebContent = obj.execute("https://nu.edu.pk/").get();
            Log.d("TAG", "Back in Main");
            editText.setText(myWebContent);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    class WebContentDownload extends AsyncTask<String, Void, String>
    {


        @Override
        protected String doInBackground(String... strings) {
            Log.d("Tag", "doInBackground in progress...");
            try {
                URL url = new URL(strings[0]);
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    int myData = reader.read();
                    String webContent = "";
                    while(myData != -1)
                    {
                        char ch = (char) myData;
                        webContent = webContent + ch;
                        myData = reader.read();
                    }
                    return webContent;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}