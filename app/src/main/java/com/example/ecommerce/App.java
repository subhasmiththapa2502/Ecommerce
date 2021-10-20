package com.example.ecommerce;

import android.app.Application;
import android.content.ContextWrapper;

import com.example.ecommerce.utils.Prefs;
import com.google.android.gms.maps.MapsInitializer;

/**
 * Created by Subhasmith Thapa on 19,October,2021
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initSharedPreferences();
        MapsInitializer.initialize(this);
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
