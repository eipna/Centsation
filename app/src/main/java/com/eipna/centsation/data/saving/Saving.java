package com.eipna.centsation.data.saving;


import java.util.Comparator;

public class Saving {

    private int ID;
    private String name;
    private double value;
    private double goal;
    private String notes;
    private int isArchived;

    public static int IS_ARCHIVE = 1;
    public static int NOT_ARCHIVE = 0;

    public Saving() {
        this.ID = -1;
        this.name = null;
        this.value = 0.0;
        this.goal = 0.0;
        this.notes = null;
        this.isArchived = 0;
    }

    public static final Comparator<Saving> SORT_NAME_ASCENDING = Comparator.comparing(firstSaving -> firstSaving.getName().toLowerCase());

    public static final Comparator<Saving> SORT_NAME_DESCENDING = (firstSaving, secondSaving) -> secondSaving.getName().toLowerCase().compareTo(firstSaving.getName().toLowerCase());

    public static final Comparator<Saving> SORT_VALUE_LOWEST = Comparator.comparingDouble(Saving::getValue);

    public static final Comparator<Saving> SORT_VALUE_HIGHEST = (firstSaving, secondSaving) -> Double.compare(secondSaving.getValue(), firstSaving.getValue());

    public static final Comparator<Saving> SORT_GOAL_LOWEST = Comparator.comparingDouble(Saving::getGoal);

    public static final Comparator<Saving> SORT_GOAL_HIGHEST = (firstSaving, secondSaving) -> Double.compare(secondSaving.getGoal(), firstSaving.getGoal());

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public int getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }
}