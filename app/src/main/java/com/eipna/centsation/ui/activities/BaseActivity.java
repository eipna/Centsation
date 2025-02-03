package com.eipna.centsation.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eipna.centsation.util.SharedPreferencesUtil;

public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
        super.onCreate(savedInstanceState);
    }
}