package com.marvinsyan.csgoskinsviewer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Loads database only once and handles queries.
 * http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
 */
public class DatabaseHelper extends SQLiteAssetHelper {
    private static DatabaseHelper sInstance;
    private static final String DATABASE_NAME = "skins.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // setForcedUpgrade() does not seem to work without passing a version
        // Forces app to always use the latest database
        setForcedUpgrade(2);
        db = getReadableDatabase();
    }

    public Cursor executeQuery(String query) {
        return db.rawQuery(query, null);
    }
}
