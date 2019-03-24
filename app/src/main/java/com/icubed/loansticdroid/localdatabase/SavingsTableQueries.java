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
    public void insertLoanToStorage(SavingsTable savingsTable){
        savingsTableDao.insert(savingsTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<SavingsTable> loadAllLoans(){
        return savingsTableDao.loadAll();
    }

    public List<SavingsTable> loadAllLoansOrderByCreationDate(){
        return savingsTableDao.queryBuilder()
                .orderDesc(SavingsTableDao.Properties.SavingsCreationDate)
                .build().list();
    }

    public List<SavingsTable> loadLoansForBorrowerOrderByCreationDate(String borrowerId){
        return savingsTableDao.queryBuilder()
                .where(SavingsTableDao.Properties.BorrowerId.eq(borrowerId))
                .orderDesc(SavingsTableDao.Properties.SavingsCreationDate)
                .build().list();
    }

    public List<SavingsTable> loadLoansForGroupOrderByCreationDate(String groupId){
        return savingsTableDao.queryBuilder()
                .where(SavingsTableDao.Properties.GroupId.eq(groupId))
                .orderDesc(SavingsTableDao.Properties.SavingsCreationDate)
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public SavingsTable loadSingleLoan(String loanId){
        List<SavingsTable> list = savingsTableDao.queryBuilder().where(SavingsTableDao.Properties.SavingsId.eq(loanId)).build().list();

        if(list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

    public void updateLoanDetails(SavingsTable savingsTable){
        savingsTableDao.update(savingsTable);
    }

    public void deleteLoan(SavingsTable savingsTable){
        savingsTableDao.delete(savingsTable);
    }
}
