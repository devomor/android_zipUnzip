package com.demo.unzip.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.demo.unzip.AdAdmob;
import com.demo.unzip.Preferences;
import com.demo.unzip.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.hzy.libp7zip.P7ZipApi;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.util.InternalZipConstants;

import com.demo.unzip.Documents.Constants;
import com.demo.unzip.Documents.Data_Model;
import com.demo.unzip.Documents.File_order_holder;
import com.demo.unzip.Documents.Utils;
import com.demo.unzip.Documents.ZipFileService;


public class AllImagesActivity extends AppCompatActivity implements View.OnClickListener {
    public static FloatingActionMenu fabmenu;
    public static TextView noitenTv;
    File Compress;
    File Extracted;
    private File FilesExternal;
    private ProgressDialog dialog1;
    FloatingActionButton fabCompress;
    FloatingActionButton fabDelete;
    ArrayList<Data_Model> filXESsd;
    String fileNameToSave;
    ArrayList<Data_Model> files;
    All_files_adapter mFileItemAdapter;
    RecyclerView mStorageListView;
    Preferences preferences;
    File root;
    SearchView searchView;
    CheckBox selectAllCheckBox;
    private String str;
    File temp;
    Toolbar toolbar;
    public static final String[] All_Doc_ARRAY = {"jpg", "jpeg", "png", "gif", Constants.psd};
    public static int checkmenu = 0;
    public static final File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Extractor/Compressed");
    public static ArrayList<Data_Model> selectedItems = new ArrayList<>();
    int check = 0;
    String fileName = "";


    @Override
    public void onCreate(Bundle bundle) {
        String str;
        super.onCreate(bundle);
        setContentView(R.layout.activity_all_images);



        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd_Counter(this);


        this.preferences = new Preferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_back_icon_black);
        this.selectAllCheckBox = (CheckBox) findViewById(R.id.selectAllCheckBox);
        noitenTv = (TextView) findViewById(R.id.noitem);
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
        this.Compress = new File(this.root.getAbsolutePath() + "/Extractor/Compressed");
        this.Extracted = new File(this.root.getAbsolutePath() + "/Extractor/Extract");
        try {
            if (!file2.exists()) {
                file2.mkdirs();
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fragment_storage_list);
        this.mStorageListView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mStorageListView.setLayoutManager(new GridLayoutManager(this, 1));
        new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(Calendar.getInstance().getTime());
        this.temp = new File(this.root.getAbsolutePath() + "/Extractor/.temp_Compressed");
        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.menu);
        fabmenu = floatingActionMenu;
        floatingActionMenu.setVisibility(View.GONE);
        this.selectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AllImagesActivity.this.selectAllCheckBox.isChecked()) {
                    AllImagesActivity.this.selectAllFiles();
                    AllImagesActivity.this.mFileItemAdapter.notifyDataSetChanged();
                    AllImagesActivity.fabmenu.setVisibility(View.VISIBLE);
                    return;
                }
                AllImagesActivity.this.UnSelectAllFiles();
                AllImagesActivity.this.mFileItemAdapter.notifyDataSetChanged();
                AllImagesActivity.fabmenu.setVisibility(View.GONE);
            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.zip);
        this.fabCompress = floatingActionButton;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AllImagesActivity.selectedItems.size() > 0) {
                    AllImagesActivity.this.openSaveDialog();
                } else {
                    Toast.makeText(AllImagesActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FloatingActionButton floatingActionButton2 = (FloatingActionButton) findViewById(R.id.delete);
        this.fabDelete = floatingActionButton2;
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AllImagesActivity.selectedItems.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AllImagesActivity.this);
                    builder.setMessage(AllImagesActivity.this.getString(R.string.deletesure));
                    builder.setCancelable(true);
                    builder.setPositiveButton(AllImagesActivity.this.getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Iterator<Data_Model> it = AllImagesActivity.selectedItems.iterator();
                            while (it.hasNext()) {
                                AllImagesActivity.this.onRemoveFile(it.next());
                                AllImagesActivity.this.dismissProgressDialog1();
                            }
                            AllImagesActivity.fabmenu.setVisibility(View.GONE);
                            AllImagesActivity.fabmenu.close(true);
                            new FilesTask().execute(new String[0]);
                            AllImagesActivity.selectedItems.clear();
                        }
                    });
                    builder.setNegativeButton(AllImagesActivity.this.getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            AllImagesActivity.selectedItems.clear();
                        }
                    });
                    builder.create().show();
                    return;
                }
                Toast.makeText(AllImagesActivity.this, "No File Selected", Toast.LENGTH_SHORT).show();
            }
        });
        new FilesTask().execute(new String[0]);
        SearchView searchView = (SearchView) findViewById(R.id.search);
        this.searchView = searchView;
        searchView.setIconified(false);
        this.searchView.clearFocus();
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str2) {
                if (AllImagesActivity.this.mFileItemAdapter == null) {
                    return false;
                }
                AllImagesActivity.this.mFileItemAdapter.getFilter().filter(str2);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str2) {
                if (AllImagesActivity.this.mFileItemAdapter == null) {
                    return false;
                }
                AllImagesActivity.this.mFileItemAdapter.getFilter().filter(str2);
                return false;
            }
        });
    }


    public void selectAllFiles() {
        Iterator<Data_Model> it = this.files.iterator();
        while (it.hasNext()) {
            Data_Model next = it.next();
            next.setSelected(true);
            selectedItems.add(next);
        }
    }


    public void UnSelectAllFiles() {
        Iterator<Data_Model> it = this.files.iterator();
        while (it.hasNext()) {
            Data_Model next = it.next();
            next.setSelected(false);
            selectedItems.remove(next);
        }
    }

    public static void setTvNoSearchFoundVisibility(int i) {
        noitenTv.setVisibility(i);
    }


    public void openSaveDialog() {
        final AlertDialog create = new AlertDialog.Builder(this).create();
        View inflate = getLayoutInflater().inflate(R.layout.custom_dialog_box, (ViewGroup) null);
        create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        inflate.findViewById(R.id.linCompressFiles7Zip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create.dismiss();
                final Dialog dialog = new Dialog(AllImagesActivity.this);
                dialog.requestWindowFeature(1);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_already);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                final EditText editText = (EditText) dialog.findViewById(R.id.edtFileName);
                AllImagesActivity.this.fileNameToSave = editText.getText().toString();
                dialog.findViewById(R.id.linCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.linOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        AllImagesActivity.this.fileNameToSave = editText.getText().toString();
                        if (AllImagesActivity.this.fileNameToSave.length() > 1) {
                            dialog.dismiss();
                            AllImagesActivity.this.OnCompressClick();
                            return;
                        }
                        Toast.makeText(AllImagesActivity.this, "Please Enter a name", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        inflate.findViewById(R.id.linCompressFilesZip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AllImagesActivity.selectedItems.size() > 0) {
                    create.dismiss();
                    final AlertDialog create2 = new AlertDialog.Builder(AllImagesActivity.this).create();
                    View inflate2 = AllImagesActivity.this.getLayoutInflater().inflate(R.layout.dialog_already, (ViewGroup) null);
                    create2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    final EditText editText = (EditText) inflate2.findViewById(R.id.edtFileName);
                    editText.requestFocus();
                    ((InputMethodManager) AllImagesActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(2, 0);
                    inflate2.findViewById(R.id.linOK).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {
                            AllImagesActivity.this.fileName = editText.getText().toString();
                            if (AllImagesActivity.this.fileName.isEmpty()) {
                                return;
                            }
                            create2.dismiss();
                            new Intent(AllImagesActivity.this, ZipFileService.class);
                            ArrayList arrayList = new ArrayList();
                            for (int i = 0; i < AllImagesActivity.selectedItems.size(); i++) {
                                arrayList.add(AllImagesActivity.selectedItems.get(i).getFilePath());
                            }
                            new Zip_Compression_bgTask().execute(Integer.valueOf(AllImagesActivity.selectedItems.size()));
                        }
                    });
                    inflate2.findViewById(R.id.linCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {
                            create2.dismiss();
                        }
                    });
                    create2.setView(inflate2);
                    create2.show();
                }
            }
        });
        create.setView(inflate);
        create.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void OnCompressClick() {
        showProgressDialog1();
        Iterator<Data_Model> it = selectedItems.iterator();
        while (it.hasNext()) {
            Data_Model next = it.next();
            File file = new File(next.getFilePath());
            FileUtils.copyFile(file, new File(this.temp + InternalZipConstants.ZIP_FILE_SEPARATOR + next.getFileName()), new FileUtils.OnReplaceListener() {
                @Override
                public boolean onReplace() {
                    return false;
                }
            });
        }
        onCompressMultiFile(this.temp.getAbsolutePath(), "7z", this.Compress.getAbsolutePath(), this.fileNameToSave);
    }

    public ArrayList<Data_Model> getListFiles(File file) {
        ArrayList<Data_Model> arrayList = new ArrayList<>();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    arrayList.addAll(getListFiles(file2));
                } else if (getIntent().getIntExtra("type", 20) == 20) {
                    String name = file2.getName();
                    String[] strArr = All_Doc_ARRAY;
                    if (name.endsWith(strArr[0]) || file2.getName().endsWith(strArr[1]) || file2.getName().endsWith(strArr[2]) || file2.getName().endsWith(strArr[3]) || file2.getName().endsWith(strArr[4])) {
                        arrayList.add(Utils.getFileInfoFromPath2(file2.getPath()));
                    }
                }
            }
        }
        return arrayList;
    }


    public static class Open_File_By_File_Format {
        public static void openFile(Context context, File file) throws IOException {
            Uri fromFile = Uri.fromFile(file);
            Intent intent = new Intent("android.intent.action.VIEW");
            if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                intent.setDataAndType(fromFile, "application/msword");
            } else if (file.toString().contains(".pdf")) {
                intent.setDataAndType(fromFile, "application/pdf");
            } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                intent.setDataAndType(fromFile, "application/vnd.ms-powerpoint");
            } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                intent.setDataAndType(fromFile, "application/vnd.ms-excel");
            } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
                intent.setDataAndType(fromFile, "application/x-wav");
            } else if (file.toString().contains(".wav") || file.toString().contains(".mp3")) {
                intent.setDataAndType(fromFile, "audio/*");
            } else if (file.toString().contains(".gif")) {
                intent.setDataAndType(fromFile, "image/gif");
            } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
                intent.setDataAndType(fromFile, "image/*");
            } else if (file.toString().contains(".txt")) {
                intent.setDataAndType(fromFile, "text/plain");
            } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                intent.setDataAndType(fromFile, "video/*");
            } else {
                intent.setDataAndType(fromFile, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                context.startActivity(intent);
            } catch (Exception unused) {
                Toast.makeText(context, "No default Application found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        final Data_Model data_Model = (Data_Model) view.getTag();
        String filePath = data_Model.getFilePath();
        int id = view.getId();
        if (id != R.id.item) {
            if (id == R.id.linMenu) {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog_compress);
                dialog.findViewById(R.id.linOpenFile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            try {
                                StrictMode.class.getMethod("disableDeathOnFileUriExposure", new Class[0]).invoke(null, new Object[0]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Open_File_By_File_Format.openFile(AllImagesActivity.this, new File(data_Model.getFilePath()));
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        dialog.cancel();
                    }
                });
                dialog.findViewById(R.id.linCompressFiles).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        AllImagesActivity.this.onCompressFile(data_Model);
                        dialog.cancel();
                    }
                });
                dialog.findViewById(R.id.linShareFiles).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("image/*");
                        intent.putExtra("android.intent.extra.STREAM", Uri.parse(data_Model.getFilePath()));
                        try {
                            AllImagesActivity.this.startActivity(Intent.createChooser(intent, "Share ZIP File"));
                        } catch (Exception unused) {
                        }
                        dialog.cancel();
                    }
                });
                dialog.findViewById(R.id.linDeleteFiles).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        dialog.cancel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(AllImagesActivity.this);
                        builder.setMessage(AllImagesActivity.this.getString(R.string.deletesure));
                        builder.setCancelable(true);
                        builder.setPositiveButton(AllImagesActivity.this.getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AllImagesActivity.this.onRemoveFile(data_Model);
                                new FilesTask().execute(new String[0]);
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton(AllImagesActivity.this.getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
                dialog.show();
                return;
            }
            return;
        }
        Preferences preferences = this.preferences;
        int GetValueInt = preferences.GetValueInt(preferences.AddDialog);
        Preferences preferences2 = this.preferences;
        if (preferences2.GetValue(preferences2.Installed)) {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    StrictMode.class.getMethod("disableDeathOnFileUriExposure", new Class[0]).invoke(null, new Object[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Open_File_By_File_Format.openFile(this, new File(filePath));
                return;
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                StrictMode.class.getMethod("disableDeathOnFileUriExposure", new Class[0]).invoke(null, new Object[0]);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        try {
            Open_File_By_File_Format.openFile(this, new File(filePath));
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        Preferences preferences3 = this.preferences;
        preferences3.SetValue(preferences3.AddDialog, GetValueInt + 1);
    }

    public void onRemoveFile(final Data_Model data_Model) {
        showProgressDialog1();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                String message;
                try {
                    AllImagesActivity.this.removeFile(new File(data_Model.getFilePath()));
                    message = "Deleted: " + data_Model.getFileName();
                } catch (Exception e) {
                    message = e.getMessage();
                }
                observableEmitter.onNext(message);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String str) throws Exception {
                if (AllImagesActivity.this.isFinishing()) {
                    return;
                }
                AllImagesActivity.this.dismissProgressDialog1();
            }
        });
    }

    public void onCompressFile(Data_Model data_Model) {
        this.check = 1;
        String filePath = data_Model.getFilePath();
        runCommand(File_order_holder.getCompressCmd(filePath, this.Compress + InternalZipConstants.ZIP_FILE_SEPARATOR + data_Model.getFileName() + ".7z", "7z"));
    }

    public void onCompressMultiFile(String str, String str2, String str3, String str4) {
        this.check = 1;
        runCommand2(File_order_holder.getCompressCmd(str, str3 + InternalZipConstants.ZIP_FILE_SEPARATOR + str4 + "." + str2, str2));
    }

    private void runCommand(final String str) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(Integer.valueOf(P7ZipApi.executeCommand(str)));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer num) throws Exception {
                if (!AllImagesActivity.this.isFinishing()) {
                    AllImagesActivity.this.dismissProgressDialog1();
                }
                AllImagesActivity.this.showResult(num.intValue());
            }
        });
    }

    private void runCommand2(final String str) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                observableEmitter.onNext(Integer.valueOf(P7ZipApi.executeCommand(str)));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer num) throws Exception {
                if (!AllImagesActivity.this.isFinishing()) {
                    AllImagesActivity.this.dismissProgressDialog1();
                }
                AllImagesActivity.this.showResult(num.intValue());
            }
        });
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
        new Intent(this, Extract_Files_activity.class);
        if (i == 255) {
            if (isFinishing()) {
                return;
            }
            new AlertDialog.Builder(this).setMessage(getString(R.string.mesag_ret_user_stop_this)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else if (i == 0) {
            String[] list = this.temp.list();
            if (list != null) {
                for (String str : list) {
                    new File(this.temp, str).delete();
                }
            }
            final AlertDialog create = new AlertDialog.Builder(this).create();
            View inflate = getLayoutInflater().inflate(R.layout.dialog_success_comressed, (ViewGroup) null);
            create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            ((TextView) inflate.findViewById(R.id.txtMessage)).setText("Successfully Compressed");
            inflate.findViewById(R.id.linOpenFolder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AllImagesActivity allImagesActivity = AllImagesActivity.this;
                    allImagesActivity.startActivity(new Intent(allImagesActivity, CompressFilesActivity.class));
                    AllImagesActivity.this.finish();
                    create.cancel();
                }
            });
            inflate.findViewById(R.id.linOK).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    create.cancel();
                }
            });
            if (isFinishing()) {
                return;
            }
            create.setView(inflate);
            create.show();
        } else if (i == 1) {
            if (isFinishing()) {
                return;
            }
            new AlertDialog.Builder(this).setMessage(getString(R.string.msg_ret_warning)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else if (i == 2) {
            if (isFinishing()) {
                return;
            }
            new AlertDialog.Builder(this).setMessage(getString(R.string.message_ret_faults)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else if (i != 7) {
            if (i != 8 || isFinishing()) {
                return;
            }
            new AlertDialog.Builder(this).setMessage(getString(R.string.msg_ret_memmory)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i2) {
                    if (dialogInterface != null) {
                        dialogInterface.dismiss();
                    }
                }
            }).create().show();
        } else if (!isFinishing()) {
            new AlertDialog.Builder(this).setMessage(getString(R.string.message_ret_commnds)).setCancelable(true).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i2) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
    }


    private class FilesTask extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;

        private void showProgressDialog() {
            if (this.dialog == null) {
                ProgressDialog progressDialog = new ProgressDialog(AllImagesActivity.this);
                this.dialog = progressDialog;
                progressDialog.setTitle(AllImagesActivity.this.getString(R.string.pelasewait));
                progressDialog.setMessage(AllImagesActivity.this.getString(R.string.take_som));
                this.dialog.setCancelable(false);
            }
            this.dialog.show();
        }

        private void dismissProgressDialog() {
            ProgressDialog progressDialog = this.dialog;
            if (progressDialog == null || !progressDialog.isShowing()) {
                return;
            }
            this.dialog.dismiss();
        }

        private FilesTask() {
        }

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public String doInBackground(String... strArr) {
            AllImagesActivity allImagesActivity = AllImagesActivity.this;
            allImagesActivity.files = allImagesActivity.getListFiles(allImagesActivity.root);
            if (AllImagesActivity.this.FilesExternal == null) {
                return null;
            }
            AllImagesActivity allImagesActivity2 = AllImagesActivity.this;
            allImagesActivity2.filXESsd = allImagesActivity2.getListFiles(allImagesActivity2.FilesExternal);
            AllImagesActivity.this.files.addAll(AllImagesActivity.this.filXESsd);
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            dismissProgressDialog();
            AllImagesActivity allImagesActivity = AllImagesActivity.this;
            All_files_adapter all_files_adapter = new All_files_adapter(allImagesActivity, allImagesActivity);
            allImagesActivity.mFileItemAdapter = all_files_adapter;
            AllImagesActivity.this.mStorageListView.setAdapter(all_files_adapter);
            AllImagesActivity.this.mFileItemAdapter.setDataList(AllImagesActivity.this.files);
            AllImagesActivity.this.mFileItemAdapter.notifyDataSetChanged();
            View findViewById = AllImagesActivity.this.findViewById(R.id.noitem);
            if (all_files_adapter.getItemCount() < 1) {
                findViewById.setVisibility(View.VISIBLE);
                AllImagesActivity.fabmenu.setVisibility(View.GONE);
                return;
            }
            findViewById.setVisibility(View.GONE);
        }
    }


    public class Zip_Compression_bgTask extends AsyncTask<Integer, Integer, String> {
        int count = 0;
        private ProgressDialog dialog;
        private String name;

        public Zip_Compression_bgTask() {
        }

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }


        @Override
        public String doInBackground(Integer... numArr) {


            File file = null;
            try {
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
                zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
                ZipFile zipFile = new ZipFile(MainActivityHomeDocuments.mediaStorageDir.getAbsolutePath() + File.separator + fileName + ".zip");
                if (selectedItems.size() <= 0) {
                    return null;
                }
                int totalItems = selectedItems.size();
                count = 0;
                Iterator<Data_Model> iterator = selectedItems.iterator();
                while (iterator.hasNext()) {
                    count++;
                    Data_Model dataModel = iterator.next();
                    file = new File(dataModel.getFilePath());
                    zipFile.addFile(file, zipParameters);
                    int progress = (int) (((float) count / totalItems) * 100);
                    String progressMessage = "progress " + progress + " Processing file " + count + "/" + selectedItems.size();
                    dialog.setMessage("Please wait... " + count + "/" + selectedItems.size());
                    dialog.setProgress(progress);
                    Log.e("MYTAG", "ErrorNo: doInBackground:" + progressMessage);
                }
                return null;
            } catch (ZipException e) {
                e.printStackTrace();
                Log.e("MYTAG", "ErrorNo: doInBackground:" + e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(String str) {
            dismissProgressDialog();
            AllImagesActivity.selectedItems.clear();
            AlertDialog.Builder builder = new AlertDialog.Builder(AllImagesActivity.this);
            builder.setTitle(R.string.sucess_zip);
            builder.setMessage(AllImagesActivity.this.getString(R.string.zipping_3) + this.name);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton(R.string.open_folder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AllImagesActivity.this.startActivity(new Intent(AllImagesActivity.this, CompressFilesActivity.class));
                    AllImagesActivity.this.finish();
                }
            });
            builder.create().show();
        }

        private void showProgressDialog() {
            if (this.dialog == null) {
                ProgressDialog progressDialog = new ProgressDialog(AllImagesActivity.this);
                this.dialog = progressDialog;
                progressDialog.setMessage(AllImagesActivity.this.getString(R.string.zip34));
                progressDialog.setIndeterminate(true);
                progressDialog.setMax(AllImagesActivity.selectedItems.size());
                progressDialog.setProgressStyle(1);
                this.dialog.setCancelable(false);
            }
            this.dialog.show();
        }

        private void dismissProgressDialog() {
            ProgressDialog progressDialog = this.dialog;
            if (progressDialog == null || !progressDialog.isShowing()) {
                return;
            }
            this.dialog.dismiss();
        }
    }

    public void showProgressDialog1() {
        if (isFinishing()) {
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.dialog1 = progressDialog;
        progressDialog.setTitle(getText(R.string.pelasewait));
        this.dialog1.setMessage(getText(R.string.process_progress_messag));
        this.dialog1.setCancelable(false);
        this.dialog1.show();
    }

    public void dismissProgressDialog1() {
        try {
            ProgressDialog progressDialog = this.dialog1;
            if (progressDialog == null || !progressDialog.isShowing() || isFinishing()) {
                return;
            }
            this.dialog1.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class All_files_adapter extends RecyclerView.Adapter<All_files_adapter.ViewHolder> implements Filterable {
        static int counter = 1;
        private final Activity mActivity;
        public List<Data_Model> mFileInfoList;
        public List<Data_Model> mFileInfoListfilter = new ArrayList();
        public View.OnClickListener mItemClickListener;
        View rootView;

        public All_files_adapter(Activity activity, View.OnClickListener onClickListener) {
            this.mActivity = activity;
            this.mItemClickListener = onClickListener;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            if (this.mFileInfoListfilter.size() >= i) {
                final Data_Model data_Model = this.mFileInfoListfilter.get(i);
                if (data_Model.getFileName().endsWith("jpg") || data_Model.getFileName().endsWith("jpeg")) {
                    Glide.with(this.mActivity).load(data_Model.getFilePath()).into(viewHolder.icon);
                } else if (data_Model.getFileName().endsWith("png")) {
                    Glide.with(this.mActivity).load(data_Model.getFilePath()).into(viewHolder.icon);
                } else {
                    viewHolder.icon.setImageResource(R.drawable.home_icon_all_image_main_page_icon);
                }
                if (!data_Model.isFolder()) {
                    viewHolder.subCount.setText(Formatter.formatFileSize(this.mActivity, data_Model.getFileLength()));
                }
                viewHolder.item.setTag(data_Model);
                viewHolder.linMenu.setTag(data_Model);
                viewHolder.fileName.setText(data_Model.getFileName());
                viewHolder.box.setChecked(this.mFileInfoListfilter.get(i).isSelected());
                viewHolder.box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AllImagesActivity.fabmenu.setVisibility(View.VISIBLE);
                        if (AllImagesActivity.checkmenu != 0) {
                            int i2 = AllImagesActivity.checkmenu;
                        }
                        if (viewHolder.box.isChecked()) {
                            All_files_adapter.this.mFileInfoListfilter.get(i).setSelected(true);
                            All_files_adapter.counter++;
                            AllImagesActivity.fabmenu.setVisibility(View.VISIBLE);
                            AllImagesActivity.selectedItems.add(data_Model);
                            return;
                        }
                        AllImagesActivity.selectedItems.remove(data_Model);
                        All_files_adapter.counter--;
                        AllImagesActivity.selectedItems.remove(data_Model);
                        All_files_adapter.counter--;
                        All_files_adapter.this.mFileInfoListfilter.get(i).setSelected(false);
                        if (All_files_adapter.counter == 1) {
                            AllImagesActivity.fabmenu.setVisibility(View.GONE);
                            if (AllImagesActivity.checkmenu == 0) {
                                return;
                            }
                            int i3 = AllImagesActivity.checkmenu;
                        }
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.item_layout_all_image, viewGroup, false);
            this.rootView = inflate;
            return new ViewHolder(inflate);
        }

        public void setDataList(List<Data_Model> list) {
            this.mFileInfoList = list;
            this.mFileInfoListfilter = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return this.mFileInfoListfilter.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                public Filter.FilterResults performFiltering(CharSequence charSequence) {
                    String charSequence2 = charSequence.toString();
                    if (charSequence2.isEmpty()) {
                        All_files_adapter all_files_adapter = All_files_adapter.this;
                        all_files_adapter.mFileInfoListfilter = all_files_adapter.mFileInfoList;
                    } else {
                        final ArrayList arrayList = new ArrayList();
                        for (Data_Model data_Model : All_files_adapter.this.mFileInfoList) {
                            if (data_Model.getFileName().toLowerCase().contains(charSequence2.toLowerCase())) {
                                arrayList.add(data_Model);
                            }
                        }
                        All_files_adapter all_files_adapter2 = All_files_adapter.this;
                        all_files_adapter2.mFileInfoListfilter = arrayList;
                        all_files_adapter2.mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (All_files_adapter.this.mActivity instanceof AllImagesActivity) {
                                    if (arrayList.size() == 0) {
                                        AllImagesActivity.setTvNoSearchFoundVisibility(0);
                                    } else {
                                        AllImagesActivity.setTvNoSearchFoundVisibility(8);
                                    }
                                }
                            }
                        });
                    }
                    Filter.FilterResults filterResults = new Filter.FilterResults();
                    filterResults.values = All_files_adapter.this.mFileInfoListfilter;
                    return filterResults;
                }

                @Override
                public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                    All_files_adapter.this.mFileInfoListfilter = (ArrayList) filterResults.values;
                    All_files_adapter.this.notifyDataSetChanged();
                }
            };
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox box;
            TextView fileName;
            ImageView icon;
            LinearLayout item;
            LinearLayout linMenu;
            TextView subCount;

            public ViewHolder(View view) {
                super(view);
                this.icon = (ImageView) view.findViewById(R.id.file_item_icon);
                this.fileName = (TextView) view.findViewById(R.id.file_item_name);
                this.subCount = (TextView) view.findViewById(R.id.file_sub_count);
                this.linMenu = (LinearLayout) view.findViewById(R.id.linMenu);
                this.item = (LinearLayout) view.findViewById(R.id.item);
                this.box = (CheckBox) view.findViewById(R.id.check);
                this.linMenu.setOnClickListener(All_files_adapter.this.mItemClickListener);
                this.item.setOnClickListener(All_files_adapter.this.mItemClickListener);
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
                    intent4.putExtra("android.intent.extra.TEXT", "Hi! I'm using a great Unzip Files application. Check it out:http://play.google.com/store/apps/details?id=" + getPackageName());
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
