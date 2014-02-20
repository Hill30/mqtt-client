package com.hill30.android.serviceTracker.common.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class DBWritableConnection extends DBReadableConnection {

    public DBWritableConnection(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    protected SQLiteDatabase getDatabaseFromHolder(DBOpenHelper connectionHolder) {
        return connectionHolder.getReadableDatabase();
    }

    public void beginTransaction() {
        getDatabase().beginTransaction();
    }

    public void commitTransaction() {
        getDatabase().setTransactionSuccessful();
    }

    public void endTransaction() {
        getDatabase().endTransaction();
    }

    public void insert(final String table, final ContentValues values) {
        execute( new Runnable() {
            @Override
            public void run() {
                getDatabase().insert(table, null, values);
            }
        });
    }

    public long insertAndGetId(final String table, final ContentValues values) {
        return getDatabase().insert(table, null, values);
    }

    public void update(String table, ContentValues values) {
        update(table, values, null);
    }

    public void update(String table, ContentValues values,
                       String whereClause) {
        update(table, values, whereClause, new String[] {});
    }

    public void update(String table, ContentValues values, String whereClause,
                       String whereArg) {
        update(table, values, whereClause, new String[] {whereArg});
    }

    public void update(final String table, final ContentValues values,
                       final String whereClause, final String[] whereArgs) {
        execute( new Runnable() {
            @Override
            public void run() {
                getDatabase().update(table, values, whereClause, whereArgs);
            }
        });
    }

    public void delete(String table, String whereClause, String whereArg) {
        delete(table, whereClause, new String[] {whereArg});
    }

    public void delete(final String table, final String whereClause, final String[] whereArgs) {
        execute( new Runnable() {
            @Override
            public void run() {
                getDatabase().delete(table, whereClause, whereArgs);
            }
        });
    }

}
