package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class SavingsPlanTypeTableQueries {

    private SavingsPlanTypeTableDao savingsPlanTypeTableDao;

    public SavingsPlanTypeTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsPlanTypeTableDao = daoSession.getSavingsPlanTypeTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void insertSavingsPlanTypeToStorage(SavingsPlanTypeTable savingsPlanTypeTable){
        savingsPlanTypeTableDao.insert(savingsPlanTypeTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<SavingsPlanTypeTable> loadAllSavingsPlanTypes(){
        return savingsPlanTypeTableDao.loadAll();
    }

    /************Load all LoansQueries from local Storage********/
    public List<SavingsPlanTypeTable> loadAllSavingsPlanTypesOrderByName(){
        return savingsPlanTypeTableDao.queryBuilder()
                .orderDesc(SavingsPlanTypeTableDao.Properties.SavingsTypeName)
                .build()
                .list();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsPlanTypeTable loadSingleSavingsPlanType(String loanTypeId){
        List<SavingsPlanTypeTable> list = savingsPlanTypeTableDao.queryBuilder().where(SavingsPlanTypeTableDao.Properties.SavingsPlanTypeId.eq(loanTypeId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    public void updateSavingsPlanTypeDetails(SavingsPlanTypeTable savingsPlanTypeTable){
        savingsPlanTypeTableDao.update(savingsPlanTypeTable);
    }

    public void deleteSavingsPlanType(SavingsPlanTypeTable savingsPlanTypeTable){
        savingsPlanTypeTableDao.delete(savingsPlanTypeTable);
    }
    
}
