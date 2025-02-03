package com.eipna.centsation.data;

public enum Theme {
    LIGHT("Light", "light_mode"),
    DARK("Dark", "dark_mode"),
    BATTERY("Battery Saving", "battery_mode"),
    SYSTEM("System Default", "system_mode");

    public static final Theme[] themes;
    public final String NAME;
    public final String VALUE;

    static {
        themes = values();
    }

    Theme(String name, String value) {
        this.NAME = name;
        this.VALUE = value;
    }

    public static String[] getNames() {
        String[] array = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            array[i] = values()[i].NAME;
        }
        return array;
    }

    public static String[] getValues() {
        String[] array = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            array[i] = values()[i].VALUE;
        }
        return array;
    }
}