package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class AccountTableQueries {
    
    private AccountTableDao accountTableDao;
    
    public AccountTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        accountTableDao = daoSession.getAccountTableDao();
    }

    /***************Save AccountQueries to local Storage*********/
    public void insertAccountToStorage(AccountTable accountTable){
        accountTableDao.insert(accountTable);
    }

    /***************Save AccountQueries to local Storage*********/
    public void deleteAccountFromStorage(AccountTable accountTable){
        accountTableDao.delete(accountTable);
    }

    /************Load all collections from local Storage********/
    public List<AccountTable> loadAllAccountOrderByLastName(){
        return accountTableDao.queryBuilder()
                .orderAsc(AccountTableDao.Properties.LastName)
                .build()
                .list();
    }

    /**********Load a single borrower from local Storage*******/
    public AccountTable loadSingleAccount(String accountId){
        return accountTableDao.queryBuilder()
                .where(AccountTableDao.Properties.AccountId.eq(accountId))
                .build()
                .list()
                .get(0);
    }

    public List<AccountTable> loadAllAccounts() {
        return accountTableDao.loadAll();
    }

    /**********search for accounts based on first name, last name************/
    public List<AccountTable> searchAccount(String searchString){
        List<AccountTable> list = accountTableDao.loadAll();
        List<AccountTable> accountTables = new ArrayList<>();

        for(AccountTable bt : list) {

            Boolean lastNameCheck = bt.getLastName().toLowerCase().contains(searchString.toLowerCase());
            Boolean firstNameCheck = bt.getFirstName().toLowerCase().contains(searchString.toLowerCase());

            if (lastNameCheck || firstNameCheck) {
                accountTables.add(bt);
            }
        }

        return accountTables;
    }
    
}
