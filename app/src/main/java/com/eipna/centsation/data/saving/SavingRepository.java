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

        values.put(COLUMN_SAVING_NAME, createdSaving.getName());
        values.put(COLUMN_SAVING_VALUE, createdSaving.getValue());
        values.put(COLUMN_SAVING_GOAL, createdSaving.getGoal());
        values.put(COLUMN_SAVING_NOTES, createdSaving.getNotes());
        values.put(COLUMN_SAVING_IS_ARCHIVED, createdSaving.getIsArchived());

        Transaction initialTransaction = new Transaction();
        initialTransaction.setSavingID((int) database.insert(TABLE_SAVING, null, values));
        initialTransaction.setAmount(createdSaving.getValue());
        initialTransaction.setType(TransactionType.DEPOSIT.VALUE);
        transactionRepository.create(initialTransaction);
        database.close();
    }

    public void update(Saving updatedSaving) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SAVING_NAME, updatedSaving.getName());
        values.put(COLUMN_SAVING_VALUE, updatedSaving.getValue());
        values.put(COLUMN_SAVING_GOAL, updatedSaving.getGoal());
        values.put(COLUMN_SAVING_NOTES, updatedSaving.getNotes());
        values.put(COLUMN_SAVING_IS_ARCHIVED, updatedSaving.getIsArchived());

        database.update(TABLE_SAVING, values, COLUMN_SAVING_ID + " = ?", new String[]{String.valueOf(updatedSaving.getID())});
        database.close();
    }

    public void delete(int savingID) {
        SQLiteDatabase database = getWritableDatabase();
        transactionRepository.clear(savingID);
        database.delete(TABLE_SAVING, COLUMN_SAVING_ID + " = ?", new String[]{String.valueOf(savingID)});
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
                queriedSaving.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_SAVING_ID)));
                queriedSaving.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SAVING_NAME)));
                queriedSaving.setValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_SAVING_VALUE)));
                queriedSaving.setGoal(cursor.getDouble(cursor.getColumnIndex(COLUMN_SAVING_GOAL)));
                queriedSaving.setNotes(cursor.getString(cursor.getColumnIndex(COLUMN_SAVING_NOTES)));
                queriedSaving.setIsArchived(cursor.getInt(cursor.getColumnIndex(COLUMN_SAVING_IS_ARCHIVED)));
                list.add(queriedSaving);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }
}