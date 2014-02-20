package com.hill30.android.serviceTracker.common.database;

import android.content.Context;

import com.hill30.android.serviceTracker.common.ApplicationGlobals;


public class DBConnectionPool   {
    public static final int DB_VER1 = 1;

    private DBOpenHelper connectionHolder;
    private static DBConnectionPool connectionPool;

    public DBConnectionPool(Context context, ApplicationGlobals globals) {
        connectionHolder = new DBOpenHelper(context, globals);
        connectionPool = this;
    }

    private DBWritableConnection getWritableConnectionImpl() {
        return new DBWritableConnection(connectionHolder.getWritableDatabase());
    }

    private DBReadableConnection getReadableConnectionImpl() {
        return new DBReadableConnection(connectionHolder.getReadableDatabase());
    }

    /**
     * Provides a synchronous database connection both for read and write operations.
     * All database operations requested through this connection will
     * be executed on the thread which requested the operation. Only synchronous connections allow transaction
     * management operations as well as operations returning values - i.e. insertAndGetId. If you need to run such
     * operations in background, you will have to setup a background thread yourself, i.e. through AsynchTask
     *
     * @return a writeable database connection object.
     *
     **/
    public static DBWritableConnection getWritableConnection() {
        return connectionPool.getWritableConnectionImpl();
    }

    /**
     * Provides a synchronous database connection for read operations only.
     * All database operations requested through this connection will
     * be executed on the thread which requested the operation.
     *
     * @return a read only database connection object.
     *
     **/
    public static DBReadableConnection getReadableConnection() {
        return connectionPool.getReadableConnectionImpl();
    }

    public static int latestDbVersion() {
        return DB_VER1;
    }
}
