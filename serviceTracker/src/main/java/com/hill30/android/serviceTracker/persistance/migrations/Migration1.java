package com.hill30.android.serviceTracker.persistance.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hill30.android.serviceTracker.common.database.IMigration;
import com.hill30.android.serviceTracker.persistance.factories.ActivityRecordMessageFactory;

/**
 * Created by azavarin on 2/20/14.
 */
public class Migration1 implements IMigration {
    @Override
    public void migrate(SQLiteDatabase db) {
        Log.d("Migration", "Running Migration1");

        db.execSQL("CREATE TABLE "+ ActivityRecordMessageFactory.TABLE +"(\n" +
                "    " + ActivityRecordMessageFactory.ID_COLUMN     + " INTEGER PRIMARY KEY \n" +
                ");");

        Log.d("Migration", "Migration1 - OK");
    }
}
