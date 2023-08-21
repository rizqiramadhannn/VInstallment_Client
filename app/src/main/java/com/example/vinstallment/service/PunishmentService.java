package com.example.vinstallment.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.vinstallment.R;
import com.example.vinstallment.VInstallmentAIDL;
import com.example.vinstallment.receiver.MyDeviceAdminReceiver;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PunishmentService extends Service {
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private SharedPreferences punishmentSharedPreferences;
    private boolean firstPunishment;
    private boolean secondPunishment;
    private boolean thirdPunishment;


    private boolean isEssentialApp(String packageName) {
        return packageName.equals("com.android.contacts") ||
                packageName.equals("com.android.mms") ||
                packageName.equals("com.whatsapp") ||
                packageName.equals("com.android.phone") ||
                packageName.equals("com.example.vinstallment_server") ||
                packageName.equals("com.android.settings");
    }

    private Runnable playSoundRunnable = new Runnable() {
        @Override
        public void run() {
            mediaPlayer.start();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
            }, mediaPlayer.getDuration());

            handler.postDelayed(this, 3600000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("TextView", MODE_PRIVATE);
        punishmentSharedPreferences = getSharedPreferences("Punishment", MODE_PRIVATE);
        firstPunishment = punishmentSharedPreferences.getBoolean("FirstPunishment", false);
        secondPunishment = punishmentSharedPreferences.getBoolean("SecondPunishment", false);
        thirdPunishment = punishmentSharedPreferences.getBoolean("ThirdPunishment", false);
        mediaPlayer = MediaPlayer.create(this, R.raw.payment_sound);
        mediaPlayer.setLooping(true);
        handler.removeCallbacks(playSoundRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Notification createNotification(String title, String subtitle, int notif_id) {
        NotificationChannel channel = new NotificationChannel(
                "my_channel_id",
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setOngoing(true);

        return builder.build();
    }

    private String[] getPkgList(){
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        List<String> packageNamesList = new ArrayList<>();

        for (PackageInfo packageInfo : installedPackages) {
            String packageName = packageInfo.packageName;
            Log.d("PackageList", "Package Name: " + packageName);
            if (!isEssentialApp(packageName)) {
                packageNamesList.add(packageName);
            }
        }
        return packageNamesList.toArray(new String[0]);
    }

    private final VInstallmentAIDL.Stub mBinder = new VInstallmentAIDL.Stub() {

        @Override
        public void showNotif(String title, String subtitle, int id) throws RemoteException {
            Notification notification = createNotification(title, subtitle, id); // Implement this method
            startForeground(id, notification);
        }

        public void removeNotif() throws RemoteException {
            stopForeground(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.cancel(3);
        }

        public void disableCamera(boolean status){
            firstPunishmentStatus(status);
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);
            devicePolicyManager.setCameraDisabled(adminComponent, status);
        }

        public void suspendApps(boolean status){
            secondPunishmentStatus(status);
            String[] packageNamesArray = getPkgList();
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);
            devicePolicyManager.setPackagesSuspended(adminComponent, packageNamesArray, status);
            devicePolicyManager.setApplicationHidden(adminComponent, "com.android.vending", status);
        }

        @Override
        public void stopPlaying() throws RemoteException {
            thirdPunishmentStatus(false);
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            handler.removeCallbacks(playSoundRunnable);
        }

        @Override
        public void startPlaying() throws RemoteException {
            thirdPunishmentStatus(true);
            playSoundRunnable.run();
        }

        public void firstPunishmentStatus(boolean status){
            SharedPreferences.Editor editor = punishmentSharedPreferences.edit();
            editor.putBoolean("FirstPunishment", status);
            editor.apply();
            firstPunishment = status;
        }

        public boolean getFirstPunishmentStatus(){
            return firstPunishment;
        }

        public void secondPunishmentStatus(boolean status){
            SharedPreferences.Editor editor = punishmentSharedPreferences.edit();
            editor.putBoolean("SecondPunishment", status);
            editor.apply();
            secondPunishment = status;
        }

        public boolean getSecondPunishmentStatus(){
            return secondPunishment;
        }

        public void thirdPunishmentStatus(boolean status){
            SharedPreferences.Editor editor = punishmentSharedPreferences.edit();
            editor.putBoolean("ThirdPunishment", status);
            editor.apply();
            thirdPunishment = status;
        }

        public boolean getThirdPunishmentStatus(){
            return thirdPunishment;
        }

        public void installmentComplete(){
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);

            if (devicePolicyManager.isDeviceOwnerApp(adminComponent.getPackageName())) {
                devicePolicyManager.clearDeviceOwnerApp(adminComponent.getPackageName());
            }
        }
    };
}
