package com.eipna.centsation.data;

public enum Contrast {
    LOW("contrast_low", "Low"),
    MEDIUM("contrast_medium", "Medium"),
    HIGH("contrast_high", "High");

    private static final Contrast[] contrasts;
    public final String VALUE;
    public final String NAME;

    static {
        contrasts = values();
    }

    Contrast(String value, String name) {
        this.VALUE = value;
        this.NAME = name;
    }

    public static String[] toNameArray() {
        String[] strings = new String[contrasts.length];
        for (int i = 0; i < contrasts.length; i++) {
            strings[i] = contrasts[i].NAME;
        }
        return strings;
    }

    public static String[] toValueArray() {
        String[] strings = new String[contrasts.length];
        for (int i = 0; i < contrasts.length; i++) {
            strings[i] = contrasts[i].VALUE;
        }
        return strings;
    }

    public static String getName(String value) {
        for (Contrast contrast : contrasts) {
            if (contrast.VALUE.equals(value)) {
                return contrast.NAME;
            }
        }
        return null;
    }
}