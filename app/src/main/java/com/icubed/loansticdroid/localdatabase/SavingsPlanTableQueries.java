package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class SavingsPlanTableQueries {
    private SavingsScheduleTableDao savingsTableDao;

    public SavingsPlanTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsTableDao = daoSession.getSavingsScheduleTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void createSavingsSchedule(SavingsPlanTable savingsTable){
        savingsTableDao.insert(savingsTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<SavingsPlanTable> loadAllSavingsSchedule(){
        return savingsTableDao.loadAll();
    }

    public List<SavingsPlanTable> loadAllSavingsScheduleOrderByCreationDate(){
        return savingsTableDao.queryBuilder()
                .orderDesc(SavingsScheduleTableDao.Properties.SavingsCreationDate)
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsPlanTable loadSingleSavingSchedule(String savingsScheduleId){
        List<SavingsPlanTable> list = savingsTableDao.queryBuilder().where(SavingsScheduleTableDao.Properties.SavingsScheduleId.eq(savingsScheduleId)).build().list();

        if(list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

    public void updateSavingsScheduleDetails(SavingsPlanTable savingsScheduleTable){
        savingsTableDao.update(savingsScheduleTable);
    }

    public void deleteSavingsSchedule(SavingsPlanTable savingsScheduleTable){
        savingsTableDao.delete(savingsScheduleTable);
    }
}
