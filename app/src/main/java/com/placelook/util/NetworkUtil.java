package com.placelook.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by victor on 29.10.17.
 */

public class NetworkUtil {
    public static NetworkType getConnectivityType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return NetworkType.PREF_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return NetworkType.PREF_MOBILE;
        }
        return NetworkType.PREF_NONETWORK;
    }
}
