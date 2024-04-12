package com.demo.unzip.Documents;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Utils {
    private static final String[] ARCHIVE_ARRAY = {"rar", "zip", "7z", "bz2", "bzip2", "tbz2", "tbz", "gz", "gzip", "tgz", "tar", "xz", "txz"};

    public static class FileComparator implements Comparator<File> {
        private FileComparator() {
        }

        @Override
        public int compare(File file, File file2) {
            int fileScore = Utils.getFileScore(file2) - Utils.getFileScore(file);
            return fileScore == 0 ? file.getName().compareToIgnoreCase(file2.getName()) : fileScore;
        }
    }

    public static Data_fatcher_holder getFileInfoFromPath(String str) {
        Data_fatcher_holder data_fatcher_holder = new Data_fatcher_holder();
        File file = new File(str);
        data_fatcher_holder.setFileName(file.getName());
        data_fatcher_holder.setFilePath(file.getAbsolutePath());
        data_fatcher_holder.setFileType(Files_type_holder.fileunknown);
        if (file.isDirectory()) {
            data_fatcher_holder.setFolder(true);
            data_fatcher_holder.setFileType(Files_type_holder.folderEmpty);
            String[] list = file.list();
            if (list != null && list.length > 0) {
                data_fatcher_holder.setSubCount(list.length);
                data_fatcher_holder.setFileType(Files_type_holder.folderFull);
            }
        } else {
            data_fatcher_holder.setFileLength(file.length());
            if (isArchive(file)) {
                data_fatcher_holder.setFileType(Files_type_holder.filearchive);
            }
        }
        return data_fatcher_holder;
    }

    public static Data_Model getFileInfoFromPath2(String str) {
        Data_Model data_Model = new Data_Model();
        File file = new File(str);
        data_Model.setFileName(file.getName());
        data_Model.setFilePath(file.getAbsolutePath());
        data_Model.setSelected(false);
        data_Model.setFileType(Files_type_holder.fileunknown);
        if (file.isDirectory()) {
            data_Model.setFolder(true);
            data_Model.setFileType(Files_type_holder.folderEmpty);
            String[] list = file.list();
            if (list != null && list.length > 0) {
                data_Model.setSubCount(list.length);
                data_Model.setFileType(Files_type_holder.folderFull);
            }
        } else {
            data_Model.setFileLength(file.length());
            if (isArchive(file)) {
                data_Model.setFileType(Files_type_holder.filearchive);
            }
        }
        return data_Model;
    }

    private static boolean isArchive(File file) {
        String name = file.getName();
        String lowerCase = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        for (String str : ARCHIVE_ARRAY) {
            if (lowerCase.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static List<Data_fatcher_holder> getInfoListFromPath(String str) {
        File[] listFiles;
        ArrayList arrayList = new ArrayList();
        File file = new File(str);
        if (file.exists() && file.isDirectory() && file.canRead() && (listFiles = file.listFiles()) != null) {
            Arrays.sort(listFiles, new FileComparator());
            for (File file2 : listFiles) {
                arrayList.add(getFileInfoFromPath(file2.getPath()));
            }
        }
        return arrayList;
    }

    public static List<Data_Model> getInfoListFromPath_n(String str) {
        File[] listFiles;
        ArrayList arrayList = new ArrayList();
        File file = new File(str);
        if (file.exists() && file.isDirectory() && file.canRead() && (listFiles = file.listFiles()) != null) {
            Arrays.sort(listFiles, new FileComparator());
            for (File file2 : listFiles) {
                arrayList.add(getFileInfoFromPath2(file2.getPath()));
            }
        }
        return arrayList;
    }

    public static List<Data_fatcher_holder> getInfoListFromPath(String str, boolean z) {
        File[] listFiles;
        ArrayList arrayList = new ArrayList();
        File file = new File(str);
        if (file.exists() && file.isDirectory() && file.canRead() && (listFiles = file.listFiles()) != null) {
            Arrays.sort(listFiles, new FileComparator());
            for (File file2 : listFiles) {
                arrayList.add(getFileInfoFromPath(file2.getPath()));
            }
        }
        return arrayList;
    }

    public static int getFileScore(File file) {
        return (int) file.length();
    }

    public static String getParentPath(String str) {
        return str.substring(0, str.lastIndexOf(File.separatorChar));
    }
}
