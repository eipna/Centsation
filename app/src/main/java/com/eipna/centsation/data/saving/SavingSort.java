package com.eipna.centsation.data.saving;


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
}