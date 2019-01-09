package com.sakaimobile.development.sakaiclient20.networking.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Development on 5/13/18.
 */

public class SharedPrefsUtil {

    private static String TREE_SET = "TREE_SET";
    public static String ALL_GRADES_TREE_TYPE = "ALL_GRADES";
    public static String ASSIGNMENTS_BY_COURSES_TREE_TYPE = "ASSIGNMENTS_BY_COURSES";
    public static String ASSIGNMENTS_BY_TERM_TREE_TYPE = "ASSIGNMENTS_BY_TERM";
    public static String ALL_COURSES_TREE_TYPE = "ALL_COURSES";
    public static String SITE_RESOURCES_TREE_TYPE = "SITE_RESOURCES";


    public static String getTreeState(Context context, String treeType) {
        SharedPreferences sharedPref = context.getSharedPreferences(TREE_SET, Context.MODE_PRIVATE);

        // We want a default state of "1" (first node is expanded) if the state
        // is not defined or an empty state is saved. This makes it clear to the user that
        // our structures are trees and not just links
        String state = sharedPref.getString(treeType, "1");
        if(state.length() == 0)
            state = "1";

        return state;
    }

    public static void saveTreeState(Context context, AndroidTreeView tree, String treeType) {
        if(tree == null)
            return;

        SharedPreferences sharedPref = context.getSharedPreferences(TREE_SET, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String state = tree.getSaveState();
        editor.putString(treeType, state);
        editor.apply();
    }

    public static void clearTreeStates(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(TREE_SET, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();

        editor.apply();
    }

}
