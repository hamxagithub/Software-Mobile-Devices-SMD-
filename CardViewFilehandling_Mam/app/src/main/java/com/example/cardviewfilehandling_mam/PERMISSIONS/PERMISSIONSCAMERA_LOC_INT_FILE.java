package com.example.cardviewfilehandling_mam.PERMISSIONS;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;


import com.example.cardviewfilehandling_mam.R;


public class PERMISSIONSCAMERA_LOC_INT_FILE extends AppCompatActivity {
    Button camerabtn,locbtn,intbtn,internalbtn,exterternalbtn;
    private static final int CAMERA_PERMISSION_REQUEST_CODE=1;
    private static final int LOC_PERMISSION_REQUEST_CODE=2;

    private static final int INTERNET_PERMISSION_REQUEST_CODE=3;

    private static final int External_PERMISSION_REQUEST_CODE=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_permissionscamera_loc_int_file);
        camerabtn=findViewById(R.id.camera_btn);
        locbtn=findViewById(R.id.loc_btn);
        intbtn=findViewById(R.id.int_btn);
        internalbtn=findViewById(R.id.internal_btn);
        exterternalbtn=findViewById(R.id.external_btn);
        camerabtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });
        locbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                requestLocationPermission();
            }

        });
        intbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             requestInternetPermission();
            }
        });
internalbtn.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick(View v) {
        readStoragePermission();
    }
});
exterternalbtn.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick(View v) {
        externalStoragePermission();
    }
});
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void requestCameraPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
        else
        {
            Toast.makeText(this, "CAMERA PERMISSION ALREADY GRANTED", Toast.LENGTH_SHORT).show();
        }
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOC_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Location permission already granted",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void requestInternetPermission() {
        Toast.makeText(this, "Internet permission is granted by default",
                Toast.LENGTH_SHORT).show();
    }
    public void readStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
            Toast.makeText(this, "  Read Permission granted", Toast.LENGTH_SHORT).show();
        }

    }
    public void externalStoragePermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.MANAGE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.MANAGE_EXTERNAL_STORAGE},External_PERMISSION_REQUEST_CODE);
            Toast.makeText(this, "EXTERNAL  STORAGE GRANTED", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "EXTERNAL Already STORAGE GRANTED", Toast.LENGTH_SHORT).show();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera Permission Granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Camera Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case LOC_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location Permission Granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case INTERNET_PERMISSION_REQUEST_CODE:
                break;
        }
    }


}