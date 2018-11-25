package com.example.development.sakaiclient20.networking.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Development on 5/13/18.
 */

public class SharedPrefsUtil {

    private static String TREE_SET = "TREE_SET";
    public static String ALL_GRADES_TREE_TYPE = "ALL_GRADES";
    public static String ASSIGNMENTS_TREE_TYPE = "ASSIGNMENTS";
    public static String ALL_COURSES_TREE_TYPE = "ALL_COURSES";


    public static String getTreeState(Context context, String treeType) {
        SharedPreferences sharedPref = context.getSharedPreferences(TREE_SET, Context.MODE_PRIVATE);
        return sharedPref.getString(treeType, "");
    }

    public static void saveTreeState(Context context, AndroidTreeView tree, String treeType) {
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
