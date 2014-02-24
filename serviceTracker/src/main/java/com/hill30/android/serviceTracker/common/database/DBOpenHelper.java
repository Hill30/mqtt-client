package com.hill30.android.serviceTracker.common.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.hill30.android.serviceTracker.R;
import com.hill30.android.serviceTracker.common.ApplicationGlobals;
import com.hill30.android.serviceTracker.persistence.migrations.Migration1;

import java.util.*;


public class DBOpenHelper extends SQLiteOpenHelper {

    private Map<Integer, IMigration> migrations = new HashMap<Integer, IMigration>();

    DBOpenHelper(Context context, ApplicationGlobals globals) {
        super(context, context.getResources().getString(R.string.DB_NAME),
                null, DBConnectionPool.DB_VER1);

        addMigration(1, new Migration1());

    }

    public void addMigration(int version, IMigration migration) {
        if (migrations.containsKey(version))
            throw new RuntimeException(String.format("Migration %s was added twice.", migration));

        migrations.put(version, migration);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.i("DBOpenHelper", "Creating DB");

            migrate(sqLiteDatabase, 0, DBConnectionPool.latestDbVersion());

            Log.i("DBOpenHelper", "DB created");
        } catch (SQLException e) {
            Log.e("DBOpenHelper", "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            Log.i("DBOpenHelper","Upgrade database");
            Log.i("DBOpenHelper","Old version:"+oldVersion+"; New version:"+newVersion);

            migrate(sqLiteDatabase, oldVersion, newVersion);

            Log.i("DBOpenHelper","DB upgraded");
        } catch (SQLException e) {
            Log.i("DBOpenHelper","DB: Failed to upgrade");
            Log.e("DBOpenHelper","Error upgrading database", e);
        }
    }


    private void migrate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // sort by versions
        List<Integer> migrationVersions = new ArrayList<Integer>(migrations.keySet());
        Collections.sort(migrationVersions);

        // apply version-by-version
        try {
            db.beginTransaction();

            for (int i = 0; i < migrationVersions.size(); i++) {
                Integer migrationVersion = migrationVersions.get(i);
                if (migrationVersion <= oldVersion || migrationVersion > newVersion)
                    continue;

                try {
                    migrations.get(migrationVersion).migrate(db);
                }   catch (RuntimeException e){
                    String errMsg = String.format("Cannot migrate to version %d. Error: \n %s \n", migrationVersion, e.getMessage());
                    Log.w("DATABASE", "MIGRATION FAILED", new Exception(errMsg, e));

                    throw e;

                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.w("DATABASE", "MIGRATION OK from " + oldVersion + " to " + newVersion);
    }
}
