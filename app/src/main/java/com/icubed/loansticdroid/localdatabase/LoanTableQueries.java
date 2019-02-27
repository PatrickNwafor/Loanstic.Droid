package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class LoanTableQueries {

    private LoansTableDao loansTableDao;

    public LoanTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        loansTableDao = daoSession.getLoansTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void insertLoanToStorage(LoansTable loansTable){
        loansTableDao.insert(loansTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<LoansTable> loadAllLoans(){
        return loansTableDao.loadAll();
    }

    public List<LoansTable> loadAllLoansOrderByCreationDate(){
        return loansTableDao.queryBuilder()
                .orderDesc(LoansTableDao.Properties.LoanCreationDate)
                .build().list();
    }

    public List<LoansTable> loadLoansForBorrowerOrderByCreationDate(String borrowerId){
        return loansTableDao.queryBuilder()
                .where(LoansTableDao.Properties.BorrowerId.eq(borrowerId))
                .orderDesc(LoansTableDao.Properties.LoanCreationDate)
                .build().list();
    }

    public List<LoansTable> loadLoansForGroupOrderByCreationDate(String groupId){
        return loansTableDao.queryBuilder()
                .where(LoansTableDao.Properties.GroupId.eq(groupId))
                .orderDesc(LoansTableDao.Properties.LoanCreationDate)
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public LoansTable loadSingleLoan(String loanId){
        List<LoansTable> list = loansTableDao.queryBuilder().where(LoansTableDao.Properties.LoanId.eq(loanId)).build().list();

        if(list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

    public void updateLoanDetails(LoansTable loansTable){
        loansTableDao.update(loansTable);
    }

    public void deleteLoan(LoansTable loansTable){
        loansTableDao.delete(loansTable);
    }

}
