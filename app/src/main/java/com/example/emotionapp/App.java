package com.example.emotionapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class App extends Application {
    SharedPreferencesHelper helper;
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseCrashlytics.getInstance();
        helper = new SharedPreferencesHelper(this);
        if (helper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }
}
