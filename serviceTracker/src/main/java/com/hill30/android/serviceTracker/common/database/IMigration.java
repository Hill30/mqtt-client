package com.hill30.android.serviceTracker.common.database;

import android.database.sqlite.SQLiteDatabase;

public interface IMigration {
    void migrate(SQLiteDatabase db);
}
