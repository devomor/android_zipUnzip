package com.demo.unzip.common;

import static com.demo.unzip.MyApplication.REQUIRED_PERMISSIONS;
import static com.demo.unzip.MyApplication.hasPermissions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.demo.unzip.AdAdmob;
import com.demo.unzip.R;
import com.demo.unzip.main.MainActivityHomeDocuments;

public class PermissionGrant_Activity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final int ALL_PERMISSIONS = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant_permission);


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd_Counter(this);


        BtnClicks();
    }

    public void BtnClicks() {
        findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasPermissions(PermissionGrant_Activity.this, REQUIRED_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(PermissionGrant_Activity.this, REQUIRED_PERMISSIONS, ALL_PERMISSIONS);
                } else {
                    goToNext();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                goToNext();
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, ALL_PERMISSIONS);
            }
        }
    }

    private boolean isFirstTime() {

        return sharedPreferences.getBoolean("isFirstTime", true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void goToNext() {
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (isFirstTime()) {
            Toast.makeText(this, "Welcome to the app!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivityHomeDocuments.class).putExtra("isFromSplash", "true"));
            finish();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        } else {
            startActivity(new Intent(this, MainActivityHomeDocuments.class));
            finish();
        }
    }
}
