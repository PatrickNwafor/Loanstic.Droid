package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class LoanTableQueries {

    private LoansTableDao loansTableDao;

    public LoanTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        loansTableDao = daoSession.getLoansTableDao();
    }

    /***************Save Loans to local Storage*********/
    public void insertLoanToStorage(LoansTable loansTable){
        loansTableDao.insert(loansTable);
    }

    /************Load all Loans from local Storage********/
    public List<LoansTable> loadAllLoans(){
        return loansTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public LoansTable loadSingleLoan(String loanId){
        return loansTableDao.queryBuilder()
                .where(LoansTableDao.Properties.LoanId.eq(loanId))
                .build()
                .list()
                .get(0);
    }

}
