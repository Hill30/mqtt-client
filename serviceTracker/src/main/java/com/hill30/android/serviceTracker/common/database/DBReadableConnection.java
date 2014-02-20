package com.hill30.android.serviceTracker.common.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DBReadableConnection extends DBConnection {

    public static String createColumnsList(String prefix, String[] columns) {
        StringBuilder sb = new StringBuilder();
        for (String item : columns)
            sb.append(prefix).append(item).append(", ");
        if (sb.length() >= 2)
            sb.replace(sb.length()-2, sb.length(), "");
        return sb.toString();
    }



    public DBReadableConnection(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    public SQLiteDatabase getDatabase() {
        return super.getDatabase();
    }

    @Override
    protected SQLiteDatabase getDatabaseFromHolder(DBOpenHelper connectionHolder) {
        return connectionHolder.getReadableDatabase();
    }

    public void orderedQuery(CursorReader reader, String where, String orderBy, String... selectionArgs ) {
        rawQuery(reader, SQLiteQueryBuilder.buildQueryString(false, reader.Table(), reader.Columns(), where, null, null, orderBy, null), selectionArgs);
    }

    public void query(CursorReader reader) {
        query(reader, null);
    }

    public void query(CursorReader reader, String where, String... selectionArgs ) {
        rawQuery(reader, SQLiteQueryBuilder.buildQueryString(false, reader.Table(), reader.Columns(), where, null, null, null, null), selectionArgs);
    }

    public void rawQuery(final CursorReader reader, final String sql, final String[] selectionArgs) {

        String args = "";

        // TODO: replace with StringBuilder
        if (selectionArgs != null)
            for (Object arg : selectionArgs) {
                if (!args.equals(""))
                    args += ", ";
                args += "\"" + arg.toString() + "\"";
            }

        execute( new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor = getDatabase().rawQuery(sql, selectionArgs);
                    try {
                        while (cursor.moveToNext()) {
                            if (!reader.processRow(cursor))
                                return;
                        }
                    } finally {
                        cursor.close();
                    }
                }
                catch (SQLException e) {
                    Log.e("DBReadableConnection", "Error getting data", e);
                    throw e;
                }
            }
        });
    }

}
