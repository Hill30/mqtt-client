package com.hill30.android.serviceTracker.common.database;

import android.database.Cursor;

public interface CursorReader {
    String Table();
    String[] Columns();
    boolean processRow(Cursor cursor);
    int getIndex(String columnName);
}
