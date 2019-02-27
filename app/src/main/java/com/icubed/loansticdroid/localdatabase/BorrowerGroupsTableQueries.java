package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class BorrowerGroupsTableQueries {
    private BorrowerGroupsTableDao borrowerGroupsTableDao;

    public BorrowerGroupsTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        borrowerGroupsTableDao = daoSession.getBorrowerGroupsTableDao();
    }

    /***************Save DueCollection to local Storage*********/
    public void insertGroupMemberToStorage(BorrowerGroupsTable borrowerGroupsTable){
        borrowerGroupsTableDao.insert(borrowerGroupsTable);
    }

    /**********Load a single collection from local Storage*******/
    public List<BorrowerGroupsTable> loadBorrowerGroups(String borrowerId){
        return borrowerGroupsTableDao.queryBuilder()
                .where(BorrowerGroupsTableDao.Properties.BorrowerId.eq(borrowerId))
                .build()
                .list();
    }

    /***************Delete Group from local Storage*********/
    public void deleteGroupFromStorage(BorrowerGroupsTable borrowerGroupsTable){
        borrowerGroupsTableDao.delete(borrowerGroupsTable);
    }

    public void updateGroupDetails(BorrowerGroupsTable borrowerGroupsTable){
        borrowerGroupsTableDao.update(borrowerGroupsTable);
    }
}
