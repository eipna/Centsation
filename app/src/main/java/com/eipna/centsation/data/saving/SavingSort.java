package com.eipna.centsation.data.saving;


import android.view.MenuItem;

import com.eipna.centsation.R;

import java.util.Comparator;

public enum SavingSort {

    NAME("sort_name"),
    VALUE("sort_value"),
    GOAL("sort_goal"),
    ASCENDING("sort_ascending"),
    DESCENDING("sort_descending");

    private static final SavingSort[] sorts;
    public final String SORT;

    static {
        sorts = values();
    }

    SavingSort(String sort) {
        this.SORT = sort;
    }
}