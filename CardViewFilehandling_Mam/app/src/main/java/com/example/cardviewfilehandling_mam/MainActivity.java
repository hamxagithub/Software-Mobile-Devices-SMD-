package com.example.cardviewfilehandling_mam;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        String data="This is a simple file in internet Storgae.";
        try {
            FileOutputStream fileOutputStream=openFileOutput("myfile.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
            Toast.makeText (this,"File written",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this,"error file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
        String filepath =getFilesDir().getPath()+"/myfile.txt";
        MediaScannerConnection.scanFile(this,new String[]{filepath},null,(path,url)->Toast.makeText(this,"Scanned",Toast.LENGTH_SHORT).show());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

