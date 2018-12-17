package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.Date;
import java.util.List;

public class CollectionTableQueries {

    private CollectionTableDao collectionTableDao;

    public CollectionTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        collectionTableDao = daoSession.getCollectionTableDao();
    }

    /***************Save Collection to local Storage*********/
    public void insertCollectionToStorage(CollectionTable collectionTable){
        collectionTableDao.insert(collectionTable);
    }

    /************Load all collections from local Storage********/
    public List<CollectionTable> loadAllCollections(){
        return collectionTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public CollectionTable loadSingleCollection(String collectionId){
        return collectionTableDao.queryBuilder()
                .where(CollectionTableDao.Properties.CollectionId.eq(collectionId))
                .build()
                .list()
                .get(0);
    }

    /********Update Table When Due Collection is Confirmed*********/
    public void updateStorageAfterCollection(CollectionTable collectionTable, Date timestamp){
        collectionTable.setIsDueCollected(true);
        collectionTable.setTimestamp(timestamp);
        collectionTableDao.update(collectionTable);
    }
}
