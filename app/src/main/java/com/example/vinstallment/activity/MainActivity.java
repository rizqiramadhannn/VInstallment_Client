package com.example.vinstallment.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.vinstallment.R;
import com.example.vinstallment.events.PunishmentEvent;
import com.example.vinstallment.service.PunishmentService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {
    private TextView firstPunishment;
    private TextView secondPunishment;
    private TextView thirdPunishment;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("TextView", MODE_PRIVATE);
        firstPunishment = findViewById(R.id.firstPunishment);
        secondPunishment = findViewById(R.id.secondPunishment);
        thirdPunishment = findViewById(R.id.thirdPunishment);

        Intent serviceIntent = new Intent(this, PunishmentService.class);
        startService(serviceIntent);
        Button paymentButton = findViewById(R.id.payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click event here
                Intent intent = new Intent(MainActivity.this, SetPolicyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: " + sharedPreferences.getBoolean("FirstPunishment", false));
        if (!sharedPreferences.getBoolean("FirstPunishment", false)){
            firstPunishment.setText(R.string.punishment_not_active);
            Log.i("TAG", "not active: ");
        } else {
            firstPunishment.setText(R.string.punishment_active);
            Log.i("TAG", "active: ");
        }

        secondPunishment.setText(R.string.punishment_not_active);
        thirdPunishment.setText(R.string.punishment_not_active);
    }

//    @Subscribe
//    public void onPunishmentEvent(PunishmentEvent event){
//        Log.i("EventBus", "onFirstPunishmentEvent: " + event.getId() + " " + event.getStatus());
//        boolean isActive = event.getStatus();
//        int id = event.getId();
//        switch (id){
//            case 1:
//                if (isActive){
//                    firstPunishment.setText(R.string.punishment_active);
//                } else {
//                    firstPunishment.setText(R.string.punishment_not_active);
//                }
//                break;
//            case 2:
//                if (isActive){
//                    secondPunishment.setText(R.string.punishment_active);
//                } else {
//                    secondPunishment.setText(R.string.punishment_not_active);
//                }
//                break;
//            case 3:
//                if (isActive){
//                    thirdPunishment.setText(R.string.punishment_active);
//                } else {
//                    thirdPunishment.setText(R.string.punishment_not_active);
//                }
//                break;
//        }
//    }

}

