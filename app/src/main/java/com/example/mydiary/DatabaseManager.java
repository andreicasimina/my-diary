package com.example.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Locale;

public class DatabaseManager {
    private final Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseManager (Context ctx) { context = ctx; }

    public void open() throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    public void insert (int year, int month, int day, String entryContent) {
        ContentValues contentValues = new ContentValues();
        String id = String.format(Locale.JAPAN, "%d%02d%02d", year, month, day);

        contentValues.put(DatabaseHelper.ENTRY_ID, id);
        contentValues.put(DatabaseHelper.ENTRY_YEAR, year);
        contentValues.put(DatabaseHelper.ENTRY_MONTH, month);
        contentValues.put(DatabaseHelper.ENTRY_DAY, day);
        contentValues.put(DatabaseHelper.ENTRY_CONTENT, entryContent);

        sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    public void update (int year, int month, int day, String entryContent) {
        ContentValues contentValues = new ContentValues();
        String id = String.format(Locale.JAPAN, "%d%02d%02d", year, month, day);

        contentValues.put(DatabaseHelper.ENTRY_ID, id);
        contentValues.put(DatabaseHelper.ENTRY_YEAR, year);
        contentValues.put(DatabaseHelper.ENTRY_MONTH, month);
        contentValues.put(DatabaseHelper.ENTRY_DAY, day);
        contentValues.put(DatabaseHelper.ENTRY_CONTENT, entryContent);

        sqLiteDatabase.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.ENTRY_ID + "=" + id, null);
    }

    public void delete (int year, int month, int day) {
        String id = String.format(Locale.JAPAN, "%d%02d%02d", year, month, day);
        sqLiteDatabase.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ENTRY_ID + "=" + id, null);
    }

    public Cursor fetch (int year, int month, int day) {
        String[] columns = new String[] {
                DatabaseHelper.ENTRY_ID,
                DatabaseHelper.ENTRY_YEAR,
                DatabaseHelper.ENTRY_MONTH,
                DatabaseHelper.ENTRY_DAY,
                DatabaseHelper.ENTRY_CONTENT
        };

        if (day != 0) {
            String id = String.format(Locale.JAPAN, "%d%02d%02d", year, month, day);

            Cursor cursor = sqLiteDatabase.query(
                    DatabaseHelper.TABLE_NAME,
                    columns,
                    DatabaseHelper.ENTRY_ID + "=?",
                    new String[]{id},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    String printId = cursor.getString(0);
                    String printContent = cursor.getString(4);
                    Log.i("DATABASE_TAG", "ID: " + printId + "\nContent: " + printContent);
                } while (cursor.moveToNext());
            } else {
                Log.i("DATABASE_TAG", "No Data Found!");
            }

            return cursor;
        } else if (month != 0) {

            Cursor cursor = sqLiteDatabase.query(
                    DatabaseHelper.TABLE_NAME,
                    columns,
                    DatabaseHelper.ENTRY_YEAR + "=?" + " AND " + DatabaseHelper.ENTRY_MONTH + "=?",
                    new String[]{String.valueOf(year), String.valueOf(month)},
                    null,
                    null,
                    DatabaseHelper.ENTRY_DAY
            );

            if (cursor.moveToFirst()) {
                do {
                    String printId = cursor.getString(0);
                    String printContent = cursor.getString(4);
                    Log.i("DATABASE_TAG", "ID: " + printId + "\nContent: " + printContent);
                } while (cursor.moveToNext());
            } else {
                Log.i("DATABASE_TAG", "No Data Found!");
            }

            return cursor;
        } else {
            Cursor cursor = sqLiteDatabase.query(
                    DatabaseHelper.TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.ENTRY_DAY
            );

            if (cursor.moveToFirst()) {
                do {
                    String printId = cursor.getString(0);
                    String printContent = cursor.getString(4);
                    Log.i("DATABASE_TAG", "ID: " + printId + "\nContent: " + printContent);
                } while (cursor.moveToNext());
            } else {
                Log.i("DATABASE_TAG", "No Data Found!");
            }

            return cursor;
        }
    }
}
