package com.hill30.android.serviceTracker.common.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Hashtable;

/**
 * <h1>ObjectFactory interface.</h1>
 * <br/>
 * Defines methods to be implemented by every object factory
 *<br/>
 * @see com.hill30.android.serviceTracker.common.database.IAbstractFactory#DDL() DDL method
 * @see com.hill30.android.serviceTracker.common.database.IAbstractFactory#Upgraders() Upgraders method
 * @see com.hill30.android.serviceTracker.common.database.IAbstractFactory.Upgrader Upgrader interface
 */
public interface IAbstractFactory {

    /**
     * Provides logic to be executed to bring the database support of the factory up to the specific version
     * from the previous one.
     * @see com.hill30.android.serviceTracker.common.database.IAbstractFactory IAbstractFactory interface
     **/
    public interface Upgrader {
        void upgrade(SQLiteDatabase db);
    }

    /**
     * This method is supposed to be overridden in the specific factory to provide upgraders.
     * An upgrader is a class implementing Upgrader interface and supplying logic to be executed
     * to bring the database support of the factory up to the specific version from the previous one.
     * Upgraders are returned in a hashtable and the version number a specific upgrader is servicing
     * is indicated by the key used to place the entry for the upgrader into the hashtable.
     *
     * @return a hashtable of upgraders.
     *
     * @see com.hill30.android.serviceTracker.common.database.IAbstractFactory.Upgrader Upgrader interface
     **/
    Hashtable<Integer, Upgrader> Upgraders();

    void upgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
