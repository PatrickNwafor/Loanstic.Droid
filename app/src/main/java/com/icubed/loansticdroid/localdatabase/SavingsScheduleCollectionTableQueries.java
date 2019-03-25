package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import com.icubed.loansticdroid.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SavingsScheduleCollectionTableQueries {
    private SavingsScheduleCollectionTableDao savingsScheduleTable;

    public SavingsScheduleCollectionTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsScheduleTable = daoSession.getSavingsScheduleCollectionTableDao();
    }

    /***************Save DueCollection to local Storage*********/
    public void insertCollectionToStorage(SavingsScheduleCollectionTable savingsCollectionTable){
        savingsScheduleTable.insert(savingsCollectionTable);
    }

    /************Load all collections from local Storage********/
    public List<SavingsScheduleCollectionTable> loadAllCollections(){
        return savingsScheduleTable.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsScheduleCollectionTable loadSingleCollection(String collectionId){
        List<SavingsScheduleCollectionTable> list = savingsScheduleTable.queryBuilder().where(SavingsScheduleCollectionTableDao.Properties.SavingsScheduleCollectionId.eq(collectionId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    /**********Load All Due Collections from local Storage***********/
    public List<SavingsScheduleCollectionTable> loadAllDueCollections(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsScheduleCollectionTable> list = savingsScheduleTable.queryBuilder().build().list();

        List<SavingsScheduleCollectionTable> dueCollectionList = new ArrayList<>();

        for (SavingsScheduleCollectionTable savingsCollectionTable : list) {
            if(DateUtil.dateString(savingsCollectionTable.getSavingsCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                    !savingsCollectionTable.getIsSavingsCollected()){
                dueCollectionList.add(savingsCollectionTable);
            }
        }

        return dueCollectionList;

    }

    /**********Load All Due Collections from local Storage***********/
    public List<SavingsScheduleCollectionTable> loadCollectionsForSavingsSchedule(String savingsScheduleId){
        return savingsScheduleTable.queryBuilder()
                .where(SavingsScheduleCollectionTableDao.Properties.SavingsScheduleId.eq(savingsScheduleId))
                .orderAsc(com.icubed.loansticdroid.localdatabase.SavingsScheduleCollectionTableDao.Properties.SavingsCollectionNumber)
                .build()
                .list();
    }

    public List<SavingsScheduleCollectionTable> loadAllOverDueCollection(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsScheduleCollectionTable> list = savingsScheduleTable.queryBuilder().build().list();

        List<SavingsScheduleCollectionTable> overDueCollectionList = new ArrayList<>();

        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        for (SavingsScheduleCollectionTable savingsCollectionTable : list) {
            if(savingsCollectionTable.getSavingsCollectionDueDate().before(midnight.getTime()) &&
                    !savingsCollectionTable.getIsSavingsCollected()){
                overDueCollectionList.add(savingsCollectionTable);
            }
        }

        return overDueCollectionList;
    }

    /********Update Table When Due DueCollection is Confirmed*********/
    public void updateCollection(SavingsScheduleCollectionTable savingsCollectionTable){
        savingsScheduleTable.update(savingsCollectionTable);
    }
}
