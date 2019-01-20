package com.icubed.loansticdroid.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.lang.reflect.Method;

import static android.support.constraint.Constraints.TAG;

public class AndroidUtils {

    Context context;

    public AndroidUtils(Context context) {
        this.context = context;
    }

    public void gpsDisabledMessage(){
        Log.d(TAG, "gpsDisabledMessage: called");
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public static boolean isMobileDataEnabled(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
            return mobileDataEnabled;
        } catch (Exception e) {
            // Some problem accessible private API
            Log.d(TAG, "isMobileDataEnabled: "+e.getMessage());
            return false;
        }
    }
}
