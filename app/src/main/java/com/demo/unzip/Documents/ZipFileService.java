package com.demo.unzip.Documents;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.util.InternalZipConstants;

import com.demo.unzip.R;


public class ZipFileService extends IntentService {
    public static final File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "ZipUnZip");
    String ZipName;
    ProgressDialog dialog;
    Intent intent;
    String name;
    ArrayList<String> selectFileItem;
    ShowDialog showDialog;


    public interface ShowDialog {
        void message(String str);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ZipFileService(String str) {
        super(str);
    }

    public ZipFileService() {
        super("hjhj");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("003", "Channel3", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLightColor(-16776961);
            notificationChannel.setLockscreenVisibility(0);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
                startForeground(103, new Notification.Builder(getApplicationContext(), "003").setOngoing(true).setSmallIcon(R.drawable.zip_folder_icon).setContentTitle("").build());
                return;
            }
            return;
        }
        startForeground(105, new Notification());
    }

    @Override
    public void onHandleIntent(Intent intent) {
        this.selectFileItem = intent.getStringArrayListExtra("selectedItem");
        String stringExtra = intent.getStringExtra("ZipFileName");
        this.name = stringExtra;
        try {
            zipper(this.selectFileItem, stringExtra);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String zipper(ArrayList<String> arrayList, String str) throws IOException {
        File file = null;
        showErrorDialoge();
        this.intent = new Intent("MY_ACTION");
        mediaStorageDir.mkdirs();
        String str2 = file.getAbsolutePath() + InternalZipConstants.ZIP_FILE_SEPARATOR + str + ".zip";
        try {
            if (new File(str2).exists()) {
                new File(str2).delete();
            }
            ZipFile zipFile = new ZipFile(str2);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            if (arrayList.size() > 0) {
                Iterator<String> it = arrayList.iterator();
                while (it.hasNext()) {
                    File file2 = new File(it.next());
                    this.ZipName = file2.getName() + "\n";
                    this.intent.putExtra("ACTION_STOP", "START");
                    this.intent.putExtra("MESSAGE", this.ZipName);
                    sendBroadcast(this.intent);
                    zipFile.addFile(file2, zipParameters);
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return str2;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.intent.putExtra("ACTION_STOP", "STOP");
        sendBroadcast(this.intent);
    }

    public void showErrorDialoge() {
        this.dialog = new ProgressDialog(this);
        if (Build.VERSION.SDK_INT >= 26) {
            this.dialog.getWindow().setType(2038);
        } else {
            this.dialog.getWindow().setType(2006);
        }
        this.dialog.show();
    }
}
