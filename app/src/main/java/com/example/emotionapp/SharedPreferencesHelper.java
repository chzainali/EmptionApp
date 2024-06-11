package com.example.emotionapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String SHARED_PREF_NAME = "MySharedPref";
    private static final String DARK_MODE_KEY = "darkMode";
    private static final String ALARM_TIME = "TIME";
    private static final String ALARM_DETAILS = "DETAILS";
    private static final String DATE = "data";
    private static final String QUOTE = "quote";


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setDarkMode(boolean darkMode) {
        editor.putBoolean(DARK_MODE_KEY, darkMode);
        editor.apply();
    }

    public void setTime(long time) {
        editor.putLong(ALARM_TIME, time);
        editor.apply();
    }

    public void setDetails(String details) {
        editor.putString(ALARM_DETAILS, details);
        editor.apply();
    }

    public void setQuote(String details) {
        editor.putString(QUOTE, details);
        editor.apply();
    }

    public void setDate(String details) {
        editor.putString(DATE, details);
        editor.apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(DARK_MODE_KEY, false); // Default is false (light mode)
    }

    public Long getTime() {
        return sharedPreferences.getLong(ALARM_TIME, 0L); // Default is false (light mode)
    }

    public String getDetails() {
        return sharedPreferences.getString(ALARM_DETAILS, null); // Default is false (light mode)
    }

    public String getQuote() {
        return sharedPreferences.getString(QUOTE, ""); // Default is false (light mode)
    }
    public String getSavedTime(){
        return sharedPreferences.getString(DATE, ""); // Default is false (light mode)

    }
}
