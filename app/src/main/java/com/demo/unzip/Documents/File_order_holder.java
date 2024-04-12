package com.demo.unzip.Documents;


public class File_order_holder {
    public static String getExtractCmd(String str, String str2) {
        return String.format("7z x '%s' '-o%s' -aoa", str, str2);
    }

    public static String getCompressCmd(String str, String str2, String str3) {
        return String.format("7z a -t%s '%s' '%s'", str3, str2, str);
    }
}
