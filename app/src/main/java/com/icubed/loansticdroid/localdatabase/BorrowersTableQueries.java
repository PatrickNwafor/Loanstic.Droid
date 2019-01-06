package com.icubed.loansticdroid.localdatabase;

import android.app.Application;
import android.util.Log;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
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

    /***************Save BorrowersQueries to local Storage*********/
    public void deleteBorrowersFromStorage(BorrowersTable borrowersTable){
        borrowersTableDao.delete(borrowersTable);
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

    /**********search for borrowers based on first name, last name or business name************/
    public List<BorrowersTable> searchBorrowers(String searchString){
        List<BorrowersTable> list = borrowersTableDao.loadAll();
        List<BorrowersTable> borrowersTables = new ArrayList<>();

        for(BorrowersTable bt : list) {

            Boolean lastNameCheck = bt.getLastName().toLowerCase().contains(searchString.toLowerCase());
            Boolean firstNameCheck = bt.getFirstName().toLowerCase().contains(searchString.toLowerCase());
            Boolean businessNameCheck = bt.getBusinessName().toLowerCase().contains(searchString.toLowerCase());

            if (lastNameCheck || firstNameCheck || businessNameCheck) {
                borrowersTables.add(bt);
            }
        }

        return borrowersTables;
    }
}
