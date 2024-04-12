package com.demo.unzip.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.unzip.AdAdmob;
import com.demo.unzip.Documents.Data_fatcher_holder;
import com.demo.unzip.Documents.File_order_holder;
import com.demo.unzip.Documents.Utils;
import com.demo.unzip.Documents.extracted_Files_adapter;
import com.demo.unzip.R;
import com.demo.unzip.common.PrefManager;
import com.hzy.libp7zip.P7ZipApi;

import net.lingala.zip4j.util.InternalZipConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class Extract_Files_activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    File Compress;
    File Extracted;
    String FilePath;
    private ProgressDialog dialog;
    int getIntval;
    RelativeLayout layout;
    LinearLayout linAllAudios;
    LinearLayout linAllPhotos;
    LinearLayout linAllVideos;
    LinearLayout linCompress;
    LinearLayout linExtract;
    LinearLayout linHome;
    public List<Data_fatcher_holder> mCurFileInfoList;
    public String mCurPath;
    public extracted_Files_adapter mFileItemAdapter;
    RecyclerView mStorageListView;
    PrefManager prefManager;
    int check = 0;
    boolean isOpen = false;
    int store_path_backpress = 1;

    public static class Open_File_By_File_Format {
        public static void openFile(Context context, File file) throws IOException {
            Uri fromFile = Uri.fromFile(file);
            Intent intent = new Intent("android.intent.action.VIEW");
            if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                intent.setDataAndType(fromFile, "application/msword");
            } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png") || file.toString().contains(".JPG") || file.toString().contains(".JPEG") || file.toString().contains(".PNG")) {
                intent.setDataAndType(fromFile, "image/*");
            } else if (file.toString().contains(".pdf")) {
                intent.setDataAndType(fromFile, "application/pdf");
            } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                intent.setDataAndType(fromFile, "application/vnd.ms-powerpoint");
            } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                intent.setDataAndType(fromFile, "application/vnd.ms-excel");
            } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
                intent.setDataAndType(fromFile, "application/x-wav");
            } else if (file.toString().contains(".rtf")) {
                intent.setDataAndType(fromFile, "application/rtf");
            } else if (file.toString().contains(".wav") || file.toString().contains(".mp3")) {
                intent.setDataAndType(fromFile, "audio/x-wav");
            } else if (file.toString().contains(".gif")) {
                intent.setDataAndType(fromFile, "image/gif");
            } else if (file.toString().contains(".txt")) {
                intent.setDataAndType(fromFile, "text/plain");
            } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                intent.setDataAndType(fromFile, "video/*");
            } else {
                intent.setDataAndType(fromFile, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName()+".provider", file), intent.getType());
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "No default Application found!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_extracted_files);


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd_Counter(this);


        this.prefManager = new PrefManager(this);
        this.linHome = (LinearLayout) findViewById(R.id.linHome);
        this.linCompress = (LinearLayout) findViewById(R.id.linCompress);
        this.linExtract = (LinearLayout) findViewById(R.id.linExtract);
        this.linAllPhotos = (LinearLayout) findViewById(R.id.linAllPhotos);
        this.linAllVideos = (LinearLayout) findViewById(R.id.linAllVideos);
        this.linAllAudios = (LinearLayout) findViewById(R.id.linAllAudios);
        this.linCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Extract_Files_activity.this.getApplicationContext(), CompressFilesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Extract_Files_activity.this.startActivity(intent);
            }
        });
        this.linHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Extract_Files_activity.this.getApplicationContext(), MainActivityHomeDocuments.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Extract_Files_activity.this.startActivity(intent);
            }
        });
        this.linAllPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Extract_Files_activity.this.getApplicationContext(), AllImagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Extract_Files_activity.this.startActivity(intent);
            }
        });
        this.linAllVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Extract_Files_activity.this.getApplicationContext(), AllVideosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Extract_Files_activity.this.startActivity(intent);
            }
        });
        this.linAllAudios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Extract_Files_activity.this.getApplicationContext(), AllAudiosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Extract_Files_activity.this.startActivity(intent);
            }
        });
        this.mStorageListView = (RecyclerView) findViewById(R.id.fragment_storage_list);
        this.mStorageListView.setItemAnimator(null);
        this.isOpen  = getIntent().getBooleanExtra("openFile", false);
        if ( this.isOpen ) {
            this.FilePath = getIntent().getStringExtra("filepath");
            loadPathInfo( this.FilePath);
            this.mStorageListView.setLayoutManager(new GridLayoutManager(this, 1));
            this.mFileItemAdapter= new extracted_Files_adapter(this, this);
            this.mCurPath = this.FilePath;
            this.mStorageListView.setAdapter(this.mFileItemAdapter);
            return;
        }
        this.getIntval = getIntent().getIntExtra("choice", 0);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory.getAbsolutePath() + "/Extractor/");
        this.Compress = new File(externalStorageDirectory.getAbsolutePath() + "/Extractor/Compressed");
        this.Extracted = new File(externalStorageDirectory.getAbsolutePath() + "/Extractor/Extract");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!this.Compress.exists()) {
                this.Compress.mkdirs();
            }
            if (!this.Extracted.exists()) {
                this.Extracted.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String externalStorageState = Environment.getExternalStorageState();
        if ("mounted".equals(externalStorageState) || "mounted_ro".equals(externalStorageState)) {
            File externalStorageDirectory2 = Environment.getExternalStorageDirectory();
            String parent = externalStorageDirectory2.getParent();
            if (parent == null) {
                Log.e("hi", "External Storage: " + externalStorageDirectory2 + "\n");
            } else {
                File file2 = new File(parent);
                File[] listFiles = file2.listFiles();
                this.mCurPath = file2.getAbsolutePath();
                String[] splitList = this.mCurPath.split(InternalZipConstants.ZIP_FILE_SEPARATOR);
                Log.e("hi", "External splitList length: " + splitList.length);
                Log.e("hi", "External mCurPath:1 " + mCurPath);
                if (listFiles == null) {
                    if (this.getIntval == 4) {
                        this.mCurPath = this.Extracted.getAbsolutePath();
                    } else if (this.getIntval == 3) {
                        this.mCurPath = this.Compress.getAbsolutePath();
                    } else {
                        this.getIntval = 3;
                        this.mCurPath = externalStorageDirectory2.getAbsolutePath();
                    }
                } else {
                    if (this.getIntval == 1 && listFiles.length == 4) {
                        this.mCurPath = listFiles[1].getAbsolutePath();
                    } else if (this.getIntval == 2 && listFiles.length == 4) {
                        this.mCurPath = listFiles[2].getAbsolutePath();
                    } else if (this.getIntval == 3) {
                        this.mCurPath = this.Compress.getAbsolutePath();
                    } else if (this.getIntval == 4) {
                        this.mCurPath = this.Extracted.getAbsolutePath();
                    } else if (splitList.length > 2) {
                        this.mCurPath = this.mCurPath.substring(0, this.mCurPath.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR));
                    }
                }
                Log.e("hi", "External mCurPath:2 " + mCurPath);
            }
        }



        loadPathInfo(this.mCurPath);
        this.mStorageListView.setLayoutManager(new GridLayoutManager(this, 1));
        this.mFileItemAdapter = new extracted_Files_adapter(this, this);
        this.mStorageListView.setAdapter(this.mFileItemAdapter );
    }

    @Override
    public void onDestroy() {
        String str = this.FilePath;
        if (str != null) {
            try {
                removeFile(new File(str));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 1 && iArr[0] == 0) {
            loadPathInfo(this.mCurPath);
        }
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    public void loadPathInfo(final String str) {
        List<Data_fatcher_holder> list = this.mCurFileInfoList;
        if (list != null) {
            list.clear();
        }
        Observable.create(new ObservableOnSubscribe<List<Data_fatcher_holder>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Data_fatcher_holder>> observableEmitter) throws Exception {
                if (Extract_Files_activity.this.isOpen) {
                    Extract_Files_activity.this.mCurFileInfoList = Utils.getInfoListFromPath(str, true);
                    observableEmitter.onNext(Extract_Files_activity.this.mCurFileInfoList);
                    observableEmitter.onComplete();
                    return;
                }
                Extract_Files_activity.this.mCurFileInfoList = Utils.getInfoListFromPath(str);
                observableEmitter.onNext(Extract_Files_activity.this.mCurFileInfoList);
                observableEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Data_fatcher_holder>>() {
            @Override
            public void accept(List<Data_fatcher_holder> list2) throws Exception {
                Extract_Files_activity.this.mStorageListView.getRecycledViewPool().clear();
                Extract_Files_activity.this.mFileItemAdapter.setDataList(Extract_Files_activity.this.mCurFileInfoList);
                Extract_Files_activity.this.mFileItemAdapter.notifyDataSetChanged();
                Extract_Files_activity extract_Files_activity = Extract_Files_activity.this;
                extract_Files_activity.mCurPath = str;
                extract_Files_activity.mStorageListView.smoothScrollToPosition(0);
                list2.size();
                if (list2.size() >= 1) {
                    Extract_Files_activity.this.findViewById(R.id.emptyMsg).setVisibility(View.GONE);
                } else {
                    Extract_Files_activity.this.findViewById(R.id.emptyMsg).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadPathInfo(this.mCurPath);
    }
    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof String) {
            loadPathInfo((String) tag);
        } else if (tag instanceof Data_fatcher_holder) {
            final Data_fatcher_holder data_fatcher_holder = (Data_fatcher_holder) tag;
            if (data_fatcher_holder.isFolder()) {
                if (this.isOpen) {
                    this.store_path_backpress = 2;
                    loadPathInfo(data_fatcher_holder.getFilePath());
                    return;
                }
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog_extracted);
                dialog.findViewById(R.id.linOpenFolder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Extract_Files_activity extract_Files_activity = Extract_Files_activity.this;
                        extract_Files_activity.store_path_backpress = 2;
                        extract_Files_activity.loadPathInfo(data_fatcher_holder.getFilePath());
                        dialog.cancel();
                    }
                });
                dialog.findViewById(R.id.linCompressFolder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Extract_Files_activity.this.onCompressFile(data_fatcher_holder);
                        dialog.cancel();
                    }
                });
                dialog.findViewById(R.id.linDeleteFolder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Extract_Files_activity.this.onRemoveFile(data_fatcher_holder);
                        dialog.cancel();
                    }
                });
                dialog.show();
            } else if (this.isOpen) {

                Log.e("MYTAG", "ErrorNo: onClick:1" +data_fatcher_holder.getFilePath());
                try {
                    Open_File_By_File_Format.openFile(this, new File(data_fatcher_holder.getFilePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                final Dialog dialog2 = new Dialog(this);
                dialog2.requestWindowFeature(1);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog2.setContentView(R.layout.dialog_compress);
                dialog2.findViewById(R.id.linOpenFile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        try {
                            Log.e("MYTAG", "ErrorNo:2 onClick:" +data_fatcher_holder.getFilePath());
                            Open_File_By_File_Format.openFile(Extract_Files_activity.this, new File(data_fatcher_holder.getFilePath()));
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        dialog2.cancel();
                    }
                });
                dialog2.findViewById(R.id.linCompressFiles).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Extract_Files_activity.this.onCompressFile(data_fatcher_holder);
                        dialog2.cancel();
                    }
                });
                dialog2.findViewById(R.id.linShareFiles).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("image/*");
                        intent.putExtra("android.intent.extra.STREAM", Uri.parse(data_fatcher_holder.getFilePath()));
                        try {
                            Extract_Files_activity.this.startActivity(Intent.createChooser(intent, "Share File"));
                        } catch (Exception unused) {
                        }
                    }
                });
                dialog2.findViewById(R.id.linDeleteFiles).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Extract_Files_activity.this.onRemoveFile(data_fatcher_holder);
                        dialog2.cancel();
                    }
                });
                dialog2.show();
            }
        }
    }

    public void onCompressFile(Data_fatcher_holder data_fatcher_holder) {
        getWindow().addFlags(128);
        this.check = 1;
        String filePath = data_fatcher_holder.getFilePath();
        runCommand(File_order_holder.getCompressCmd(filePath, this.Compress + InternalZipConstants.ZIP_FILE_SEPARATOR + data_fatcher_holder.getFileName() + ".7z", "7z"));
    }

    private void onExtractFile(Data_fatcher_holder data_fatcher_holder) {
        getWindow().addFlags(128);
        this.check = 2;
        String filePath = data_fatcher_holder.getFilePath();
        runCommand(File_order_holder.getExtractCmd(filePath, this.Extracted + InternalZipConstants.ZIP_FILE_SEPARATOR + data_fatcher_holder.getFileName() + "-ext"));
    }

    public void onRemoveFile(final Data_fatcher_holder data_fatcher_holder) {
        showProgressDialog();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                String message;
                try {
                    Extract_Files_activity.this.removeFile(new File(data_fatcher_holder.getFilePath()));
                    message = "Deleted: " + data_fatcher_holder.getFileName();
                } catch (Exception e) {
                    message = e.getMessage();
                }
                observableEmitter.onNext(message);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String str) throws Exception {
                Extract_Files_activity.this.dismissProgressDialog();
                Extract_Files_activity.this.onRefresh();
            }
        });
    }

    private void runCommand(final String str) {
        showProgressDialog();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(Integer.valueOf(P7ZipApi.executeCommand(str)));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer num) throws Exception {
                Extract_Files_activity.this.dismissProgressDialog();
                Extract_Files_activity.this.showResult(num.intValue());
                Extract_Files_activity.this.onRefresh();
            }
        });
    }

    private void showProgressDialog() {
        if (this.dialog == null) {
            this.dialog  = new ProgressDialog(this);
            this.dialog .setMessage(getText(R.string.process_progress_messag));
            this.dialog.setCancelable(false);
        }
        this.dialog.show();
    }
    public void dismissProgressDialog() {
        ProgressDialog progressDialog = this.dialog;
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        this.dialog.dismiss();
    }

    public void removeFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                removeFile(file2);
            }
        }
        file.delete();
    }

    public void showResult(int i) {
        getWindow().clearFlags(128);
        final Intent intent = new Intent(this, CompressFilesActivity.class);
        if (i != 255 && i == 0) {
            int i2 = this.check;
            if (i2 == 1) {
                final AlertDialog create = new AlertDialog.Builder(this).create();
                View inflate = getLayoutInflater().inflate(R.layout.dialog_success_comressed, (ViewGroup) null);
                create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                ((TextView) inflate.findViewById(R.id.txtMessage)).setText("Successfully Compressed");
                inflate.findViewById(R.id.linOpenFolder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("choice", 3);
                        Extract_Files_activity.this.startActivity(intent);
                        create.cancel();
                    }
                });
                inflate.findViewById(R.id.linOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        create.cancel();
                    }
                });
                create.setView(inflate);
                create.show();
            } else if (i2 == 2) {
                final AlertDialog create2 = new AlertDialog.Builder(this).create();
                View inflate2 = getLayoutInflater().inflate(R.layout.dialog_success_comressed, (ViewGroup) null);
                create2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                ((TextView) inflate2.findViewById(R.id.txtMessage)).setText("File is Successfully Extracted.");
                inflate2.findViewById(R.id.linOpenFolder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("choice", 4);
                        Extract_Files_activity.this.startActivity(intent);
                        create2.cancel();
                    }
                });
                inflate2.findViewById(R.id.linOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        create2.cancel();
                    }
                });
                create2.setView(inflate2);
                create2.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        this.mCurPath.split(InternalZipConstants.ZIP_FILE_SEPARATOR);
        if (this.isOpen || this.getIntval > 0) {
            if (this.store_path_backpress > 1) {
                String str = this.mCurPath;
                loadPathInfo(str.substring(0, str.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR)));
                this.store_path_backpress--;
                return;
            }
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            case R.id.rate:
                if (isOnline()) {
                    Intent intent3 = new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent3);
                } else {
                    Toast makeText = Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT);
                    makeText.setGravity(17, 0, 0);
                    makeText.show();
                }
                return true;
            case R.id.share:
                if (isOnline()) {
                    Intent intent4 = new Intent("android.intent.action.SEND");
                    intent4.setType("text/plain");
                    intent4.putExtra("android.intent.extra.TEXT", "Hi! I'm using a great " + getResources().getString(R.string.app_name) + " Files application. Check it out:http://play.google.com/store/apps/details?id=" + getPackageName());
                    intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(Intent.createChooser(intent4, "Share with Friends"));
                } else {
                    Toast makeText2 = Toast.makeText(getApplicationContext(), "No Internet Connection..", Toast.LENGTH_SHORT);
                    makeText2.setGravity(17, 0, 0);
                    makeText2.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
