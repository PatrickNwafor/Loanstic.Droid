package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class BorrowersTableQueries {

    private BorrowersTableDao borrowersTableDao;
    DaoSession daoSession;

    public BorrowersTableQueries(Application application){
        daoSession = ((App) application).getDaoSession();
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
        List<BorrowersTable> list = borrowersTableDao.queryBuilder().where(BorrowersTableDao.Properties.BorrowersId.eq(borrowersId)).build().list();

        if(list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

    public List<BorrowersTable> loadAllBorrowers() {
        return borrowersTableDao.loadAll();
    }

    public void updateBorrowerDetails(BorrowersTable borrowersTable){
        borrowersTableDao.update(borrowersTable);
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
