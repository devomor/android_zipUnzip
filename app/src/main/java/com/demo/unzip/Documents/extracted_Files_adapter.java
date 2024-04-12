package com.demo.unzip.Documents;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.demo.unzip.R;


public class extracted_Files_adapter extends RecyclerView.Adapter<extracted_Files_adapter.ViewHolder> {
    private static final String[] ARCHIVE_ARRAY = {Constants.pdf, Constants.DOC, "docx", Constants.ppt, "pptx", Constants.txt, "apk", "xlsx", Constants.xls, Constants.psd, "mp4", "mp3", "bmp", "jpg", "png", "gif"};
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    int iconId;
    Bitmap imageBitmap;
    private final Activity mActivity;
    private final View.OnClickListener mItemClickListener;
    View rootView;
    final int THUMBNAIL_SIZE = 64;
    byte[] imageData = null;
    private List<Data_fatcher_holder> mFileInfoList = new ArrayList();


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView icon;
        TextView subCount;

        public ViewHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.file_item_icon);
            this.fileName = (TextView) view.findViewById(R.id.file_item_name);
            this.subCount = (TextView) view.findViewById(R.id.file_sub_count);
        }
    }

    public extracted_Files_adapter(Activity activity, View.OnClickListener onClickListener) {
        this.mActivity = activity;
        this.mItemClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.extract_files_list_items, viewGroup, false);
        this.rootView = inflate;
        inflate.setOnClickListener(this.mItemClickListener);
        return new ViewHolder(this.rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (this.mFileInfoList.size() >= i) {
            Data_fatcher_holder data_fatcher_holder = this.mFileInfoList.get(i);
            int i2 = this.mActivity.getSharedPreferences(MY_PREFS_NAME, 0).getInt("id_icon", 0);
            boolean z = data_fatcher_holder.getFileName().endsWith(Constants.xls) || data_fatcher_holder.getFileName().endsWith(Constants.xls);
            int i3 = R.drawable.files_icon_xls_icon;
            if (z) {
                i3 = R.drawable.files_icon_pdf_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.DOC) || data_fatcher_holder.getFileName().endsWith("docx")) {
                i3 = R.drawable.files_icon_doc_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.ppt)) {
                i3 = R.drawable.files_icon_ppt_icon;
            } else if (data_fatcher_holder.getFileName().endsWith("pptx")) {
                i3 = R.drawable.files_icon_pptx_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.txt)) {
                i3 = R.drawable.files_icon_txt_icon;
            } else if (data_fatcher_holder.getFileName().endsWith("apk")) {
                i3 = R.drawable.files_icon_apk_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.psd)) {
                i3 = R.drawable.files_icon_psd_icon;
            } else if (data_fatcher_holder.getFileName().endsWith("mp4")) {
                i3 = R.drawable.files_icon_mp4_icon;
            } else if (data_fatcher_holder.getFileName().endsWith("mp3") || data_fatcher_holder.getFileName().endsWith("jpg") || data_fatcher_holder.getFileName().endsWith("jpeg") || data_fatcher_holder.getFileName().endsWith("png")) {
                i3 = R.drawable.files_icon_mp3__icon;
            } else if (!data_fatcher_holder.getFileName().endsWith(Constants.xls) && !data_fatcher_holder.getFileName().endsWith("xlsx")) {
                if (i2 == 1) {
                    viewHolder.icon.setImageResource(R.drawable.files_icon_jpg_icon);
                } else {
                    viewHolder.icon.setImageResource(R.drawable.files_icon_others_icon);
                }
                i3 = R.drawable.files_icon_others_icon;
            }
            if (i2 == 1) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(data_fatcher_holder.getFilePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                this.imageBitmap = BitmapFactory.decodeStream(fileInputStream);
            }
            int i4 = AnonymousClass1.$SwitchMap$hsa$free$files$compressor$unarchiver$holder$Files_type_holder[data_fatcher_holder.getFileType().ordinal()];
            if (i4 == 1 || i4 == 2 || i4 == 3) {
                i3 = R.drawable.zip_folder_icon;
            }
            if (data_fatcher_holder.isFolder()) {
                viewHolder.subCount.setText(this.mActivity.getString(R.string.items, new Object[]{Integer.valueOf(data_fatcher_holder.getSubCount())}));
            } else {
                viewHolder.subCount.setText(Formatter.formatFileSize(this.mActivity, data_fatcher_holder.getFileLength()));
            }
            viewHolder.itemView.setTag(data_fatcher_holder);
            viewHolder.fileName.setText(data_fatcher_holder.getFileName());
            if (i2 == 1) {
                try {
                    Bitmap bitmap = this.imageBitmap;
                    this.imageBitmap = Bitmap.createScaledBitmap(bitmap, (int) (Float.valueOf(Float.valueOf(bitmap.getWidth()).floatValue() / Float.valueOf(this.imageBitmap.getHeight()).floatValue()).floatValue() * 64.0f), 64, false);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    this.imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    this.imageData = byteArrayOutputStream.toByteArray();
                    viewHolder.icon.setImageBitmap(this.imageBitmap);
                    return;
                } catch (Exception unused) {
                    viewHolder.icon.setImageResource(i3);
                    return;
                }
            }
            viewHolder.icon.setImageResource(i3);
        }
    }


    public static class AnonymousClass1 {
        static final int[] $SwitchMap$hsa$free$files$compressor$unarchiver$holder$Files_type_holder;

        static {
            int[] iArr = new int[Files_type_holder.values().length];
            $SwitchMap$hsa$free$files$compressor$unarchiver$holder$Files_type_holder = iArr;
            iArr[Files_type_holder.folderFull.ordinal()] = 1;
            iArr[Files_type_holder.folderEmpty.ordinal()] = 2;
            try {
                iArr[Files_type_holder.filearchive.ordinal()] = 3;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.mFileInfoList.size();
    }

    public void setDataList(List<Data_fatcher_holder> list) {
        this.mFileInfoList = list;
        notifyDataSetChanged();
    }
}
