package com.example.vinstallment.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.vinstallment.R;

public class ThirdPunishmentService extends Service {
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public ThirdPunishmentService getService() {
            return ThirdPunishmentService.this;
        }
    }

    private Runnable playSoundRunnable = new Runnable() {
        @Override
        public void run() {
            mediaPlayer.start(); // Play the sound
            handler.postDelayed(this, 5000); // Repeat every 30 seconds
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.payment_sound);
        mediaPlayer.setLooping(true);
        Notification notification = createNotification(); // Implement this method
        startForeground(3, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startPlaying() {
        playSoundRunnable.run();
    }

    public void stopPlaying() {
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
    }

    private Notification createNotification() {
        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "my_channel_id",
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            // Set other channel properties if needed
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.icon) // Replace with your own icon
                .setContentTitle("Segera lakukan pembayaran!")
                .setContentText("Pembayaranmu sudah terlambat, segera lakukan pembayaran agar hpmu kembali berfungsi dengan baik")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create the notification
        return builder.build();
    }
}
