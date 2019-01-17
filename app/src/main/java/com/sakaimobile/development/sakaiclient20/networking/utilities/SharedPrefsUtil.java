package com.sakaimobile.development.sakaiclient20.networking.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Development on 5/13/18.
 */

public class SharedPrefsUtil {

    private static String TREE_SET = "TREE_SET";
    private static String ANNOUNCEMENTS_SET = "ANNOUNCEMENTS_SET";
    private static String ANNOUNCEMENTS_POSITION = "ANNOUNCEMENTS_POSITION";

    public static String ALL_GRADES_TREE_TYPE = "ALL_GRADES";
    public static String ASSIGNMENTS_BY_COURSES_TREE_TYPE = "ASSIGNMENTS_BY_COURSES";
    public static String ASSIGNMENTS_BY_TERM_TREE_TYPE = "ASSIGNMENTS_BY_TERM";
    public static String ALL_COURSES_TREE_TYPE = "ALL_COURSES";
    public static String SITE_RESOURCES_TREE_TYPE = "SITE_RESOURCES";


    public static void saveAnnouncementScrollState(Context context, int scrollPos) {
        context.getSharedPreferences(ANNOUNCEMENTS_SET, Context.MODE_PRIVATE)
                .edit()
                .putInt(ANNOUNCEMENTS_POSITION, scrollPos)
                .apply();
    }

    public static int getAnnouncementScrollState(Context context) {
        return context.getSharedPreferences(ANNOUNCEMENTS_SET, Context.MODE_PRIVATE)
                .getInt(ANNOUNCEMENTS_POSITION, 0);
    }

    public static String getTreeState(Context context, String treeType) {
        SharedPreferences sharedPref = context.getSharedPreferences(TREE_SET, Context.MODE_PRIVATE);
        // We want a default state of "1" (first node is expanded) if the state
        // is not defined. This makes it clear to the user that our structures
        // are trees and not just links.
        return sharedPref.getString(treeType, "1");
    }

    public static void saveTreeState(Context context, AndroidTreeView tree, String treeType) {
        if(tree == null)
            return;

        String state = tree.getSaveState();
        context.getSharedPreferences(TREE_SET, Context.MODE_PRIVATE)
                .edit()
                .putString(treeType, state)
                .apply();
    }

    public static void clearTreeStates(Context context) {
        context.getSharedPreferences(TREE_SET, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

}
