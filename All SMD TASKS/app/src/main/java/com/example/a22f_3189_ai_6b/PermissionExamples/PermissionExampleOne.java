package com.example.a22f_3189_ai_6b.PermissionExamples;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.a22f_3189_ai_6b.R;

public class PermissionExampleOne extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_permission_example_one);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Check_Permission(View view) {
        if(!CheckPermission())
        {
            RequestPermission();
        }
    }

    public void Request_Permission(View view) {
        if(CheckPermission()){
            Toast.makeText(this, "Permissions Already Granted", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean CheckPermission(){
        int P1 = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        int P2 = ContextCompat.checkSelfPermission(this, CAMERA);
        return P1 == PackageManager.PERMISSION_GRANTED && P2 == PackageManager.PERMISSION_GRANTED;
    }

    public void RequestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,CAMERA},101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        if(requestCode == 101 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

    }
}