package com.example.vinstallment.provision;

import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_MODE;
import static android.app.admin.DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vinstallment.R;

import java.util.ArrayList;
public class GetProvisioningModeActivity extends Activity {
    private Button dobutton;
    private PersistableBundle persistableBundle;
    private TextView copyrightTV;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_get_provisioning_mode);
        copyrightTV = findViewById(R.id.copyright);
        dobutton = findViewById(R.id.do_button);
        persistableBundle = getIntent().getParcelableExtra(DevicePolicyManager.EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE);
        if (persistableBundle != null && !persistableBundle.isEmpty()){
            String company_name = "", team = "";
            if (persistableBundle.containsKey("company_name")){
                company_name = persistableBundle.get("company_name").toString();
            }
            if (persistableBundle.containsKey("team")){
                team = persistableBundle.get("team").toString();
            }
            copyrightTV.setText("© 2023 " + team + " team of Vostra " + company_name);
        }
        if (persistableBundle == null){
            copyrightTV.setText("Bundle is null");
        }
        dobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putExtra(EXTRA_PROVISIONING_MODE, PROVISIONING_MODE_FULLY_MANAGED_DEVICE);
                finishWithIntent(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void finishWithIntent(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
