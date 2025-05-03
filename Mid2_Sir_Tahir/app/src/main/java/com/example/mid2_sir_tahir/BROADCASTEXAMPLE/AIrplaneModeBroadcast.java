package com.example.mid2_sir_tahir.BROADCASTEXAMPLE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class AIrplaneModeBroadcast extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        if(isAirplaneModeChange(context.getApplicationContext()))
        {
            Toast.makeText(context,"AIrplane MOde is ON",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context,"AIrplane MOde is OFF",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isAirplaneModeChange(Context context)
    {
        boolean a = Settings.System.getInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,0)!=0;
        return a;
    }
}
