package com.eipna.centsation.data.saving;

public enum SavingSort {
    NAME("name"),
    VALUE("value"),
    GOAL("goal"),
    ASCENDING("asc"),
    DESCENDING("desc");

    private static final SavingSort[] sorts;
    public final String SORT;

    static {
        sorts = values();
    }

    SavingSort(String sort) {
        this.SORT = sort;
    }
}