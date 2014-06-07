package com.chrslee.csgopedia.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Loads pre-populated database file from assets folder.
 * Allows activities to access the same database across the entire app.
 * http://stackoverflow.com/a/18148718/3505851
 */
public class ItemsDatabase extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "itemsDB.db";
    private static final int DATABASE_VERSION = 1;
    private static ItemsDatabase mInstance = null;

    public static ItemsDatabase getInstance(Context ctx) {
        // Load database from assets if not already loaded.
        if (mInstance == null) {
            mInstance = new ItemsDatabase(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private ItemsDatabase(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(2); // force if version under 2
    }
}
