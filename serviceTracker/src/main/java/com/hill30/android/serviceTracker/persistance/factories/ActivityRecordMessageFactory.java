package com.hill30.android.serviceTracker.persistance.factories;

import android.content.ContentValues;
import android.database.Cursor;

import com.hill30.android.serviceTracker.common.database.AbstractCursorReader;
import com.hill30.android.serviceTracker.entities.ActivityRecordMessage;
import com.hill30.android.serviceTracker.persistance.interfaces.IActivityRecordMessageFactory;
import com.hill30.android.serviceTracker.persistance.interfaces.IEntityFactory;

/**
 * Created by azavarin on 2/20/14.
 */
public class ActivityRecordMessageFactory extends BaseFactory<ActivityRecordMessage> implements IActivityRecordMessageFactory {

    public static final String TABLE = "activity_record_messages";

    public static final String ID_COLUMN = "id";

    @Override
    public String idColumnName() {
        return ID_COLUMN;
    }

    @Override
    protected ActivityRecordMessage createEmpty() {
        return new ActivityRecordMessage();
    }

    @Override
    protected String table() {
        return TABLE;
    }

    @Override
    protected String[] columns() {
        return new String[]{
                ID_COLUMN
        };
    }

    @Override
    protected ContentValues contentValues(ActivityRecordMessage value) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, value.getId());
        return values;
    }

    @Override
    protected ActivityRecordMessage populate(ActivityRecordMessage item, Cursor cursor, AbstractCursorReader reader) {
        item.setId(cursor.getInt(reader.getIndex(ID_COLUMN)));
        return item;
    }
}
