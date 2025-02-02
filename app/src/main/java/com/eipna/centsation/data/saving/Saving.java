package com.eipna.centsation.data.saving;


public class Saving {

    private int ID;
    private String name;
    private double currentAmount;
    private double goal;
    private String notes;
    private boolean isArchived;

    public Saving() {
        this.ID = -1;
        this.name = null;
        this.currentAmount = 0.0;
        this.goal = 0.0;
        this.notes = null;
        this.isArchived = false;
    }

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

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}