package com.sakaimobile.development.sakaiclient20.ui.helpers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseIconProvider {

    private static final String ICONS_FILE_NAME = "siteIcons.json";
    private static Map<Integer, String> courseIcons = new HashMap<>();

    /**
     * Initializes the {@link #courseIcons} map at runtime
     * when the application starts. This ensures that all views requiring course icons
     * can get them through {@link #getCourseIcon} can do so.
     * @param context The application context used for retrieving the master icon list
     */
    public static void initializeCourseIcons(Context context) {
        // In case reading the JSON file fails, we won't load any icons
        String rawJson = "{ \"icons\": [ ] }";
        try {
            InputStream inputStream = context.getResources().getAssets().open(ICONS_FILE_NAME);
            byte[] contents = new byte[inputStream.available()];
            inputStream.read(contents);
            rawJson = new String(contents);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            IconList iconList = new Gson().fromJson(rawJson, IconList.class);
            for(IconList.CourseIcon icon : iconList.icons) {
                courseIcons.put(Integer.parseInt(icon.subjectCode), icon.icon);
            }
        }
    }

    /**
     * @return The subject icon if it exists, an empty String otherwise
     */
    public static String getCourseIcon(int subjectCode) {
        return courseIcons.containsKey(subjectCode)
                ? courseIcons.get(subjectCode)
                : "";
    }

    static class IconList {
        List<CourseIcon> icons;

        static class CourseIcon {
            @SerializedName("Android")
            String icon;
            String subjectCode;
        }
    }
}
