package com.eipna.centsation.data.transaction;

public enum TransactionType {
    ADD("Add"),
    DEDUCT("Deduct");

    private static final TransactionType[] types;
    public final String VALUE;

    static {
        types = values();
    }

    TransactionType(String value) {
        this.VALUE = value;
    }
}