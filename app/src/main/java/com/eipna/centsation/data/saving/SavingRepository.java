package com.eipna.centsation.data.saving;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.eipna.centsation.data.Database;
import com.eipna.centsation.data.transaction.Transaction;
import com.eipna.centsation.data.transaction.TransactionRepository;
import com.eipna.centsation.data.transaction.TransactionType;

import java.util.ArrayList;

public class SavingRepository extends Database {

    private final TransactionRepository transactionRepository;

    public SavingRepository(@Nullable Context context) {
        super(context);
        transactionRepository = new TransactionRepository(context);
    }

    public void create(Saving createdSaving) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SAVING_ID, createdSaving.getID());
        values.put(COLUMN_SAVING_NAME, createdSaving.getName());
        values.put(COLUMN_SAVING_CURRENT_SAVING, createdSaving.getCurrentSaving());
        values.put(COLUMN_SAVING_GOAL, createdSaving.getGoal());
        values.put(COLUMN_SAVING_NOTES, createdSaving.getNotes());
        values.put(COLUMN_SAVING_IS_ARCHIVED, createdSaving.getIsArchived());
        values.put(COLUMN_SAVING_DEADLINE, createdSaving.getDeadline());
        database.insert(TABLE_SAVING, null, values);

        Transaction initialTransaction = new Transaction();
        initialTransaction.setSavingID(createdSaving.getID());
        initialTransaction.setAmount(createdSaving.getCurrentSaving());
        initialTransaction.setType(TransactionType.CREATED.VALUE);
        transactionRepository.create(initialTransaction);
        database.close();
    }

    public void update(Saving updatedSaving) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SAVING_NAME, updatedSaving.getName());
        values.put(COLUMN_SAVING_CURRENT_SAVING, updatedSaving.getCurrentSaving());
        values.put(COLUMN_SAVING_GOAL, updatedSaving.getGoal());
        values.put(COLUMN_SAVING_NOTES, updatedSaving.getNotes());
        values.put(COLUMN_SAVING_IS_ARCHIVED, updatedSaving.getIsArchived());
        values.put(COLUMN_SAVING_DEADLINE, updatedSaving.getDeadline());

        database.update(TABLE_SAVING, values, COLUMN_SAVING_ID + " = ?", new String[]{updatedSaving.getID()});
        database.close();
    }

    public void delete(String savingID) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_SAVING, COLUMN_SAVING_ID + " = ?", new String[]{savingID});
        database.close();
    }

    public void makeTransaction(Saving updatedSaving, double amount, TransactionType type) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SAVING_CURRENT_SAVING, updatedSaving.getCurrentSaving());
        database.update(TABLE_SAVING, values, COLUMN_SAVING_ID + " = ?", new String[]{updatedSaving.getID()});

        Transaction transaction = new Transaction();
        transaction.setSavingID(updatedSaving.getID());
        transaction.setAmount(Math.abs(amount));
        transaction.setType(type.VALUE);
        transaction.setDate(System.currentTimeMillis());
        transactionRepository.create(transaction);
        database.close();
    }

    @SuppressLint("Range")
    public ArrayList<Saving> getSavings(int isArchive) {
        ArrayList<Saving> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SAVING + " WHERE " + COLUMN_SAVING_IS_ARCHIVED + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(isArchive)});

        if (cursor.moveToFirst()) {
            do {
                Saving queriedSaving = new Saving();
                queriedSaving.setID(cursor.getString(cursor.getColumnIndex(COLUMN_SAVING_ID)));
                queriedSaving.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SAVING_NAME)));
                queriedSaving.setCurrentSaving(cursor.getDouble(cursor.getColumnIndex(COLUMN_SAVING_CURRENT_SAVING)));
                queriedSaving.setGoal(cursor.getDouble(cursor.getColumnIndex(COLUMN_SAVING_GOAL)));
                queriedSaving.setNotes(cursor.getString(cursor.getColumnIndex(COLUMN_SAVING_NOTES)));
                queriedSaving.setIsArchived(cursor.getInt(cursor.getColumnIndex(COLUMN_SAVING_IS_ARCHIVED)));
                queriedSaving.setDeadline(cursor.getLong(cursor.getColumnIndex(COLUMN_SAVING_DEADLINE)));
                list.add(queriedSaving);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public ArrayList<Saving> getAll() {
        ArrayList<Saving> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_SAVING, null);

        if (cursor.moveToFirst()) {
            do {
                Saving queriedSaving = new Saving();
                queriedSaving.setID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVING_ID)));
                queriedSaving.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVING_NAME)));
                queriedSaving.setCurrentSaving(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SAVING_CURRENT_SAVING)));
                queriedSaving.setGoal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SAVING_GOAL)));
                queriedSaving.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVING_NOTES)));
                queriedSaving.setIsArchived(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SAVING_IS_ARCHIVED)));
                queriedSaving.setDeadline(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SAVING_DEADLINE)));
                list.add(queriedSaving);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }
}