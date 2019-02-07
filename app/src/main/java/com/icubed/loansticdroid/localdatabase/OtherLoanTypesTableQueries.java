package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class OtherLoanTypesTableQueries {

    private OtherLoanTypesTableDao otherLoanTypesTableDao;

    public OtherLoanTypesTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        otherLoanTypesTableDao = daoSession.getOtherLoanTypesTableDao();
    }

    /***************Save LoansQueries to local Storage*********/
    public void insertLoanTypeToStorage(OtherLoanTypesTable otherLoanTypesTable){
        otherLoanTypesTableDao.insert(otherLoanTypesTable);
    }

    /************Load all LoansQueries from local Storage********/
    public List<OtherLoanTypesTable> loadAllLoanTpes(){
        return otherLoanTypesTableDao.loadAll();
    }

    /************Load all LoansQueries from local Storage********/
    public List<OtherLoanTypesTable> loadAllLoanTypesOrderByName(){
        return otherLoanTypesTableDao.queryBuilder()
                .orderDesc(OtherLoanTypesTableDao.Properties.OtherLoanTypeName)
                .build()
                .list();
    }

    /**********Load a single collection from local Storage*******/
    public OtherLoanTypesTable loadSingleLoanType(String loanTypeId){
        return otherLoanTypesTableDao.queryBuilder()
                .where(OtherLoanTypesTableDao.Properties.OtherLoanTypeId.eq(loanTypeId))
                .build()
                .list()
                .get(0);
    }

    /**********Load a single collection from local Storage*******/
    public OtherLoanTypesTable loadSingleLoanTypeUsingUri(String imageUri){
        return otherLoanTypesTableDao.queryBuilder()
                .where(OtherLoanTypesTableDao.Properties.OtherLoanTypeId.eq(imageUri))
                .build()
                .list()
                .get(0);
    }

    public void updateLoanTypeDetails(OtherLoanTypesTable otherLoanTypesTable){
        otherLoanTypesTableDao.update(otherLoanTypesTable);
    }

    public void deleteLoanType(OtherLoanTypesTable otherLoanTypesTable){
        otherLoanTypesTableDao.delete(otherLoanTypesTable);
    }
    
}
