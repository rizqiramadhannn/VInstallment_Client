package com.example.vinstallment.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.vinstallment.R;
import com.example.vinstallment.VInstallmentAIDL;
import com.example.vinstallment.service.PunishmentService;

public class MainActivity extends AppCompatActivity {
    private TextView firstPunishment;
    private TextView secondPunishment;
    private TextView thirdPunishment;
    private VInstallmentAIDL punishmentService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(this, PunishmentService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);
        firstPunishment = findViewById(R.id.firstPunishment);
        secondPunishment = findViewById(R.id.secondPunishment);
        thirdPunishment = findViewById(R.id.thirdPunishment);
        Button paymentButton = findViewById(R.id.payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    punishmentService.showNotif("Terima kasih sudah melakukan pembayaran!", "Silahkan tunggu beberapa saat, kami akan melakukan konfirmasi pembayaranmu.", 3);
                    if (punishmentService.getFirstPunishmentStatus()){
                        punishmentService.disableCamera(false);
                        punishmentService.removeNotif();
                        firstPunishment.setText(R.string.punishment_not_active);
                    }
                    if (punishmentService.getSecondPunishmentStatus()){
                        punishmentService.suspendApps(false);
                        punishmentService.removeNotif();
                        secondPunishment.setText(R.string.punishment_not_active);
                    }
                    if (punishmentService.getThirdPunishmentStatus()){
                        punishmentService.suspendApps(false);
                        punishmentService.stopPlaying();
                        punishmentService.removeNotif();
                        thirdPunishment.setText(R.string.punishment_not_active);
                    }
                    firstPunishment.invalidate();
                    secondPunishment.invalidate();
                    thirdPunishment.invalidate();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            punishmentService.removeNotif();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 5000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(this, PunishmentService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            punishmentService = VInstallmentAIDL.Stub.asInterface(iBinder);
            try {
                if (!punishmentService.getFirstPunishmentStatus()){
                    firstPunishment.setText(R.string.punishment_not_active);
                } else {
                    firstPunishment.setText(R.string.punishment_active);
                }
                if (!punishmentService.getSecondPunishmentStatus()){
                    secondPunishment.setText(R.string.punishment_not_active);
                } else {
                    secondPunishment.setText(R.string.punishment_active);
                }
                if (!punishmentService.getThirdPunishmentStatus()){
                    thirdPunishment.setText(R.string.punishment_not_active);
                } else {
                    thirdPunishment.setText(R.string.punishment_active);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { }
    };
}

