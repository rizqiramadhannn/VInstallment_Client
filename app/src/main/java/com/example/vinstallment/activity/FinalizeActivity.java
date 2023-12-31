package com.example.vinstallment.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinstallment.R;
import com.example.vinstallment.receiver.MyDeviceAdminReceiver;
import com.example.vinstallment.service.PunishmentService;

public class FinalizeActivity extends Activity {
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finalize_activity);

        nextButton = findViewById(R.id.do_button);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (devicePolicyManager.isAdminActive(adminComponent)){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        startBlinkingAnimation();
    }

    private void startBlinkingAnimation() {
        final ImageView blinkingImage = findViewById(R.id.blinkingImage);
        final TextView blinkingText = findViewById(R.id.blinkingText);

        ObjectAnimator imageFadeIn = ObjectAnimator.ofFloat(blinkingImage, "alpha", 0f, 1f);
        imageFadeIn.setDuration(2000);
        ObjectAnimator imageFadeOut = ObjectAnimator.ofFloat(blinkingImage, "alpha", 1f, 0f);
        imageFadeOut.setDuration(2000);

        ObjectAnimator textFadeIn = ObjectAnimator.ofFloat(blinkingText, "alpha", 0f, 1f);
        textFadeIn.setDuration(2000);
        ObjectAnimator textFadeOut = ObjectAnimator.ofFloat(blinkingText, "alpha", 1f, 0f);
        textFadeOut.setDuration(2000);

        final AnimatorSet imageBlink = new AnimatorSet();
        imageBlink.play(imageFadeOut).after(imageFadeIn);

        final AnimatorSet textBlink = new AnimatorSet();
        textBlink.play(textFadeOut).after(textFadeIn);

        imageBlink.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                imageBlink.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        textBlink.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                textBlink.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        blinkingImage.post(new Runnable() {
            @Override
            public void run() {
                imageBlink.start();
                textBlink.start();
            }
        });
    }
}
