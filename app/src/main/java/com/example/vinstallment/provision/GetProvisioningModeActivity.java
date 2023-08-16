package com.example.vinstallment.provision;

import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_MODE;
import static android.app.admin.DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.vinstallment.R;

import java.util.ArrayList;
@SuppressLint("NewApi")
public class GetProvisioningModeActivity extends Activity {

    private static final String TAG = GetProvisioningModeActivity.class.getSimpleName();
    private Button dobutton;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_get_provisioning_mode);
        dobutton = findViewById(R.id.do_button);
        dobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putExtra(EXTRA_PROVISIONING_MODE, PROVISIONING_MODE_FULLY_MANAGED_DEVICE);
                finishWithIntent(intent);
            }
        });
    }
    // TODO tambahin "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE"
    @Override
    public void onBackPressed() {        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void finishWithIntent(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
