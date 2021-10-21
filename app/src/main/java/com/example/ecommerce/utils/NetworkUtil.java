package com.example.ecommerce.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Pagination
 * Created by Subhasmith Thapa on 19,October,2021
 */
public class NetworkUtil {

    public static boolean hasNetwork(Context context) {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) isConnected = true;
        return isConnected;

    }
    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
