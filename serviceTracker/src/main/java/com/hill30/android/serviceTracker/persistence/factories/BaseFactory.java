package com.hill30.android.serviceTracker.persistence.factories;

import android.content.ContentValues;
import android.database.Cursor;

import com.hill30.android.serviceTracker.common.database.AbstractCursorReader;
import com.hill30.android.serviceTracker.common.database.AbstractFactory;
import com.hill30.android.serviceTracker.common.database.DBConnectionPool;
import com.hill30.android.serviceTracker.entities.EntityBase;
import com.hill30.android.serviceTracker.persistence.interfaces.IEntityFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFactory<T extends EntityBase> extends AbstractFactory implements IEntityFactory<T> {

    public BaseFactory() {
    }

    @Override
    public List<T> getAllEntities() {
        List<T> result = new ArrayList<T>();
        BaseReader reader = new BaseReader(result);

        DBConnectionPool.getReadableConnection().query(reader, null);

        return result;
    }

    @Override
    public T getEntity(int id) {
        T item = createEmpty();
        BaseReader reader = new BaseReader(item);
        DBConnectionPool.getReadableConnection().query(reader, idColumnName() + "=?", id + "");

        if (reader.count == 0)
            return null;

        return item;
    }


    public abstract String idColumnName();
    protected abstract T createEmpty();
    protected abstract String table();
    protected abstract String[] columns();
    protected abstract ContentValues contentValues(T value);
    protected abstract T populate(T item, Cursor cursor, AbstractCursorReader reader);


    @Override
    public void saveOrUpdate(T entity) {

        ContentValues values = contentValues(entity);

        T existingEntity = getEntity(entity.getId());
        if (existingEntity == null) {
            DBConnectionPool.getWritableConnection().insert(table(), values);
        } else {
            DBConnectionPool.getWritableConnection().update(table(), values, idColumnName() + "=?", entity.getId() + "");
        }
    }

    protected Double getNullableDouble(Cursor cursor, int index) {
        if (cursor.isNull(index))
            return null;
        return cursor.getDouble(index);
    }

    protected Float getNullableFloat(Cursor cursor, int index) {
        if (cursor.isNull(index))
            return null;
        return cursor.getFloat(index);
    }

    protected Integer getNullableInteger(Cursor cursor, int index) {
        if (cursor.isNull(index))
            return null;
        return cursor.getInt(index);
    }

    protected class BaseReader extends AbstractCursorReader {

        @Override
        public String Table() {
            return BaseFactory.this.table();
        }

        @Override
        public String[] Columns() {
            return BaseFactory.this.columns();
        }

        private T item = null;
        private List<T> list = null;
        private int count = 0;

        public int count() { return count; }

        public BaseReader(T item) {
            this.item = item;
        }

        public BaseReader(List<T> list) {
            this.list = list;
        }

        public T populate(T item, Cursor cursor) {
            BaseFactory.this.populate(item, cursor, this);
            count++;
            return item;
        }

        @Override
        public boolean processRow(Cursor cursor) {
            if (item != null) {
                populate(item, cursor);
                return false;
            }
            list.add(populate(createEmpty(), cursor));
            return true;
        }
    }

}
