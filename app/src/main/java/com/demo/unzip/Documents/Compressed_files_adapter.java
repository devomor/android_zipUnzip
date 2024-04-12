package com.demo.unzip.Documents;

import android.app.Activity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.demo.unzip.R;
import com.demo.unzip.main.CompressFilesActivity;


public class Compressed_files_adapter extends RecyclerView.Adapter<Compressed_files_adapter.ViewHolder> {
    private final Activity mActivity;
    public View.OnClickListener mItemClickListener;
    View rootView;
    public List<Data_fatcher_holder> mFileInfoListfilter = new ArrayList();
    private List<Data_fatcher_holder> mFileInfoList = new ArrayList();


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView icon;
        LinearLayout item;
        TextView subCount;

        public ViewHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.file_item_icon);
            this.fileName = (TextView) view.findViewById(R.id.file_item_name);
            this.subCount = (TextView) view.findViewById(R.id.file_sub_count);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.item);
            this.item = linearLayout;
            linearLayout.setOnClickListener(Compressed_files_adapter.this.mItemClickListener);
        }
    }

    public Compressed_files_adapter(Activity activity, View.OnClickListener onClickListener, View.OnClickListener onClickListener2) {
        this.mActivity = activity;
        this.mItemClickListener = onClickListener;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            public Filter.FilterResults performFiltering(CharSequence charSequence) {
                String charSequence2 = charSequence.toString();
                if (charSequence2.isEmpty()) {
                    Compressed_files_adapter compressed_files_adapter = Compressed_files_adapter.this;
                    compressed_files_adapter.mFileInfoListfilter = compressed_files_adapter.mFileInfoList;
                } else {
                    final ArrayList arrayList = new ArrayList();
                    for (Data_fatcher_holder data_fatcher_holder : Compressed_files_adapter.this.mFileInfoList) {
                        if (data_fatcher_holder.getFileName().toLowerCase().contains(charSequence2.toLowerCase())) {
                            arrayList.add(data_fatcher_holder);
                        }
                    }
                    Compressed_files_adapter compressed_files_adapter2 = Compressed_files_adapter.this;
                    compressed_files_adapter2.mFileInfoListfilter = arrayList;
                    compressed_files_adapter2.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Compressed_files_adapter.this.mActivity instanceof CompressFilesActivity) {
                                if (arrayList.size() == 0) {
                                    CompressFilesActivity.setTvNoSearchFoundVisibility(0);
                                } else {
                                    CompressFilesActivity.setTvNoSearchFoundVisibility(8);
                                }
                            }
                        }
                    });
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = Compressed_files_adapter.this.mFileInfoListfilter;
                return filterResults;
            }

            @Override
            public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                Compressed_files_adapter.this.mFileInfoListfilter = (ArrayList) filterResults.values;
                Compressed_files_adapter.this.notifyDataSetChanged();
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.compress_files_list_items, viewGroup, false);
        this.rootView = inflate;
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (this.mFileInfoListfilter.size() >= i) {
            Data_fatcher_holder data_fatcher_holder = this.mFileInfoListfilter.get(i);
            int i2 = this.mActivity.getSharedPreferences(extracted_Files_adapter.MY_PREFS_NAME, 0).getInt("id_icon", 0);
            int i3 = i2 == 2 ? R.drawable.files_icon_mp4_icon : R.drawable.files_icon_others_icon;
            if (i2 == 3) {
                i3 = R.drawable.files_icon_mp3__icon;
            }
            if (data_fatcher_holder.getFileName().endsWith(Constants.pdf)) {
                i3 = R.drawable.files_icon_pdf_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.DOC)) {
                i3 = R.drawable.files_icon_doc_icon;
            } else if (data_fatcher_holder.getFileName().endsWith("docx")) {
                i3 = R.drawable.files_icon_dox_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.ppt)) {
                i3 = R.drawable.files_icon_ppt_icon;
            } else if (data_fatcher_holder.getFileName().endsWith("pptx")) {
                i3 = R.drawable.files_icon_pptx_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.xls) || data_fatcher_holder.getFileName().endsWith("xlsx")) {
                i3 = R.drawable.files_icon_xls_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.txt)) {
                i3 = R.drawable.files_icon_txt_icon;
            } else if (data_fatcher_holder.getFileName().endsWith(Constants.psd)) {
                i3 = R.drawable.files_icon_psd_icon;
            } else if (data_fatcher_holder.getFileName().endsWith("apk")) {
                i3 = R.drawable.files_icon_apk_icon;
            }
            int i4 = AnonymousClass2.$SwitchMap$hsa$free$files$compressor$unarchiver$holder$Files_type_holder[data_fatcher_holder.getFileType().ordinal()];
            if (i4 == 1 || i4 == 2) {
                i3 = R.drawable.zip_folder_icon;
            } else if (i4 == 3) {
                i3 = R.drawable.home_compressed_files_icon_yellow;
            }
            if (data_fatcher_holder.isFolder()) {
                viewHolder.subCount.setText(this.mActivity.getString(R.string.items, new Object[]{Integer.valueOf(data_fatcher_holder.getSubCount())}));
            } else {
                viewHolder.subCount.setText(Formatter.formatFileSize(this.mActivity, data_fatcher_holder.getFileLength()));
            }
            viewHolder.item.setTag(data_fatcher_holder);
            viewHolder.fileName.setText(data_fatcher_holder.getFileName());
            if (i2 == 1) {
                viewHolder.icon.setImageResource(R.drawable.zip_folder_icon);
            } else {
                viewHolder.icon.setImageResource(i3);
            }
        }
    }


    public static class AnonymousClass2 {
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
        return this.mFileInfoListfilter.size();
    }

    public void setDataList(List<Data_fatcher_holder> list) {
        this.mFileInfoList = list;
        this.mFileInfoListfilter = list;
        notifyDataSetChanged();
    }
}
