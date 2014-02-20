package com.hill30.android.serviceTracker.common.database;

public abstract class AbstractCursorReader implements CursorReader {

    @Override
    public int getIndex(String columnName) {
        return getIndex(Columns(), columnName);
    }

    public static int getIndex(String[] columns, String columnName) {
        int index = 0;
        for (String item : columns) {
            if (item.equals(columnName))
                return index;
            else
                index += 1;
        }
        return -1;
    }

}
