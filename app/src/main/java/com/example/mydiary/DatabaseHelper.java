package com.example.mydiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "EntryDatabase.db";
    static final String TABLE_NAME = "ENTRY";
    static final String ENTRY_ID = "id";
    static final String ENTRY_YEAR = "year";
    static final String ENTRY_MONTH = "month";
    static final String ENTRY_DAY = "day";
    static final String ENTRY_CONTENT = "content";

    private static final String CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ENTRY_ID + " TEXT PRIMARY KEY," +
                    ENTRY_YEAR + " INTEGER," +
                    ENTRY_MONTH + " INTEGER," +
                    ENTRY_DAY + " INTEGER," +
                    ENTRY_CONTENT + " TEXT)";

    private static final String DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
