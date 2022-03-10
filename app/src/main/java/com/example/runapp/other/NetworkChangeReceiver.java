package com.example.runapp.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Broadcast receiver to indicate if network is unavailable
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2021-01-05
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (!checkInternet(context)) {
            Toast.makeText(context, "Network unavailable, Cannot perform online operations", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if internet is available
     * @param context
     * @return true if internet is available
     */
    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }
}