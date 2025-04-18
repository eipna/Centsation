package com.eipna.centsation.data.transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.eipna.centsation.data.Database;

import java.util.ArrayList;

public class TransactionRepository extends Database {
    public TransactionRepository(@Nullable Context context) {
        super(context);
    }

    public void create(Transaction createdTransaction) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSACTION_SAVING_ID, createdTransaction.getSavingID());
        values.put(COLUMN_TRANSACTION_AMOUNT, createdTransaction.getAmount());
        values.put(COLUMN_TRANSACTION_TYPE, createdTransaction.getType());
        values.put(COLUMN_TRANSACTION_DATE, System.currentTimeMillis());
        database.insert(TABLE_TRANSACTION, null, values);
        database.close();
    }

    public ArrayList<Transaction> get(String savingID) {
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_SAVING_ID + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{savingID});

        if (cursor.moveToFirst()) {
            do {
                Transaction queriedTransaction = new Transaction();
                queriedTransaction.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID)));
                queriedTransaction.setSavingID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_SAVING_ID)));
                queriedTransaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_AMOUNT)));
                queriedTransaction.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE)));
                queriedTransaction.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE)));
                list.add(queriedTransaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public ArrayList<Transaction> getAll() {
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_TRANSACTION, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction queriedTransaction = new Transaction();
                queriedTransaction.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID)));
                queriedTransaction.setSavingID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_SAVING_ID)));
                queriedTransaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_AMOUNT)));
                queriedTransaction.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE)));
                queriedTransaction.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE)));
                list.add(queriedTransaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }
}