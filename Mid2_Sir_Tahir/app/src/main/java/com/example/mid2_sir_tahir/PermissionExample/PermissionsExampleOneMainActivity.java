package com.example.mid2_sir_tahir.PermissionExample;

import static android.Manifest.permission.CAMERA;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mid2_sir_tahir.R;

public class PermissionsExampleOneMainActivity extends AppCompatActivity {
TextView textView;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_permissions_example_one_main);
        textView=findViewById(R.id.txtpermissionone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void RequestForPermission(View view) {
        if(!Check_Permission())
        {
            Request_Permission();
        }
        else {
            textView.setText("Permission Already Granted");
        }
    }

    public void CheckForPermission(View view) {
    }
    public boolean Check_Permission()
    {
        int r1 = ContextCompat.checkSelfPermission(this,CAMERA);
        return r1== PackageManager.PERMISSION_GRANTED;
    }
    public void Request_Permission()
    {
        ActivityCompat.requestPermissions(this,new String []{CAMERA},101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
    if(requestCode==101 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
    {
        textView.setText("Permission Granted");
    }
    }
}