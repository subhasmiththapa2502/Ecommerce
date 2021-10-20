package com.example.ecommerce;

import android.app.Application;
import android.content.ContextWrapper;

import com.example.ecommerce.utils.Prefs;

/**
 * Created by Subhasmith Thapa on 19,October,2021
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initSharedPreferences();
    }

    public void initSharedPreferences(){
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
