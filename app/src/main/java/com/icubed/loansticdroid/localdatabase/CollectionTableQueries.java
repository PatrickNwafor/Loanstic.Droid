package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import com.icubed.loansticdroid.util.DateUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollectionTableQueries {

    private CollectionTableDao collectionTableDao;

    public CollectionTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        collectionTableDao = daoSession.getCollectionTableDao();
    }

    /***************Save DueCollection to local Storage*********/
    public void insertCollectionToStorage(CollectionTable collectionTable){
        collectionTableDao.insert(collectionTable);
    }

    /************Load all collections from local Storage********/
    public List<CollectionTable> loadAllCollections(){
        return collectionTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public CollectionTable loadSingleCollection(String collectionId){
        List<CollectionTable> list = collectionTableDao.queryBuilder().where(CollectionTableDao.Properties.CollectionId.eq(collectionId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    /**********Load All Due Collections from local Storage***********/
    public List<CollectionTable> loadAllDueCollections(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<CollectionTable> list = collectionTableDao.queryBuilder().build().list();

        List<CollectionTable> dueCollectionList = new ArrayList<>();

        for (CollectionTable collectionTable : list) {
            if(DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                    !collectionTable.getIsDueCollected()){
                dueCollectionList.add(collectionTable);
            }
        }

        return dueCollectionList;

    }

    /**********Load All Due Collections from local Storage***********/
    public List<CollectionTable> loadCollectionsForLoan(String loanId){
        return collectionTableDao.queryBuilder()
                .where(CollectionTableDao.Properties.LoanId.eq(loanId))
                .orderAsc(CollectionTableDao.Properties.CollectionNumber)
                .build()
                .list();
    }

    public List<CollectionTable> loadAllOverDueCollection(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<CollectionTable> list = collectionTableDao.queryBuilder().build().list();

        List<CollectionTable> overDueCollectionList = new ArrayList<>();

        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        for (CollectionTable collectionTable : list) {
            if(collectionTable.getCollectionDueDate().before(midnight.getTime()) &&
                    !collectionTable.getIsDueCollected()){
                overDueCollectionList.add(collectionTable);
            }
        }

        return overDueCollectionList;
    }

    /********Update Table When Due DueCollection is Confirmed*********/
    public void updateCollection(CollectionTable collectionTable){
        collectionTableDao.update(collectionTable);
    }
}
