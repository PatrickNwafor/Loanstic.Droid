package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class GroupPhotoValidationTableQueries {

        private GroupPhotoValidationTableDao groupPhotoValidationTableDao;
    DaoSession daoSession;

    public GroupPhotoValidationTableQueries(Application application){
        daoSession = ((App) application).getDaoSession();
        groupPhotoValidationTableDao = daoSession.getGroupPhotoValidationTableDao();
    }

    /***************Save BorrowersQueries to local Storage*********/
    public void insertBorrowersToStorage(GroupPhotoValidationTable groupPhotoValidationTable){
        groupPhotoValidationTableDao.insert(groupPhotoValidationTable);
    }

    /***************Save BorrowersQueries to local Storage*********/
    public void deleteBorrowersFromStorage(GroupPhotoValidationTable groupPhotoValidationTable){
        groupPhotoValidationTableDao.delete(groupPhotoValidationTable);
    }

    /**********Load a single borrower from local Storage*******/
    public List<GroupPhotoValidationTable> loadPhotosForGroup(String groupId){
        return groupPhotoValidationTableDao.queryBuilder()
                .where(GroupPhotoValidationTableDao.Properties.GroupId.eq(groupId))
                .build()
                .list();
    }

    public List<GroupPhotoValidationTable> loadAllBorrowers() {
        return groupPhotoValidationTableDao.loadAll();
    }

    public GroupPhotoValidationTable loadSingleGroupPhoto(String groupPhotoId) {
        return groupPhotoValidationTableDao.queryBuilder()
                .where(GroupPhotoValidationTableDao.Properties.GroupPhotoValidationId.eq(groupPhotoId))
                .build()
                .list()
                .get(0);
    }

    public void updateBorrowerDetails(GroupPhotoValidationTable borrowersTable){
        groupPhotoValidationTableDao.update(borrowersTable);
    }
    
}
