package com.demo.unzip.common;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    private static final String PREF_NAME = "appcompany_hi_en_trans";
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    public PrefManager(Context context) {
        this._context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        this.pref = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }
}
