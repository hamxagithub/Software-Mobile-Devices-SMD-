package com.example.mid2_sir_tahir.SensorsExample;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mid2_sir_tahir.R;

public class SensorExample1 extends AppCompatActivity implements SensorEventListener {
TextView textView;
SensorManager sensorManager;
View v;
boolean color=false;
long recentTimeUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sensor_example1);
        textView=findViewById(R.id.txtsensor1);
        v=findViewById(R.id.main);
     recentTimeUpdate=System.currentTimeMillis();
     sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            getSensorValues(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void getSensorValues(SensorEvent event)
    {
        float[]values=event.values;
        float x=values[0];
        float y=values[1];
        float z=values[2];
        float acceleration = (x*x +y*y+z*z)/(SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH);
        long realtime=event.timestamp;
        if(acceleration>=2) {
            if (realtime - recentTimeUpdate < 150) {
                return;
            }
            recentTimeUpdate = realtime;
            textView.setText("Mobile was Moved........");
            if(color) {
                v.setBackgroundColor(Color.BLUE);
                color=true;
            }
            else {
                v.setBackgroundColor(Color.GREEN);
                color=true;
            }

        }
        }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_NORMAL);
    }
}
