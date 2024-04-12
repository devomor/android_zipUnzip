package com.demo.unzip;

import android.content.Context;
import android.content.SharedPreferences;


public class Preferences {
    public String AddDialog = "addialog";
    public String Installed = "installed";
    public String PREF_NAME = "PREF_NAME";
    SharedPreferences _prefes;
    Context context;
    SharedPreferences.Editor editor;

    public Preferences(Context context) {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", 0);
        this._prefes = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }
    public void SetValue(String str, boolean z) {
        this.editor.putBoolean(str, z);
        this.editor.commit();
        this.editor.apply();
    }
    public void SetValue(String str, int i) {
        this.editor.putInt(str, i);
        this.editor.commit();
        this.editor.apply();
    }
    public void SetValueStringLang(String str, String str2) {
        this.editor.putString(str, str2);
        this.editor.commit();
        this.editor.apply();
    }
    public boolean GetValue(String str) {
        return this._prefes.getBoolean(str, true);
    }

    public int GetValueInt(String str) {
        return this._prefes.getInt(str, 1);
    }

    public String GetValueStringlang(String str) {
        return this._prefes.getString(str, "en");
    }
}
