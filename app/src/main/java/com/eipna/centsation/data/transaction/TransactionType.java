package com.eipna.centsation.data.transaction;

public enum TransactionType {
    ADD("Add"),
    DEDUCT("Deduct");

    private static final TransactionType[] types;
    private final String TYPE;

    static {
        types = values();
    }

    TransactionType(String type) {
        this.TYPE = type;
    }
}