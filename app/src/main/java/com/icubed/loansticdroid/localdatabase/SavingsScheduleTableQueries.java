package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import com.icubed.loansticdroid.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SavingsScheduleTableQueries {
    private SavingsScheduleTableDao savingsScheduleTable;

    public SavingsScheduleTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsScheduleTable = daoSession.getSavingsScheduleTableDao();
    }

    /***************Save DueCollection to local Storage*********/
    public void insertCollectionToStorage(SavingsScheduleTable savingsCollectionTable){
        savingsScheduleTable.insert(savingsCollectionTable);
    }

    /************Load all collections from local Storage********/
    public List<SavingsScheduleTable> loadAllCollections(){
        return savingsScheduleTable.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsScheduleTable loadSingleCollection(String collectionId){
        List<SavingsScheduleTable> list = savingsScheduleTable.queryBuilder().where(com.icubed.loansticdroid.localdatabase.SavingsScheduleTableDao.Properties.SavingsId.eq(collectionId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    /**********Load All Due Collections from local Storage***********/
    public List<SavingsScheduleTable> loadAllDueCollections(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsScheduleTable> list = savingsScheduleTable.queryBuilder().build().list();

        List<SavingsScheduleTable> dueCollectionList = new ArrayList<>();

        for (SavingsScheduleTable savingsCollectionTable : list) {
            if(DateUtil.dateString(savingsCollectionTable.getSavingsCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                    !savingsCollectionTable.getIsSavingsCollected()){
                dueCollectionList.add(savingsCollectionTable);
            }
        }

        return dueCollectionList;

    }

    /**********Load All Due Collections from local Storage***********/
    public List<SavingsScheduleTable> loadCollectionsForLoan(String savingsId){
        return savingsScheduleTable.queryBuilder()
                .where(com.icubed.loansticdroid.localdatabase.SavingsScheduleTableDao.Properties.SavingsId.eq(savingsId))
                .orderAsc(com.icubed.loansticdroid.localdatabase.SavingsScheduleTableDao.Properties.SavingsCollectionNumber)
                .build()
                .list();
    }

    public List<SavingsScheduleTable> loadAllOverDueCollection(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsScheduleTable> list = savingsScheduleTable.queryBuilder().build().list();

        List<SavingsScheduleTable> overDueCollectionList = new ArrayList<>();

        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        for (SavingsScheduleTable savingsCollectionTable : list) {
            if(savingsCollectionTable.getSavingsCollectionDueDate().before(midnight.getTime()) &&
                    !savingsCollectionTable.getIsSavingsCollected()){
                overDueCollectionList.add(savingsCollectionTable);
            }
        }

        return overDueCollectionList;
    }

    /********Update Table When Due DueCollection is Confirmed*********/
    public void updateCollection(SavingsScheduleTable savingsCollectionTable){
        savingsScheduleTable.update(savingsCollectionTable);
    }
}
