package com.eipna.centsation.data.transaction;

public enum TransactionType {
    ADD,
    DEDUCT;

    private static final TransactionType[] types;

    static {
        types = values();
    }
}