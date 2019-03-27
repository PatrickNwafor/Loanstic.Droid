package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class SavingsPlanTableQueries {
    private SavingsPlanTableDao savingsPlanTableDao;

    public SavingsPlanTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsPlanTableDao = daoSession.getSavingsPlanTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void createSavingsSchedule(SavingsPlanTable savingsTable){
        savingsPlanTableDao.insert(savingsTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<SavingsPlanTable> loadAllSavingsSchedule(){
        return savingsPlanTableDao.loadAll();
    }

    public List<SavingsPlanTable> loadAllSavingsScheduleOrderByCreationDate(){
        return savingsPlanTableDao.queryBuilder()
                .orderDesc(SavingsPlanTableDao.Properties.SavingsCreationDate)
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsPlanTable loadSingleSavingSchedule(String savingsPlanId){
        List<SavingsPlanTable> list = savingsPlanTableDao.queryBuilder().where(SavingsPlanTableDao.Properties.SavingsPlanId.eq(savingsPlanId)).build().list();

        if(list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

    public void updateSavingsScheduleDetails(SavingsPlanTable savingsPlanTable){
        savingsPlanTableDao.update(savingsPlanTable);
    }

    public void deleteSavingsSchedule(SavingsPlanTable savingsPlanTable){
        savingsPlanTableDao.delete(savingsPlanTable);
    }
}
