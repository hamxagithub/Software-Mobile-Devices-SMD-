package com.example.myapplication.MUSICNOTIFICATION;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

public class MusicServices extends Service {

    private MediaPlayer mediaPlayer;
    private final MusicBinder binder = new MusicBinder();

    private static final String CHANNEL_ID = "MusicChannel";
    private static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) binder;
    }

    public class MusicBinder extends Binder {
        public MusicServices getServices() {
            return MusicServices.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if ("ACTION_PLAY".equals(action)) {
            play();
        } else if ("ACTION_PAUSE".equals(action)) {
            pause();
        }
        return START_STICKY;
    }

    @SuppressLint("ForegroundServiceType")
    public void play() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.nokkkk);
            mediaPlayer.setLooping(true);
        }
        mediaPlayer.start();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopForeground(false);
        }
    }

    private Notification createNotification() {
        Intent playIntent = new Intent(this, MusicServices.class);
        playIntent.setAction("ACTION_PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, MusicServices.class);
        pauseIntent.setAction("ACTION_PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText("Playing: nokkkk")
                .setSmallIcon(R.drawable.ic_music_note)
                .addAction(R.drawable.ic_play_arrow, "Play", playPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Music Playback", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
        super.onDestroy();
    }

}