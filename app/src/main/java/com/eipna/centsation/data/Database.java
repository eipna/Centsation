package com.eipna.centsation.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Database extends SQLiteOpenHelper {

    private final Context context;

    private static final String DATABASE_NAME = "centsation.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SAVING = "savings";
    public static final String COLUMN_SAVING_ID = "saving_id";
    public static final String COLUMN_SAVING_NAME = "name";
    public static final String COLUMN_SAVING_VALUE = "value";
    public static final String COLUMN_SAVING_GOAL = "goal";
    public static final String COLUMN_SAVING_NOTES = "notes";
    public static final String COLUMN_SAVING_IS_ARCHIVED = "is_archived";

    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_TRANSACTION_SAVING_ID = "saving_id";
    public static final String COLUMN_TRANSACTION_AMOUNT = "amount";
    public static final String COLUMN_TRANSACTION_TYPE = "type";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createSavingTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SAVING + "(" +
                COLUMN_SAVING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SAVING_NAME + " TEXT NOT NULL, " +
                COLUMN_SAVING_VALUE + " REAL NOT NULL, " +
                COLUMN_SAVING_GOAL + " REAL NOT NULL, " +
                COLUMN_SAVING_NOTES + " TEXT, " +
                COLUMN_SAVING_IS_ARCHIVED + " INTEGER NOT NULL)";

        String createHistoryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + "(" +
                COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRANSACTION_SAVING_ID + " INTEGER NOT NULL, " +
                COLUMN_TRANSACTION_AMOUNT + " REAL NOT NULL, " +
                COLUMN_TRANSACTION_TYPE + " TEXT NOT NULL," +
                "FOREIGN KEY (" + COLUMN_TRANSACTION_SAVING_ID + ") REFERENCES " + TABLE_SAVING + "(" + COLUMN_SAVING_ID + "))";

        sqLiteDatabase.execSQL(createSavingTable);
        sqLiteDatabase.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVING);
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public void exportJSON(Uri uri) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor savingCursor = database.rawQuery("SELECT * FROM " + TABLE_SAVING, null);
        Cursor transactionCursor = database.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);

        JSONArray savingArray = new JSONArray();
        JSONArray transactionArray = new JSONArray();

        try {
            if (savingCursor.moveToFirst()) {
                do {
                    JSONObject savingObject = new JSONObject();
                    savingObject.put(COLUMN_SAVING_ID, savingCursor.getInt(savingCursor.getColumnIndex(COLUMN_SAVING_ID)));
                    savingObject.put(COLUMN_SAVING_NAME, savingCursor.getString(savingCursor.getColumnIndex(COLUMN_SAVING_NAME)));
                    savingObject.put(COLUMN_SAVING_VALUE, savingCursor.getDouble(savingCursor.getColumnIndex(COLUMN_SAVING_VALUE)));
                    savingObject.put(COLUMN_SAVING_GOAL, savingCursor.getDouble(savingCursor.getColumnIndex(COLUMN_SAVING_GOAL)));
                    savingObject.put(COLUMN_SAVING_NOTES, savingCursor.getString(savingCursor.getColumnIndex(COLUMN_SAVING_NOTES)));
                    savingObject.put(COLUMN_SAVING_IS_ARCHIVED, savingArray.getInt(savingCursor.getColumnIndex(COLUMN_SAVING_IS_ARCHIVED)));
                    savingArray.put(savingObject);
                } while (savingCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Export", "Something went wrong when collecting savings");
        }

        try {
            if (transactionCursor.moveToFirst()) {
                do {
                    JSONObject transactionObject = new JSONObject();
                    transactionObject.put(COLUMN_TRANSACTION_ID, transactionCursor.getInt(transactionCursor.getColumnIndex(COLUMN_TRANSACTION_ID)));
                    transactionObject.put(COLUMN_TRANSACTION_SAVING_ID, transactionCursor.getInt(transactionCursor.getColumnIndex(COLUMN_TRANSACTION_SAVING_ID)));
                    transactionObject.put(COLUMN_TRANSACTION_AMOUNT, transactionCursor.getDouble(transactionCursor.getColumnIndex(COLUMN_TRANSACTION_AMOUNT)));
                    transactionObject.put(COLUMN_TRANSACTION_TYPE, transactionCursor.getString(transactionCursor.getColumnIndex(COLUMN_TRANSACTION_TYPE)));
                    transactionArray.put(transactionObject);
                } while (transactionCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Export", "Something went wrong when collecting transactions");
        }

        try {
            JSONObject exportData = new JSONObject();
            exportData.put(TABLE_SAVING, savingArray);
            exportData.put(TABLE_TRANSACTIONS, transactionArray);

            assert context != null;
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            assert outputStream != null;
            outputStream.write(exportData.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("Export", "Something went wrong when exporting data");
        } finally {
            savingCursor.close();
            transactionCursor.close();
            database.close();
        }
    }

    public void importJSON(Uri uri) {
        SQLiteDatabase database = getWritableDatabase();
        StringBuilder jsonBuilder = new StringBuilder();

        try {
            assert context != null;
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            bufferedReader.close();

            JSONObject importData = new JSONObject(jsonBuilder.toString());
            JSONArray savingArray = importData.getJSONArray(TABLE_SAVING);
            JSONArray transactionArray = importData.getJSONArray(TABLE_TRANSACTIONS);

            for (int i = 0; i < savingArray.length(); i++) {
                JSONObject savingObject = savingArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(COLUMN_SAVING_ID, savingObject.getInt(COLUMN_SAVING_ID));
                values.put(COLUMN_SAVING_NAME, savingObject.getString(COLUMN_SAVING_NAME));
                values.put(COLUMN_SAVING_VALUE, savingObject.getDouble(COLUMN_SAVING_VALUE));
                values.put(COLUMN_SAVING_GOAL, savingObject.getDouble(COLUMN_SAVING_GOAL));
                values.put(COLUMN_SAVING_NOTES, savingObject.getString(COLUMN_SAVING_NOTES));
                values.put(COLUMN_SAVING_IS_ARCHIVED, savingObject.getInt(COLUMN_SAVING_IS_ARCHIVED));
                database.insert(TABLE_SAVING, null, values);
            }

            for (int i = 0; i < transactionArray.length(); i++) {
                JSONObject transactionObject = transactionArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(COLUMN_TRANSACTION_ID, transactionObject.getInt(COLUMN_TRANSACTION_ID));
                values.put(COLUMN_TRANSACTION_SAVING_ID, transactionObject.getInt(COLUMN_TRANSACTION_SAVING_ID));
                values.put(COLUMN_TRANSACTION_AMOUNT, transactionObject.getDouble(COLUMN_TRANSACTION_AMOUNT));
                values.put(COLUMN_TRANSACTION_TYPE, transactionObject.getString(COLUMN_TRANSACTION_TYPE));
                database.insert(TABLE_TRANSACTIONS, null, values);
            }
        } catch (Exception e) {
            Log.e("Import", "Something went wrong when importing");
        } finally {
            database.close();
        }
    }
}