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

    /**
     * Given a context and a key-set, this method saves all items from the
     * Headers object into the SharedPreferences for access later throughout
     * the application
     * @param context The context to use when accessing the SharedPreferences object
     * @param set The set that should store the headers. This is currently simply "Headers"
     * @param headers The set of headers to save
     */
    public static void saveHeaders(Context context, String set, Headers headers) {
        // Get the SharedPreferences and prepare to edit it
        SharedPreferences prefs = context.getSharedPreferences(set, 0);
        SharedPreferences.Editor editor = prefs.edit();

        // IMPORTANT: We do not want cookies to be lingering from
        // past app sessions, hence everything needs to be cleared first.
        editor.clear();

        // Add all headers through editor
        for(String headerName : headers.names()) {
            if(!headerName.contains("cookie") && !headerName.contains("cnection")) {
                editor.putString(headerName, headers.get(headerName));
            }
        }

        // TODO: Figure out if this might cause race conditions, compare to .commit()
        editor.apply();
    }

    /**
     * Given a context, key-set, and OkHttp3 Request.Builder object, this applies
     * all key-value pairs in the SharedPreferences set designated by the key-set
     * to the builder. Specifically, this method is used to apply headers to the
     * builder, the most important of which is the X-Sakai-Session header.
     * @param context The context to use when accessing the SharedPreferences object
     * @param set The set that should store the headers. This is currently simply "Headers"
     * @param builder The Request.Builder object on which to apply the headers
     */
    public static void applyHeaders(Context context, String set, Request.Builder builder) {
        // Get the SharedPreferences first and all entries in the set first
        SharedPreferences prefs = context.getSharedPreferences(set, 0);

        // This is a Map of generics <String, ?> because all keys are Strings,
        // but SharedPrefs allows the values to be of any primitive type.
        Map<String, ?> allEntries = prefs.getAll();

        // Iterate through all entries
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();

            // We can simply cast the value to a String since we know
            // that we only set String-valued headers in the past.
            String value = (String)entry.getValue();

            // We do not want to set the Headers designated by "Set-Cookies"
            // or "X-Cnection", since those might tamper with our intended requests.
            if(!key.contains("cookie") || !key.contains("cnection")) {
                builder.addHeader(key, value);
            }
        }
    }

}
