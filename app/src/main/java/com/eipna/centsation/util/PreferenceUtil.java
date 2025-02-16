package com.eipna.centsation.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.eipna.centsation.data.Currency;
import com.eipna.centsation.data.Theme;
import com.eipna.centsation.data.saving.SavingSort;

public class PreferenceUtil {

    private final SharedPreferences sharedPreferences;

    public PreferenceUtil(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isDynamicColors() {
        return sharedPreferences.getBoolean("dynamic_colors", false);
    }

    public void setDynamicColors(boolean value) {
        sharedPreferences.edit().putBoolean("dynamic_colors", value).apply();
    }

    public String getTheme() {
        return sharedPreferences.getString("theme", Theme.SYSTEM.VALUE);
    }

    public void setTheme(String value) {
        sharedPreferences.edit().putString("theme", value).apply();
    }

    public String getCurrency() {
        return sharedPreferences.getString("currency", Currency.UNITED_STATES_DOLLAR.CODE);
    }

    public void setCurrency(String value) {
        sharedPreferences.edit().putString("currency", value).apply();
    }

    public void setSavingSort(String value) {
        sharedPreferences.edit().putString("saving_sort", value).apply();
    }

    public String getSavingSort() {
        return sharedPreferences.getString("saving_sort", SavingSort.NAME_ASCENDING.NAME);
    }
}