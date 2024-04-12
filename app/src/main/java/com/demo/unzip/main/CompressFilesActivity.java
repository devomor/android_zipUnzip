package com.demo.unzip.main;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SnackbarUtils;
import com.demo.unzip.AdAdmob;
import com.demo.unzip.Documents.Compressed_files_adapter;
import com.demo.unzip.Documents.Data_fatcher_holder;
import com.demo.unzip.Documents.File_order_holder;
import com.demo.unzip.Documents.Utils;
import com.hzy.libp7zip.P7ZipApi;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.lingala.zip4j.util.InternalZipConstants;

import com.demo.unzip.R;
public class CompressFilesActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String[] ARCHIVE_ARRAY = {"rar", "zip", "7z", "bz2", "bzip2", "tbz2", "tbz", "gz", "gzip", "tgz", "tar", "xz", "txz"};
    public static TextView noitenTv;
    File Extracted;
    File ExtractedFiles;
    private File FilesExternal;
    private FrameLayout adContainerView;
    private ProgressDialog dialog;
    EditText et_search;
    List<Data_fatcher_holder> files;
    List<Data_fatcher_holder> filessd;
    Data_fatcher_holder info2;
    LinearLayout linAllAudios;
    LinearLayout linAllPhotos;
    LinearLayout linAllVideos;

    LinearLayout linCompress;
    LinearLayout linExtract;
    LinearLayout linHome;
    public Compressed_files_adapter mFileItemAdapter;
    RecyclerView mStorageListView;
    File root;
    SearchView searchView;
    private String str;
    EditText userInput;
    boolean withError;

    @Override
    public void onCreate(Bundle bundle) {
        String str;
        super.onCreate(bundle);
        setContentView(R.layout.activity_compress_files);


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd_Counter(this);


        this.linHome = (LinearLayout) findViewById(R.id.linHome);
        this.linCompress = (LinearLayout) findViewById(R.id.linCompress);
        this.linExtract = (LinearLayout) findViewById(R.id.linExtract);
        this.linAllPhotos = (LinearLayout) findViewById(R.id.linAllPhotos);
        this.linAllVideos = (LinearLayout) findViewById(R.id.linAllVideos);
        this.linAllAudios = (LinearLayout) findViewById(R.id.linAllAudios);
        this.linHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompressFilesActivity.this.getApplicationContext(), MainActivityHomeDocuments.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                CompressFilesActivity.this.startActivity(intent);
            }
        });
        this.linExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompressFilesActivity.this.getApplicationContext(), Extract_Files_activity.class);
                intent.putExtra("choice", 4);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                CompressFilesActivity.this.startActivity(intent);

            }
        });
        this.linAllPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompressFilesActivity.this.getApplicationContext(), AllImagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                CompressFilesActivity.this.startActivity(intent);
            }
        });
        this.linAllVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompressFilesActivity.this.getApplicationContext(), AllVideosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                CompressFilesActivity.this.startActivity(intent);
            }
        });
        this.linAllAudios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompressFilesActivity.this.getApplicationContext(), AllAudiosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                CompressFilesActivity.this.startActivity(intent);
            }
        });
        noitenTv = (TextView) findViewById(R.id.emptyMsg);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        if (intent.getAction() == null) {
            try {
                String file = ContextCompat.getExternalFilesDirs(this, null)[1].toString();
                int indexOf = file.indexOf(InternalZipConstants.ZIP_FILE_SEPARATOR);
                this.str = file.substring(indexOf, file.indexOf(InternalZipConstants.ZIP_FILE_SEPARATOR, file.indexOf(InternalZipConstants.ZIP_FILE_SEPARATOR, indexOf + 1) + 1) + 1);
            } catch (Exception unused) {
                Log.e("tag", "No Card Found!");
                this.str = null;
            }
            this.FilesExternal = null;
            if (Environment.getExternalStorageState().equals("mounted") && (str = this.str) != null) {
                this.FilesExternal = new File(str);
            }
            this.root = Environment.getExternalStorageDirectory();
            File file2 = new File(this.root.getAbsolutePath() + "/Extractor/");
            this.Extracted = new File(this.root.getAbsolutePath() + "/Extractor/Extract");
            try {
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (!this.Extracted.exists()) {
                    this.Extracted.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog();
            this.mStorageListView = (RecyclerView) findViewById(R.id.fragment_storage_list);
            new FilesTask().execute(new String[0]);
            SearchView searchView = (SearchView) findViewById(R.id.searchCompress);
            this.searchView = searchView;
            searchView.setIconified(false);
            this.searchView.clearFocus();
            this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String str2) {
                    if (CompressFilesActivity.this.mFileItemAdapter == null) {
                        return false;
                    }
                    CompressFilesActivity.this.mFileItemAdapter.getFilter().filter(str2);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String str2) {
                    if (CompressFilesActivity.this.mFileItemAdapter == null) {
                        return false;
                    }
                    CompressFilesActivity.this.mFileItemAdapter.getFilter().filter(str2);
                    return false;
                }
            });
        } else if (intent.getAction().equals("android.intent.action.VIEW")) {
            new Bundle();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                File file3 = new File(getRealPathFromURI((Uri) extras.get("fileUri")));
                String name = file3.getName();
                this.mStorageListView = (RecyclerView) findViewById(R.id.fragment_storage_list);
                File file4 = new File(getCacheDir(), name);
                runCommand(File_order_holder.getExtractCmd(file3.toString(), file4.toString()), true);
                this.ExtractedFiles = file4;
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor query = getContentResolver().query(uri, null, null, null, null);
        if (query == null) {
            return uri.getPath();
        }
        query.moveToFirst();
        String string = query.getString((int) query.getColumnIndex("_data"));
        query.close();
        return string;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public List<Data_fatcher_holder> getListFiles(File file) {
        ArrayList arrayList = new ArrayList();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    arrayList.addAll(getListFiles(file2));
                } else {
                    String name = file2.getName();
                    String[] strArr = ARCHIVE_ARRAY;
                    if (name.endsWith(strArr[0])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[1])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[2])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[3])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[4])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[5])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[6])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[7])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[8])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[9])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[10])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[11])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    } else if (file2.getName().endsWith(strArr[12])) {
                        arrayList.add(Utils.getFileInfoFromPath(file2.getPath()));
                    }
                }
            }
        } else {
            this.withError = true;
        }
        return arrayList;
    }

    @Override
    public void onClick(View view) {
        final Data_fatcher_holder data_fatcher_holder = (Data_fatcher_holder) view.getTag();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_extract);
        dialog.findViewById(R.id.linCompressFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                CompressFilesActivity.this.onExtractFile(data_fatcher_holder);
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.linOpenFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                CompressFilesActivity.this.onExtractFile(data_fatcher_holder, true);
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.linShareFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.STREAM", Uri.parse(data_fatcher_holder.getFilePath()));
                try {
                    CompressFilesActivity.this.startActivity(Intent.createChooser(intent, "Share ZIP File "));
                } catch (Exception unused) {
                }
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.linDeleteFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                CompressFilesActivity.this.onRemoveFile(data_fatcher_holder);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void onExtractFile(Data_fatcher_holder data_fatcher_holder) {
        getWindow().addFlags(128);
        this.info2 = data_fatcher_holder;
        if (new File(this.Extracted + InternalZipConstants.ZIP_FILE_SEPARATOR + data_fatcher_holder.getFileName() + "-ext").exists()) {
            first_dualog();
            return;
        }
        String filePath = data_fatcher_holder.getFilePath();
        runCommand(File_order_holder.getExtractCmd(filePath, this.Extracted + InternalZipConstants.ZIP_FILE_SEPARATOR + data_fatcher_holder.getFileName() + "-ext"));
    }

    public void onExtractFile(Data_fatcher_holder data_fatcher_holder, boolean z) {
        getWindow().addFlags(128);
        this.info2 = data_fatcher_holder;
        try {
            File cacheDir = getCacheDir();
            File file = new File(cacheDir, data_fatcher_holder.getFileName() + "-ext");
            String filePath = data_fatcher_holder.getFilePath();
            StringBuilder sb = new StringBuilder();
            sb.append(file.getAbsolutePath());
            runCommand(File_order_holder.getExtractCmd(filePath, sb.toString()), true);
            this.ExtractedFiles = file;
            Log.e("*", filePath + ": " + ((Object) sb));
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.ExtractedFiles);
            sb2.append("");
            Log.e("*", sb2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void first_dualog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This file Already Extracted");
        builder.setCancelable(true);
        builder.setPositiveButton("Override", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String filePath = CompressFilesActivity.this.info2.getFilePath();
                CompressFilesActivity compressFilesActivity = CompressFilesActivity.this;
                compressFilesActivity.runCommand(File_order_holder.getExtractCmd(filePath, CompressFilesActivity.this.Extracted + InternalZipConstants.ZIP_FILE_SEPARATOR + CompressFilesActivity.this.info2.getFileName() + "-ext"));
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CompressFilesActivity compressFilesActivity = CompressFilesActivity.this;
                compressFilesActivity.showdialogforspeed(compressFilesActivity);
            }
        });
        builder.create().show();
    }

    public void showdialogforspeed(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_already);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        EditText editText = (EditText) dialog.findViewById(R.id.edtFileName);
        this.userInput = editText;
        editText.setText(this.info2.getFileName());
        dialog.findViewById(R.id.linCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.linOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String obj = CompressFilesActivity.this.userInput.getText().toString();
                CompressFilesActivity compressFilesActivity = CompressFilesActivity.this;
                if (compressFilesActivity.Check_If_File_Exist(CompressFilesActivity.this.Extracted + InternalZipConstants.ZIP_FILE_SEPARATOR + obj + "-ext")) {
                    Toast.makeText(CompressFilesActivity.this, "This File Name Already Exist", Toast.LENGTH_SHORT).show();
                    return;
                }
                String filePath = CompressFilesActivity.this.info2.getFilePath();
                CompressFilesActivity compressFilesActivity2 = CompressFilesActivity.this;
                compressFilesActivity2.runCommand(File_order_holder.getExtractCmd(filePath, CompressFilesActivity.this.Extracted + InternalZipConstants.ZIP_FILE_SEPARATOR + obj + "-ext"));
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public boolean Check_If_File_Exist(String str) {
        if (new File(str).exists()) {
            Toast.makeText(getApplicationContext(), "This File Name Already Exist", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void onRefresh() {
        this.files.clear();
        this.files = getListFiles(this.root);
        new FilesTask().execute(new String[0]);
        this.mFileItemAdapter.notifyDataSetChanged();
        this.mStorageListView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView recyclerView = this.mStorageListView;
        Compressed_files_adapter compressed_files_adapter = new Compressed_files_adapter(this, this, this);
        this.mFileItemAdapter = compressed_files_adapter;
        recyclerView.setAdapter(compressed_files_adapter);
        recyclerView.setItemAnimator(null);
        this.mFileItemAdapter.setDataList(this.files);
    }

    public void onRemoveFile(final Data_fatcher_holder data_fatcher_holder) {
        showProgressDialog();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                String message;
                try {
                    CompressFilesActivity.this.removeFile(new File(data_fatcher_holder.getFilePath()));
                    message = "Deleted: " + data_fatcher_holder.getFileName();
                } catch (Exception e) {
                    message = e.getMessage();
                }
                observableEmitter.onNext(message);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String str) throws Exception {
                CompressFilesActivity.this.dismissProgressDialog();
                CompressFilesActivity.this.onRefresh();
                SnackbarUtils.with(CompressFilesActivity.this.mStorageListView).setMessage(str).show();
            }
        });
    }

    public void removeFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            Objects.requireNonNull(listFiles);
            for (File file2 : listFiles) {
                removeFile(file2);
            }
        }
        file.delete();
    }

    public void runCommand(final String str) {
        showProgressDialog();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(Integer.valueOf(P7ZipApi.executeCommand(str)));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer num) throws Exception {
                CompressFilesActivity.this.dismissProgressDialog();
                CompressFilesActivity.this.showResult(num.intValue());
            }
        });
    }

    public void runCommand(final String str, boolean z) {
        showProgressDialog();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(P7ZipApi.executeCommand(str));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer num) throws Exception {
                CompressFilesActivity.this.dismissProgressDialog();
                CompressFilesActivity.this.showResult(num, true);
            }
        });
    }


    public void showProgressDialog() {
        ProgressDialog progressDialog = this.dialog;
        if (progressDialog == null) {
            ProgressDialog progressDialog2 = new ProgressDialog(this);
            this.dialog = progressDialog2;
            progressDialog2.setMessage(getText(R.string.process_progress_messag));
            this.dialog.setCancelable(false);
            this.dialog.show();
        } else if (progressDialog.isShowing()) {
        } else {
            ProgressDialog progressDialog3 = new ProgressDialog(this);
            this.dialog = progressDialog3;
            progressDialog3.setMessage(getText(R.string.process_progress_messag));
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
    }

    public void dismissProgressDialog() {
        ProgressDialog progressDialog = this.dialog;
        if (progressDialog == null || !progressDialog.isShowing() || isFinishing()) {
            return;
        }
        this.dialog.dismiss();
    }

    public void showResult(int i) {
        int i2;
        getWindow().clearFlags(128);
        final Intent intent = new Intent(this, Extract_Files_activity.class);
        if (i == 255) {
            i2 = R.string.mesag_ret_user_stop_this;
        } else {
            if (i == 0) {
                final AlertDialog create = new AlertDialog.Builder(this).create();
                View inflate = getLayoutInflater().inflate(R.layout.dialog_success_comressed, (ViewGroup) null);
                create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                ((TextView) inflate.findViewById(R.id.txtMessage)).setText("Successfully Extracted");
                inflate.findViewById(R.id.linOpenFolder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("choice", 4);
                        CompressFilesActivity.this.startActivity(intent);
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
            } else if (i == 1) {
                i2 = R.string.msg_ret_warning;
            } else if (i == 2) {
                i2 = R.string.message_ret_faults;
            } else if (i == 7) {
                i2 = R.string.message_ret_commnds;
            } else if (i == 8) {
                i2 = R.string.msg_ret_memmory;
            }
            i2 = R.string.msg_ret_success;
        }
        SnackbarUtils.with(this.mStorageListView).setMessage(getString(i2)).show();
    }

    public void showResult(int i, boolean z) {
        int i2;
        getWindow().clearFlags(128);
        Intent intent = new Intent(this, Extract_Files_activity.class);
        if (i == 255) {
            i2 = R.string.mesag_ret_user_stop_this;
        } else {
            if (i == 0) {
                intent.putExtra("openFile", true);
                intent.putExtra("filepath", this.ExtractedFiles.getAbsolutePath());
                startActivity(intent);
                finish();
            } else if (i == 1) {
                i2 = R.string.msg_ret_warning;
            } else if (i == 2) {
                i2 = R.string.message_ret_faults;
            } else if (i == 7) {
                i2 = R.string.message_ret_commnds;
            } else if (i == 8) {
                i2 = R.string.msg_ret_memmory;
            }
            i2 = R.string.msg_ret_success;
        }
        SnackbarUtils.with(this.mStorageListView).setMessage(getString(i2)).show();
    }

    public static void setTvNoSearchFoundVisibility(int i) {
        noitenTv.setVisibility(i);
    }

    public void OpenFile() {
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.ExtractedFiles.getName().substring(this.ExtractedFiles.getName().lastIndexOf(".") + 1));
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            if (Build.VERSION.SDK_INT >= 24) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(FileProvider.getUriForFile(this, getApplication().getPackageName()+".provider", this.ExtractedFiles), mimeTypeFromExtension);
            } else {
                intent.setDataAndType(Uri.fromFile(this.ExtractedFiles), mimeTypeFromExtension);
            }
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this, "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
        }
    }


    public class FilesTask extends AsyncTask<String, Void, String> {
        private FilesTask() {
        }

        @Override
        public void onPreExecute() {
            CompressFilesActivity.this.showProgressDialog();
        }

        @Override
        public String doInBackground(String... strArr) {
            CompressFilesActivity compressFilesActivity = CompressFilesActivity.this;
            compressFilesActivity.files = compressFilesActivity.getListFiles(compressFilesActivity.root);
            if (CompressFilesActivity.this.FilesExternal == null) {
                return null;
            }
            CompressFilesActivity compressFilesActivity2 = CompressFilesActivity.this;
            compressFilesActivity2.filessd = compressFilesActivity2.getListFiles(compressFilesActivity2.FilesExternal);
            CompressFilesActivity.this.files.addAll(CompressFilesActivity.this.filessd);
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            CompressFilesActivity.this.mStorageListView.setLayoutManager(new GridLayoutManager(CompressFilesActivity.this, 1));
            RecyclerView recyclerView = CompressFilesActivity.this.mStorageListView;
            CompressFilesActivity compressFilesActivity = CompressFilesActivity.this;
            Compressed_files_adapter compressed_files_adapter = new Compressed_files_adapter(compressFilesActivity, compressFilesActivity, compressFilesActivity);
            compressFilesActivity.mFileItemAdapter = compressed_files_adapter;
            recyclerView.setAdapter(compressed_files_adapter);
            CompressFilesActivity.this.mFileItemAdapter.setDataList(CompressFilesActivity.this.files);
            if (CompressFilesActivity.this.mFileItemAdapter.getItemCount() < 1) {
                CompressFilesActivity.this.findViewById(R.id.emptyMsg).setVisibility(View.VISIBLE);
            }
            if (CompressFilesActivity.this.isFinishing()) {
                return;
            }
            CompressFilesActivity.this.dismissProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
