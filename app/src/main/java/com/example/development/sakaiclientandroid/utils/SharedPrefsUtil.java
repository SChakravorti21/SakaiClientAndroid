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

    private static Context context;

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static void saveHeaders(String set, Headers headers) {
        SharedPreferences prefs = context.getSharedPreferences(set, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();

        for(String headerName : headers.names()) {
            if(!headerName.contains("set-cookie") && !headerName.contains("cnection")) {
                Log.i("Putting ~ " + headerName, headers.get(headerName));
                editor.putString(headerName, headers.get(headerName));
            }
        }

        editor.commit();
    }

    public static void applyHeaders(String set, Request.Builder builder) {
        SharedPreferences prefs = context.getSharedPreferences(set, 0);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            String value = (String)entry.getValue();

            if(!key.contains("cookie") || !key.contains("cnection")) {
                key = toCapitalCase(key);
                Log.i("Adding header ~ " + key, value);
                builder.addHeader(key, value);
            }
        }
    }

    private static String toCapitalCase(String str) {
        StringBuilder sb = new StringBuilder();
        boolean capitalize = true;

        for(int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);
            if(capitalize) {
                sb.append(("" + character).toUpperCase());
                capitalize = false;
            } else {
                if(character == '-')
                    capitalize = true;

                sb.append(character);
            }
        }

        return sb.toString();
    }

}
