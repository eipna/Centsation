package com.eipna.centsation.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "centsation.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SAVING = "savings";
    public static final String COLUMN_SAVING_ID = "saving_id";
    public static final String COLUMN_SAVING_NAME = "name";
    public static final String COLUMN_SAVING_VALUE = "value";
    public static final String COLUMN_SAVING_GOAL = "goal";
    public static final String COLUMN_SAVING_NOTES = "notes";
    public static final String COLUMN_SAVING_IS_ARCHIVED = "is_archived";

    public static final String TABLE_HISTORY = "histories";
    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_HISTORY_SAVING_ID = "saving_id";
    public static final String COLUMN_HISTORY_UPDATED_VALUE = "updated_value";
    public static final String COLUMN_HISTORY_TYPE = "type";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

        String createHistoryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + "(" +
                COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HISTORY_SAVING_ID + "INTEGER NOT NULL, " +
                COLUMN_HISTORY_UPDATED_VALUE + " REAL NOT NULL, " +
                COLUMN_HISTORY_TYPE + " TEXT NOT NULL," +
                "FOREIGN KEY (" + COLUMN_HISTORY_SAVING_ID + ") REFERENCES " + TABLE_SAVING + "(" + COLUMN_SAVING_ID + ") ON DELETE CASCADE)";

        sqLiteDatabase.execSQL(createSavingTable);
        sqLiteDatabase.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVING);
        onCreate(sqLiteDatabase);
    }
}