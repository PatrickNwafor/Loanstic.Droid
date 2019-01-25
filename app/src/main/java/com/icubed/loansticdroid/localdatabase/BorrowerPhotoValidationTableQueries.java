package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class BorrowerPhotoValidationTableQueries {

    private BorrowerPhotoValidationTableDao borrowerPhotoValidationTableDao;

    public BorrowerPhotoValidationTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        borrowerPhotoValidationTableDao = daoSession.getBorrowerPhotoValidationTableDao();
    }

    /***************Save Photos to local Storage*********/
    public void insertPhototsToStorage(BorrowerPhotoValidationTable borrowerPhotoValidationTable){
        borrowerPhotoValidationTableDao.insert(borrowerPhotoValidationTable);
    }

    /************Load all Photos from local Storage********/
    public List<BorrowerPhotoValidationTable> loadAllPhotos(){
        return borrowerPhotoValidationTableDao.loadAll();
    }

    public List<BorrowerPhotoValidationTable> loadAllPhotes(String activityId) {
        return  borrowerPhotoValidationTableDao.queryBuilder()
                .where(BorrowerPhotoValidationTableDao.Properties.ActivityCycleId.eq(activityId))
                .build()
                .list();
    }
}
