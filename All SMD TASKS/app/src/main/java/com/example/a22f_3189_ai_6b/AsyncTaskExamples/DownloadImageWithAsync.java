package com.example.a22f_3189_ai_6b.AsyncTaskExamples;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageWithAsync extends AppCompatActivity {
     ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_download_image_with_async);
        imageView = findViewById(R.id.imgasyncone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Download_Image(View view) {
        DownloadNewImage obj = new DownloadNewImage();
       // Bitmap bitmap = obj.execute(https://www.google.com/search?q=img+cat&rlz=1C1CHBF_enPK1147PK1147&oq=img+cat&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIJCAEQABgKGIAEMgkIAhAAGAoYgAQyCQgDEAAYChiABDIJCAQQABgKGIAEMgkIBRAAGAoYgAQyCQgGEAAYChiABDIJCAcQABgKGIAEMgkICBAAGAoYgAQyCQgJEAAYChiABNIBCDMxMTFqMGo3qAIAsAIA&sourceid=chrome&ie=UTF-8#vhid=PMXGQD25JR7jIM&vssid=_NYO1Z86UK_PPhbIPv56J0Aw_38).get();
    }

    class DownloadNewImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url;
            try {
                url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

           // return null;
        }
    }
}