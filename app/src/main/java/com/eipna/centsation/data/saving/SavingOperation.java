package com.eipna.centsation.data.saving;

public enum SavingOperation {
    UPDATE,
    HISTORY,
    COPY_NOTES,
    ARCHIVE,
    DELETE;

    private static SavingOperation[] operations;

    static {
        operations = values();
    }
}