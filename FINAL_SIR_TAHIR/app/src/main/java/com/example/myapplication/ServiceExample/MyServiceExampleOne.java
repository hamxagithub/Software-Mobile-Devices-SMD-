package com.example.myapplication.ServiceExample;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.Provider;

public class MyServiceExampleOne extends Service {
    private Handler handler;
    private Runnable runnable;
    private int counter = 0;
    private boolean iscount = false;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        handler = new Handler();
        iscount = true;
        startCounting();
    }

    private void startCounting() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (iscount) {
                    counter++;
                    Log.d("TAG", "Counter: " + counter);
                    handler.postDelayed(this, 2000);
                }
            }
        };
        handler.post(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


    return  START_NOT_STICKY;}

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "My Service is Destroyed", Toast.LENGTH_SHORT).show();
        iscount = false;
        handler.removeCallbacks(runnable);
        Log.d("TAG", "onDestroy: Service Destroyed");
    }
}
