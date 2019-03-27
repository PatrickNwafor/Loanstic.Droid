package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import com.icubed.loansticdroid.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SavingsPlanCollectionTableQueries {
    private SavingsScheduleCollectionTableDao savingsScheduleTable;

    public SavingsPlanCollectionTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsScheduleTable = daoSession.getSavingsScheduleCollectionTableDao();
    }

    /***************Save DueCollection to local Storage*********/
    public void insertCollectionToStorage(SavingsPlanCollectionTable savingsScheduleCollectionTable){
        savingsScheduleTable.insert(savingsScheduleCollectionTable);
    }

    /************Load all collections from local Storage********/
    public List<SavingsPlanCollectionTable> loadAllCollections(){
        return savingsScheduleTable.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsPlanCollectionTable loadSingleCollection(String collectionId){
        List<SavingsPlanCollectionTable> list = savingsScheduleTable.queryBuilder().where(SavingsScheduleCollectionTableDao.Properties.SavingsScheduleCollectionId.eq(collectionId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    /**********Load All Due Collections from local Storage***********/
    public List<SavingsPlanCollectionTable> loadAllDueCollections(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsPlanCollectionTable> list = savingsScheduleTable.queryBuilder().build().list();

        List<SavingsPlanCollectionTable> dueCollectionList = new ArrayList<>();

        for (SavingsPlanCollectionTable savingsCollectionTable : list) {
            if(DateUtil.dateString(savingsCollectionTable.getSavingsCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                    !savingsCollectionTable.getIsSavingsCollected()){
                dueCollectionList.add(savingsCollectionTable);
            }
        }

        return dueCollectionList;

    }

    /**********Load All Due Collections from local Storage***********/
    public List<SavingsPlanCollectionTable> loadCollectionsForSavingsSchedule(String savingsScheduleId){
        return savingsScheduleTable.queryBuilder()
                .where(SavingsScheduleCollectionTableDao.Properties.SavingsScheduleId.eq(savingsScheduleId))
                .orderAsc(com.icubed.loansticdroid.localdatabase.SavingsScheduleCollectionTableDao.Properties.SavingsCollectionNumber)
                .build()
                .list();
    }

    public List<SavingsPlanCollectionTable> loadAllOverDueCollection(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsPlanCollectionTable> list = savingsScheduleTable.queryBuilder().build().list();

        List<SavingsPlanCollectionTable> overDueCollectionList = new ArrayList<>();

        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        for (SavingsPlanCollectionTable savingsCollectionTable : list) {
            if(savingsCollectionTable.getSavingsCollectionDueDate().before(midnight.getTime()) &&
                    !savingsCollectionTable.getIsSavingsCollected()){
                overDueCollectionList.add(savingsCollectionTable);
            }
        }

        return overDueCollectionList;
    }

    /********Update Table When Due DueCollection is Confirmed*********/
    public void updateCollection(SavingsPlanCollectionTable savingsScheduleCollectionTable){
        savingsScheduleTable.update(savingsScheduleCollectionTable);
    }
}
