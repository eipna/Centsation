package com.eipna.centsation.data.saving;


import java.util.Comparator;

public class Saving {

    private int ID;
    private String name;
    private double currentSaving;
    private double goal;
    private String notes;
    private int isArchived;

    public static int IS_ARCHIVE = 1;
    public static int NOT_ARCHIVE = 0;

    public Saving() {
        this.ID = -1;
        this.name = null;
        this.currentSaving = 0.0;
        this.goal = 0.0;
        this.notes = null;
        this.isArchived = 0;
    }

    public static final Comparator<Saving> SORT_NAME = Comparator.comparing(firstSaving -> firstSaving.getName().toLowerCase());

    public static final Comparator<Saving> SORT_VALUE = Comparator.comparingDouble(Saving::getCurrentSaving);

    public static final Comparator<Saving> SORT_GOAL = Comparator.comparingDouble(Saving::getGoal);

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

    public double getCurrentSaving() {
        return currentSaving;
    }

    public void setCurrentSaving(double currentSaving) {
        this.currentSaving = currentSaving;
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