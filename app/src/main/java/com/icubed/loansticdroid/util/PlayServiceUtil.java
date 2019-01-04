package com.icubed.loansticdroid.util;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PlayServiceUtil {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 4242;
    Context context;

    public PlayServiceUtil(Context context) {
        this.context = context;
    }

    /**********************check play service compatibility*************/
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog((Activity) context, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

}
