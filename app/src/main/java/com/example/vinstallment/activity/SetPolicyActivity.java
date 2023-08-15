package com.example.vinstallment.activity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vinstallment.R;
import com.example.vinstallment.receiver.MyDeviceAdminReceiver;
import com.example.vinstallment.service.ThirdPunishmentService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.widget.Toast;

public class SetPolicyActivity extends AppCompatActivity {
    private static final String CAMERA_PACKAGE_NAME = "com.android.camera";
    private MediaPlayer mediaPlayer;

    private Switch switchH1;
    private Switch switchH0;
    private Switch switchHplus1;
    private Switch switchHplus2;
    private Switch switchHplus3;
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID_H1 = 1;
    private final int NOTIFICATION_ID_H0 = 2;
    private boolean isCameraEnabled = true;

    private ThirdPunishmentService soundService;
    private boolean isServiceBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ThirdPunishmentService.LocalBinder binder = (ThirdPunishmentService.LocalBinder) iBinder;
            soundService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_policy);

        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);
        // Initialize views and notification manager
        switchH1 = findViewById(R.id.switchH_1);
        switchH0 = findViewById(R.id.switchH_0);
        switchHplus1 = findViewById(R.id.switchH_plus_1);
        switchHplus2 = findViewById(R.id.switchH_plus_2);
        switchHplus3 = findViewById(R.id.switchH_plus_3);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "my_channel_id"; // Choose a unique channel ID
            CharSequence channelName = "My Channel"; // Choose a channel name
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(this, ThirdPunishmentService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Set click listener for the save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your save logic here
            }
        });

        // Set switchH_1 state change listener
        switchH1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                if (isChecked) {
                    switchH1.setChecked(true);
                    switchH0.setChecked(false);
                    switchHplus1.setChecked(false);
                    switchHplus2.setChecked(false);
                    switchHplus3.setChecked(false);
                }

                // Manage notification based on switch state
                if (isChecked) {
                    // Display notification
                    showNotification("Besok adalah tenggat pembayaran!",
                            "Segera lakukan pembayaran sebelum tenggat pembayaran agar hpmu tetap berfungsi dengan baik",
                            NOTIFICATION_ID_H1);
                } else {
                    // Remove notification
                    removeNotification(NOTIFICATION_ID_H1);
                }
            }
        });

        switchH0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                if (isChecked) {
                    switchH0.setChecked(true); // Keep this switch on
                    switchH1.setChecked(false);
                    switchHplus1.setChecked(false);
                    switchHplus2.setChecked(false);
                    switchHplus3.setChecked(false);
                    // Turn off other switches
                    // Implement similar code for other switches
                }

                // Manage notification based on switch state
                if (isChecked) {
                    // Display notification
                    showNotification("Hari ini adalah tenggat pembayaran!", "Segera lakukan pembayaran agar hpmu tetap berfungsi dengan baik", NOTIFICATION_ID_H0);
                } else {
                    // Remove notification
                    removeNotification(NOTIFICATION_ID_H0);
                }
            }
        });

        switchHplus1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                if (isChecked) {
                    switchHplus1.setChecked(true);
                    switchH0.setChecked(false);
                    switchH1.setChecked(false);
                    switchHplus2.setChecked(false);
                    switchHplus3.setChecked(false);
                    // Turn off other switches
                    // Implement similar code for other switches
                }

                // Manage notification based on switch state
                if (isChecked) {
                    // Display notification
                    devicePolicyManager.setCameraDisabled(adminComponent, true);
                    Toast.makeText(SetPolicyActivity.this, "Camera disabled", Toast.LENGTH_SHORT).show();

                } else {
                    // Remove notification
                    devicePolicyManager.setCameraDisabled(adminComponent, false);
                    Toast.makeText(SetPolicyActivity.this, "Camera enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchHplus2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                if (isChecked) {
                    switchHplus2.setChecked(true);
                    switchH0.setChecked(false);
                    switchHplus1.setChecked(false);
                    switchH1.setChecked(false);
                    switchHplus3.setChecked(false);
                    // Turn off other switches
                    // Implement similar code for other switches
                }

                // Manage notification based on switch state
                if (isChecked) {

                } else {
                    // Cancel the timer when the switch is turned off

                }
            }
        });

        switchHplus3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                if (isChecked) {
                    switchHplus3.setChecked(true);
                    switchH0.setChecked(false);
                    switchHplus1.setChecked(false);
                    switchHplus2.setChecked(false);
                    switchH1.setChecked(false);
                    // Turn off other switches
                    // Implement similar code for other switches
                }

                // Manage notification based on switch state
                if (isChecked) {
                    // Display notification
                    soundService.startPlaying();
                } else {
                    // Cancel the timer when the switch is turned off
                    soundService.stopPlaying();
                }
            }
        });
    }

    // Method to show notification
    private void showNotification(String title, String subtitle, int notif_id) {
        Intent notificationIntent = new Intent(this, SetPolicyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setContentIntent(contentIntent)// Set priority to high
                .setOngoing(true);


        Notification notification = builder.build();
        notificationManager.notify(notif_id, notification);
    }

    private void removeNotification(int id) {
        notificationManager.cancel(id);
    }
}

