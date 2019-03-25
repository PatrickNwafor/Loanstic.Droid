package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class SavingsTableScheduleQueries {
    private SavingsScheduleTableDao savingsTableDao;

    public SavingsTableScheduleQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsTableDao = daoSession.getSavingsScheduleTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void createSavingsSchedule(SavingsScheduleTable savingsTable){
        savingsTableDao.insert(savingsTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<SavingsScheduleTable> loadAllSavingsSchedule(){
        return savingsTableDao.loadAll();
    }

    public List<SavingsScheduleTable> loadAllSavingsScheduleOrderByCreationDate(){
        return savingsTableDao.queryBuilder()
                .orderDesc(SavingsScheduleTableDao.Properties.SavingsCreationDate)
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsScheduleTable loadSingleSavingSchedule(String savingsScheduleId){
        List<SavingsScheduleTable> list = savingsTableDao.queryBuilder().where(SavingsScheduleTableDao.Properties.SavingsScheduleId.eq(savingsScheduleId)).build().list();

        if(list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

    public void updateSavingsScheduleDetails(SavingsScheduleTable savingsScheduleTable){
        savingsTableDao.update(savingsScheduleTable);
    }

    public void deleteSavingsSchedule(SavingsScheduleTable savingsScheduleTable){
        savingsTableDao.delete(savingsScheduleTable);
    }
}
