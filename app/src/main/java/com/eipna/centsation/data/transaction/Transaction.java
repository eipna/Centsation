package com.eipna.centsation.data.transaction;

public class Transaction {

    private int ID;
    private int savingID;
    private double amount;
    private String type;
    private long date;

    public Transaction() {
        this.ID = -1;
        this.savingID = -1;
        this.amount = -1;
        this.type = null;
        this.date = -1;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSavingID() {
        return savingID;
    }

    public void setSavingID(int savingID) {
        this.savingID = savingID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}