package com.example.books;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SharedPreferencesUtil {
    private SharedPreferencesUtil(){}
    public static final String PREF_NAME = "BooksPreferences";
    public static final String POSITION = "positions";
    public static final String QUERY = "query";

    public static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public static String getPrefsString(Context context, String key){
        return getPrefs(context).getString(key, "");
    }
    public static void setPrefString(Context context, String key, String value){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static int getPrefsInt(Context context, String key){
        return getPrefs(context).getInt(key, 0);
    }
    public static void setPrefInt(Context context, String key, int value){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static ArrayList<String> getQueryList(Context context){
        ArrayList<String> queryList = new ArrayList<>();
        for (int i=1;i<=5;i++){
            String query = getPrefs(context).getString(QUERY+i, "");
            if (!query.isEmpty()){
                query = query.replace(","," ");
                queryList.add(query);
            }
        }
        return queryList;
    }
}
