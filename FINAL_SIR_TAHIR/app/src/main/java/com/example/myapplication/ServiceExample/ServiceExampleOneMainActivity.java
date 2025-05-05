package com.example.myapplication.ServiceExample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class ServiceExampleOneMainActivity extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_example_one_main);
        intent = new Intent(this, MyServiceExampleOne.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void start_service(View view) {

        startService(intent);
        Log.d("TAG", "start_service: Service Started");
        // startForegroundService(intent);
        // startIntentService(intent);
        // startJobIntentService(intent);
        // startWorkManager(intent);
        // startJobScheduler(intent);
        // startAlarmManager(intent);


    }

    public void stop_service(View view) {
        stopService(intent);
        Log.d("TAG", "stop_service: Service Stopped");




    }
}