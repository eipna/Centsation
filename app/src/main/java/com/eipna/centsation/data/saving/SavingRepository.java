package com.eipna.centsation.data.saving;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.eipna.centsation.data.Database;

import java.util.ArrayList;

public class SavingRepository extends Database {

    public SavingRepository(@Nullable Context context) {
        super(context);
    }

    public void create(Saving createdSaving) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        int isArchiveValue = createdSaving.isArchived() ? 1 : 0;

        values.put(COLUMN_SAVING_NAME, createdSaving.getName());
        values.put(COLUMN_SAVING_CURRENT_AMOUNT, createdSaving.getCurrentAmount());
        values.put(COLUMN_SAVING_GOAL, createdSaving.getGoal());
        values.put(COLUMN_SAVING_IS_ARCHIVED, isArchiveValue);

        database.insert(TABLE_SAVING, null, values);
        database.close();
    }

    public void update(Saving updatedSaving) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        int isArchiveValue = updatedSaving.isArchived() ? 1 : 0;

        values.put(COLUMN_SAVING_NAME, updatedSaving.getName());
        values.put(COLUMN_SAVING_CURRENT_AMOUNT, updatedSaving.getCurrentAmount());
        values.put(COLUMN_SAVING_GOAL, updatedSaving.getGoal());
        values.put(COLUMN_SAVING_IS_ARCHIVED, isArchiveValue);

        database.update(TABLE_SAVING, values, COLUMN_SAVING_ID + " = ?", new String[]{String.valueOf(updatedSaving.getID())});
        database.close();
    }

    public void delete(int savingID) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_SAVING, COLUMN_SAVING_ID + " = ?", new String[]{String.valueOf(savingID)});
        database.close();
    }

    @SuppressLint("Range")
    public ArrayList<Saving> getSavings(boolean isArchive) {
        ArrayList<Saving> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        int isArchiveValue = isArchive ? 1 : 0;
        String query = "SELECT * FROM " + TABLE_SAVING + " WHERE " + COLUMN_SAVING_IS_ARCHIVED + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(isArchiveValue)});

        if (cursor.moveToFirst()) {
            do {
                Saving queriedSaving = new Saving();
                queriedSaving.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_SAVING_ID)));
                queriedSaving.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SAVING_NAME)));
                queriedSaving.setCurrentAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_SAVING_CURRENT_AMOUNT)));
                queriedSaving.setGoal(cursor.getDouble(cursor.getColumnIndex(COLUMN_SAVING_GOAL)));

                int queriedArchiveValue = cursor.getInt(cursor.getColumnIndex(COLUMN_SAVING_CURRENT_AMOUNT));
                queriedSaving.setArchived(queriedArchiveValue == 1);
                list.add(queriedSaving);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }
}