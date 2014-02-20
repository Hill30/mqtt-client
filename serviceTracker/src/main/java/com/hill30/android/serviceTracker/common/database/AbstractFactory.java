package com.hill30.android.serviceTracker.common.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Hashtable;

public abstract class AbstractFactory implements IAbstractFactory {

    public void upgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        Hashtable<Integer, Upgrader> upgraders = Upgraders();
        for (int i = oldVersion; i<currentVersion; i++)
            if (upgraders.containsKey(i + 1)) {
                IAbstractFactory.Upgrader upgrader = upgraders.get(i+1);
                try {
                    upgrader.upgrade(db);
                    Log.w("DB upgrader", "DB_VER_" + (i + 1) + " " + upgrader.getClass() + " success");
                } catch (RuntimeException e) {
                    Log.w("DB upgrader", "DB_VER_" + (i+1) + " " + upgrader.getClass() + " failed with exception", e);
                    throw e;
                }
            }
    }

    @Override
    public Hashtable<Integer, Upgrader> Upgraders() {
        return new Hashtable<Integer, Upgrader>();
    }
}
