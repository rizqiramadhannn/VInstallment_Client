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
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.vinstallment.R;
import com.example.vinstallment.VInstallmentAIDL;
import com.example.vinstallment.events.PunishmentEvent;
import com.example.vinstallment.receiver.MyDeviceAdminReceiver;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PunishmentService extends Service {
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;

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
            // Play the sound
            mediaPlayer.start();

            // Schedule the stop of the MediaPlayer after the sound duration
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
            }, mediaPlayer.getDuration());

            // Schedule the next run after 15 seconds
            handler.postDelayed(this, 3600000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("TextView", MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(this, R.raw.payment_sound);
        mediaPlayer.setLooping(true);
        handler.removeCallbacks(playSoundRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Notification createNotification(String title, String subtitle, int notif_id) {
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
        Notification.Builder builder = new Notification.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setOngoing(true);

        // Create the notification
        return builder.build();
    }

    public List<ResolveInfo> getAllLauncherIntentResolversSorted() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        // Sort the list if needed
        Collections.sort(pkgAppsList, new ResolveInfo.DisplayNameComparator(getPackageManager()));
        return pkgAppsList;
    }


    private String[] getPkgList(){
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        List<String> packageNamesList = new ArrayList<>();

        for (PackageInfo packageInfo : installedPackages) {
            String packageName = packageInfo.packageName;
            Log.d("PackageList", "Package Name: " + packageName);
            // Exclude specific package names
            if (!isEssentialApp(packageName)) {
                packageNamesList.add(packageName);
            }
        }
        packageNamesList.add("com.netflix.partner.activation");
        packageNamesList.add("com.netflix.mediaclient");
        // Convert the List to an array of strings
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

        @Override
        public void stopPlaying() throws RemoteException {
            EventBus.getDefault().post(new PunishmentEvent(false, 3));
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            handler.removeCallbacks(playSoundRunnable);
        }

        @Override
        public void startPlaying() throws RemoteException {
            EventBus.getDefault().post(new PunishmentEvent(true, 3));
            playSoundRunnable.run();
        }

        public void disableCamera(){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FirstPunishment", false);
            editor.apply();
            Log.d("TAG", "disableCamera: " + new PunishmentEvent(false, 1));
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);
            devicePolicyManager.setCameraDisabled(adminComponent, true);
        }

        public void enableCamera(){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FirstPunishment", true);
            editor.apply();
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);
            devicePolicyManager.setCameraDisabled(adminComponent, false);
        }

        public void suspendApps(){
            List<ResolveInfo> packageNamesArr = getAllLauncherIntentResolversSorted();

            for (ResolveInfo x : packageNamesArr){
                Log.i("TAG", "suspendApps: " + x.resolvePackageName);
            }
            EventBus.getDefault().post(new PunishmentEvent(true, 2));
            String[] packageNamesArray = getPkgList();
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);
            devicePolicyManager.setPackagesSuspended(adminComponent, packageNamesArray, true);
        }

        public void unsuspendApps(){
            EventBus.getDefault().post(new PunishmentEvent(false, 2));
            String[] packageNamesArray = getPkgList();
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);
            devicePolicyManager.setPackagesSuspended(adminComponent, packageNamesArray, false);
        }

        public void installmentComplete(){
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(PunishmentService.this, MyDeviceAdminReceiver.class);

            if (devicePolicyManager.isDeviceOwnerApp(adminComponent.getPackageName())) {
                devicePolicyManager.wipeData(0);
                // TODO explore opsi parameter wipe data
            }
        }
    };
}
