package com.chrslee.csgopedia.app;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ItemsDatabase extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "itemsDB.db";
    private static final int DATABASE_VERSION = 1;

    public ItemsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(2); // force if version under 2
    }
}
