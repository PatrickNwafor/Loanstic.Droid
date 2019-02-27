package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class BorrowerFilesTableQueries {

    private BorrowerFilesTableDao borrowerFilesTableDao;
    DaoSession daoSession;

    public BorrowerFilesTableQueries(Application application){
        daoSession = ((App) application).getDaoSession();
        borrowerFilesTableDao = daoSession.getBorrowerFilesTableDao();
    }

    /***************Save BorrowersQueries to local Storage*********/
    public void insertBorrowersFileToStorage(BorrowerFilesTable borrowerFilesTable){
        borrowerFilesTableDao.insert(borrowerFilesTable);
    }

    /***************Save BorrowersQueries to local Storage*********/
    public void deleteBorrowersFileFromStorage(BorrowerFilesTable borrowerFilesTable){
        borrowerFilesTableDao.delete(borrowerFilesTable);
    }

    /**********Load a single borrower from local Storage*******/
    public BorrowerFilesTable loadSingleBorrowerFile(String fileId){
        List<BorrowerFilesTable> list = borrowerFilesTableDao.queryBuilder().where(BorrowerFilesTableDao.Properties.FilesId.eq(fileId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    public List<BorrowerFilesTable> loadAllBorrowersFile(String activityId) {
        return  borrowerFilesTableDao.queryBuilder()
                .where(BorrowerFilesTableDao.Properties.ActivityCycleId.eq(activityId))
                .build()
                .list();
    }

    public void updateBorrowerFileDetails(BorrowerFilesTable borrowerFilesTable){
        borrowerFilesTableDao.update(borrowerFilesTable);
    }
    
}
