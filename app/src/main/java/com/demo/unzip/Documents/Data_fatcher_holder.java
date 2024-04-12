package com.demo.unzip.Documents;


public class Data_fatcher_holder {
    private long fileLength;
    private String fileName;
    private String filePath;
    private Files_type_holder fileType;
    private boolean isFolder;
    private int subCount;

    public Data_fatcher_holder(String str, String str2, boolean z, Files_type_holder files_type_holder) {
        this.fileName = str;
        this.filePath = str2;
        this.isFolder = z;
        this.fileType = files_type_holder;
    }

    public Data_fatcher_holder() {
    }

    public long getFileLength() {
        return this.fileLength;
    }

    public void setFileLength(long j) {
        this.fileLength = j;
    }

    public int getSubCount() {
        return this.subCount;
    }

    public void setSubCount(int i) {
        this.subCount = i;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public boolean isFolder() {
        return this.isFolder;
    }

    public void setFolder(boolean z) {
        this.isFolder = z;
    }

    public Files_type_holder getFileType() {
        return this.fileType;
    }

    public void setFileType(Files_type_holder files_type_holder) {
        this.fileType = files_type_holder;
    }
}
