package com.hill30.android.serviceTracker.common.database;

import android.database.sqlite.SQLiteDatabase;

abstract class DBConnection {

    private SQLiteDatabase database = null;

    DBConnection(SQLiteDatabase database) {
        this.database = database;
    }

    protected SQLiteDatabase getDatabase() {
        return database;
    }

    protected abstract SQLiteDatabase getDatabaseFromHolder(DBOpenHelper connectionHolder);

    protected void execute(Runnable runnable) {
        runnable.run();
    }

}
