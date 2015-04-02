package com.example.motiondetection.app;


import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.common.api.CommonStatusCodes;

public class GoogleClientErrorMessages {

    private GoogleClientErrorMessages() {
    }


    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case CommonStatusCodes.SERVICE_VERSION_UPDATE_REQUIRED:
                return mResources.getString(R.string.common_google_play_services_update_text);
            case CommonStatusCodes.NETWORK_ERROR:
                return mResources.getString(R.string.common_google_play_services_network_error_text);
            case CommonStatusCodes.SERVICE_MISSING:
                return mResources.getString(R.string.common_google_play_services_install_text_phone);
            default:
                return mResources.getString(R.string.common_google_play_services_unknown_issue);
        }
    }
}
