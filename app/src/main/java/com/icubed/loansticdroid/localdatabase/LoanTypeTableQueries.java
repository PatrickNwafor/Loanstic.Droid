package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class LoanTypeTableQueries {
    
    private LoanTypeTableDao loanTypeTableDao;

    public LoanTypeTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        loanTypeTableDao = daoSession.getLoanTypeTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void insertLoanTypeToStorage(LoanTypeTable loanTypeTable){
        loanTypeTableDao.insert(loanTypeTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<LoanTypeTable> loadAllLoanTpes(){
        return loanTypeTableDao.loadAll();
    }

    /************Load all LoansQueries from local Storage********/
    public List<LoanTypeTable> loadAllLoanTypesOrderByName(){
        return loanTypeTableDao.queryBuilder()
                .orderDesc(LoanTypeTableDao.Properties.LoanTypeName)
                .build()
                .list();
    }

    /**********Load a single collection from local Storage*******/
    public LoanTypeTable loadSingleLoanType(String loanTypeId){
        return loanTypeTableDao.queryBuilder()
                .where(LoanTypeTableDao.Properties.LoanTypeId.eq(loanTypeId))
                .build()
                .list()
                .get(0);
    }

    /**********Load a single collection from local Storage*******/
    public LoanTypeTable loadSingleLoanTypeUsingUri(String imageUri){
        return loanTypeTableDao.queryBuilder()
                .where(LoanTypeTableDao.Properties.LoanTypeImageUri.eq(imageUri))
                .build()
                .list()
                .get(0);
    }

    public void updateLoanTypeDetails(LoanTypeTable loanTypeTable){
        loanTypeTableDao.update(loanTypeTable);
    }

    public void deleteLoanType(LoanTypeTable loanTypeTable){
        loanTypeTableDao.delete(loanTypeTable);
    }
    
}
