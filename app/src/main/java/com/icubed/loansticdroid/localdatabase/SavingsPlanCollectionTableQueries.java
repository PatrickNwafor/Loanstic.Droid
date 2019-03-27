package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import com.icubed.loansticdroid.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SavingsPlanCollectionTableQueries {
    private SavingsPlanCollectionTableDao savingsPlanCollectionTableDao;

    public SavingsPlanCollectionTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsPlanCollectionTableDao = daoSession.getSavingsPlanCollectionTableDao();
    }

    /***************Save DueCollection to local Storage*********/
    public void insertCollectionToStorage(SavingsPlanCollectionTable savingsPlanCollectionTable){
        savingsPlanCollectionTableDao.insert(savingsPlanCollectionTable);
    }

    /************Load all collections from local Storage********/
    public List<SavingsPlanCollectionTable> loadAllCollections(){
        return savingsPlanCollectionTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsPlanCollectionTable loadSingleCollection(String collectionId){
        List<SavingsPlanCollectionTable> list = savingsPlanCollectionTableDao.queryBuilder().where(SavingsPlanCollectionTableDao.Properties.SavingsPlanCollectionId.eq(collectionId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    /**********Load All Due Collections from local Storage***********/
    public List<SavingsPlanCollectionTable> loadAllDueCollections(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsPlanCollectionTable> list = savingsPlanCollectionTableDao.queryBuilder().build().list();

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
        return savingsPlanCollectionTableDao.queryBuilder()
                .where(SavingsPlanCollectionTableDao.Properties.SavingsScheduleId.eq(savingsScheduleId))
                    .orderAsc(com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTableDao.Properties.SavingsCollectionNumber)
                .build()
                .list();
    }

    public List<SavingsPlanCollectionTable> loadAllOverDueCollection(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<SavingsPlanCollectionTable> list = savingsPlanCollectionTableDao.queryBuilder().build().list();

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
    public void updateCollection(SavingsPlanCollectionTable savingsPlanCollectionTable){
        savingsPlanCollectionTableDao.update(savingsPlanCollectionTable);
    }
}
