package com.example.cmpt276project.tool;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Used to judge network connection and obtain network attributes
 */
public class NetWork {
    public static int NO_NETWORK=0;
    public static int WIFI=1;
    public static int DATA_NETWORK=2;

    /**
     * Determine whether the current network is connected
     * @param context
     * @return
     */
    public static boolean isLink(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isAvailable()){
            return true;
        }
        return false;
    }

    /**
     * Get network type
     * @param context
     * @return
     */
    public static int getNetWorkType(Context context) {
        if (!isLink(context)) {
            return NO_NETWORK;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()){
            return WIFI;
        }else {
            return DATA_NETWORK;
        }
    }
}

