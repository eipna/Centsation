package com.eipna.centsation.data;

public enum DateFormat {
    EEEE_MMMM_DD_YYYY("Tuesday, April 8 2025", "EEEE, MMMM dd yyyy"),
    MM_DD_YYYY("04/08/2025", "MM/dd/yyyy"),
    DD_MM_YYYY("08/04/2025", "dd/MM/yyyy"),
    YYYY_DD_MM("2025/08/04", "yyyy/dd/MM"),
    YYYY_MM_DD("2025/04/08", "yyyy/MM/dd");

    private static final DateFormat[] dateFormats;

    static {
        dateFormats = values();
    }

    public final String NAME;
    public final String PATTERN;

    DateFormat(String NAME, String PATTERN) {
        this.NAME = NAME;
        this.PATTERN = PATTERN;
    }

    public static String getNameByPattern(String pattern) {
        for (DateFormat dateFormat : dateFormats) {
            if (dateFormat.PATTERN.equals(pattern)) {
                return dateFormat.NAME;
            }
        }
        return DateFormat.MM_DD_YYYY.NAME;
    }

    public static String[] getNames() {
        String[] strings = new String[values().length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = dateFormats[i].NAME;
        }
        return strings;
    }

    public static String[] getPatterns() {
        String[] strings = new String[values().length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = dateFormats[i].PATTERN;
        }
        return strings;
    }
}