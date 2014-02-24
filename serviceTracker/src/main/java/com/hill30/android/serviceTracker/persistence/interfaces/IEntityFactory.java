package com.hill30.android.serviceTracker.persistence.interfaces;


import com.hill30.android.serviceTracker.common.database.IAbstractFactory;
import com.hill30.android.serviceTracker.entities.EntityBase;

import java.util.List;

public interface IEntityFactory<T extends EntityBase> extends IAbstractFactory {

    List<T> getAllEntities();

    T getEntity(int id);

    void saveOrUpdate(T entity);
}
