package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class SavingsTableQueries {
    private SavingsTableDao savingsTableDao;

    public SavingsTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        savingsTableDao = daoSession.getSavingsTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void insertSavingsToStorage(SavingsTable savingsTable){
        savingsTableDao.insert(savingsTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<SavingsTable> loadAllSavings(){
        return savingsTableDao.loadAll();
    }

    public List<SavingsTable> loadAllSavingsOrderByCreationDate(){
        return savingsTableDao.queryBuilder()
                .orderDesc(SavingsTableDao.Properties.CreatedAt)
                .build().list();
    }

    public List<SavingsTable> loadSavingsForBorrowerOrderByCreationDate(String borrowerId){
        return savingsTableDao.queryBuilder()
                .where(SavingsTableDao.Properties.BorrowerId.eq(borrowerId))
                .orderDesc(SavingsTableDao.Properties.CreatedAt)
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsTable loadSingleSavings(String savingsId){
        List<SavingsTable> list = savingsTableDao.queryBuilder().where(SavingsTableDao.Properties.SavingsId.eq(savingsId)).build().list();

        if(list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

    public void updateSavingsDetails(SavingsTable savingsTable){
        savingsTableDao.update(savingsTable);
    }

    public void deleteSavings(SavingsTable savingsTable){
        savingsTableDao.delete(savingsTable);
    }
}
