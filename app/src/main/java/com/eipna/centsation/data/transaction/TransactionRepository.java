package com.eipna.centsation.data.transaction;

import android.annotation.SuppressLint;
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
        database.insert(TABLE_TRANSACTIONS, null, values);
        database.close();
    }

    @SuppressLint("Range")
    public ArrayList<Transaction> getTransactions(int savingId) {
        ArrayList<Transaction> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_TRANSACTION_SAVING_ID + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(savingId)});

        if (cursor.moveToFirst()) {
            do {
                Transaction queriedTransaction = new Transaction();
                queriedTransaction.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID)));
                queriedTransaction.setSavingID(cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_SAVING_ID)));
                queriedTransaction.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_TRANSACTION_AMOUNT)));
                queriedTransaction.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE)));
                list.add(queriedTransaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }

    public void clear(int savingID) {
        SQLiteDatabase database = getReadableDatabase();
        database.delete(TABLE_TRANSACTIONS, COLUMN_TRANSACTION_SAVING_ID + " = ?", new String[]{String.valueOf(savingID)});
        database.close();
    }
}