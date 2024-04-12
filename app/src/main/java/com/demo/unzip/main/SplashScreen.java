package com.demo.unzip.main;

import static com.demo.unzip.MyApplication.REQUIRED_PERMISSIONS;
import static com.demo.unzip.MyApplication.isOnline;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.demo.unzip.R;
import com.demo.unzip.common.PermissionGrant_Activity;
import com.demo.unzip.main.MainActivityHomeDocuments;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splashactivity);
        if (isOnline(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OpenNext1();
                }
            }, 5000);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Network error…").setMessage("Internet is not available, reconnect network and try again.").setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    finishAffinity();

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void OpenNext1() {
        if (hasPermissions(this, REQUIRED_PERMISSIONS)) {
            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            if (isFirstTime()) {

                Toast.makeText(this, "Welcome to the app!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivityHomeDocuments.class).putExtra("isFromSplash", "true"));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstTime", false);
                editor.apply();
            } else {
                startActivity(new Intent(this, MainActivityHomeDocuments.class));
                finish();

            }
        } else {
            startActivity(new Intent(this, PermissionGrant_Activity.class));
            finish();
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean isFirstTime() {
        return sharedPreferences.getBoolean("isFirstTime", true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExitApp();
    }

    public void ExitApp() {
        moveTaskToBack(true);
        finish();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }


}
