package com.sakaimobile.development.sakaiclient20.networking.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

public class AuthenticationUtils {

    private static final String COOKIE_KEY = "COOKIE";

    public static void setSessionCookie(Context context, String cookie) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(COOKIE_KEY, cookie).apply();
    }

    public static String getSessionCookie(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(COOKIE_KEY, null);
    }

}
