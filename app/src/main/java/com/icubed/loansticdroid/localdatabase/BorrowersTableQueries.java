package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class BorrowersTableQueries {

    private BorrowersTableDao borrowersTableDao;

    public BorrowersTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        borrowersTableDao = daoSession.getBorrowersTableDao();
    }

    /***************Save BorrowersQueries to local Storage*********/
    public void insertBorrowersToStorage(BorrowersTable borrowersTable){
        borrowersTableDao.insert(borrowersTable);
    }

    /************Load all collections from local Storage********/
    public List<BorrowersTable> loadAllBorrowersOrderByLastName(){
        return borrowersTableDao.queryBuilder()
                .orderAsc(BorrowersTableDao.Properties.LastName)
                .build()
                .list();
    }

    /**********Load a single borrower from local Storage*******/
    public BorrowersTable loadSingleBorrower(String borrowersId){
        return borrowersTableDao.queryBuilder()
                .where(BorrowersTableDao.Properties.BorrowersId.eq(borrowersId))
                .build()
                .list()
                .get(0);
    }

    public List<BorrowersTable> loadAllBorrowers() {
        return borrowersTableDao.loadAll();
    }
}
