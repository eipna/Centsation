package com.eipna.centsation.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.eipna.centsation.data.Theme;
import com.eipna.centsation.util.PreferenceUtil;
import com.eipna.centsation.util.ThemeUtil;
import com.google.android.material.color.DynamicColors;

public abstract class BaseActivity extends AppCompatActivity {

    protected PreferenceUtil preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = new PreferenceUtil(this);
        super.onCreate(savedInstanceState);

        ThemeUtil.set(preferences.getTheme());
        if (preferences.isDynamicColors()) DynamicColors.applyToActivityIfAvailable(this);
    }
}