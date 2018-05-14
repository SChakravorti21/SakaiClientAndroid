package com.example.development.sakaiclientandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * Created by Development on 5/13/18.
 */

public class SharedPrefsUtil {

    public static void saveHeaders(Context context, String set, Headers headers) {
        SharedPreferences prefs = context.getSharedPreferences(set, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();

        for(String headerName : headers.names()) {
            if(!headerName.contains("cookie") && !headerName.contains("cnection")) {
                editor.putString(headerName, headers.get(headerName));
            }
        }

        //TODO: Figure out if this might cause race conditions, compare to .commit()
        editor.apply();
    }

    public static void applyHeaders(Context context, String set, Request.Builder builder) {
        SharedPreferences prefs = context.getSharedPreferences(set, 0);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            String value = (String)entry.getValue();

            if(!key.contains("cookie") || !key.contains("cnection")) {
                builder.addHeader(key, value);
            }
        }
    }

}
