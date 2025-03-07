package com.eipna.centsation.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.eipna.centsation.data.Contrast;
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

    public void setSortCriteria(String value) {
        sharedPreferences.edit().putString("sort_criteria", value).apply();
    }

    public String getSortCriteria() {
        return sharedPreferences.getString("sort_criteria", SavingSort.NAME.SORT);
    }

    public void setSortOrder(boolean value) {
        sharedPreferences.edit().putBoolean("sort_order", value).apply();
    }

    public boolean getSortOrder() {
        return sharedPreferences.getBoolean("sort_order", true);
    }

    public String getContrast() {
        return sharedPreferences.getString("contrast", Contrast.LOW.VALUE);
    }

    public void setContrast(String value) {
        sharedPreferences.edit().putString("contrast", value).apply();
    }
}