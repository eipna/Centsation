package com.eipna.centsation;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.eipna.centsation.data.Theme;
import com.eipna.centsation.util.SharedPreferencesUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);
        super.onCreate();

        String selectedTheme = sharedPreferencesUtil.getString("theme", Theme.SYSTEM.VALUE);
        if (selectedTheme.equals(Theme.LIGHT.VALUE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (selectedTheme.equals(Theme.DARK.VALUE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (selectedTheme.equals(Theme.BATTERY.VALUE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        } else if (selectedTheme.equals(Theme.SYSTEM.VALUE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}