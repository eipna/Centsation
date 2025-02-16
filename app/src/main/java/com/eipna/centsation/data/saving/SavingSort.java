package com.eipna.centsation.data.saving;


import android.view.MenuItem;

import com.eipna.centsation.R;

import java.util.Comparator;

public enum SavingSort {

    NAME_ASCENDING("name_ascending", Saving.SORT_NAME_ASCENDING),
    NAME_DESCENDING("name_descending", Saving.SORT_NAME_DESCENDING),
    VALUE_LOWEST("value_lowest", Saving.SORT_VALUE_LOWEST),
    VALUE_HIGHEST("value_highest", Saving.SORT_VALUE_HIGHEST),
    GOAL_LOWEST("goal_lowest", Saving.SORT_GOAL_LOWEST),
    GOAL_HIGHEST("goal_highest", Saving.SORT_GOAL_HIGHEST);

    private static final SavingSort[] sorts;
    public final String NAME;
    public final Comparator<Saving> SORT;

    static {
        sorts = values();
    }

    SavingSort(String name, Comparator<Saving> sort) {
        this.NAME = name;
        this.SORT = sort;
    }

    public static SavingSort fromName(String name) {
        for (SavingSort savingSort : sorts) {
            if (name.equals(savingSort.NAME)) {
                return savingSort;
            }
        }
        return null;
    }

    public static int getMenuItem(SavingSort savingSort) {
        if (savingSort.equals(SavingSort.NAME_ASCENDING)) {
            return R.id.saving_sort_name_ascending;
        } else if (savingSort.equals(SavingSort.NAME_DESCENDING)) {
            return R.id.saving_sort_name_descending;
        } else if (savingSort.equals(SavingSort.VALUE_LOWEST)) {
            return R.id.saving_sort_value_lowest;
        } else if (savingSort.equals(SavingSort.VALUE_HIGHEST)) {
            return R.id.saving_sort_value_highest;
        } else if (savingSort.equals(SavingSort.GOAL_LOWEST)) {
            return R.id.saving_sort_goal_lowest;
        } else if (savingSort.equals(SavingSort.GOAL_HIGHEST)) {
            return R.id.saving_sort_goal_highest;
        }
        return -1;
    }
}