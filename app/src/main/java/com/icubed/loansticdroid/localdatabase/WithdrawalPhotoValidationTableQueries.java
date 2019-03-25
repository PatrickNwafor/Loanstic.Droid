package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class WithdrawalPhotoValidationTableQueries {
    private WithdrawalPhotoValidationTableDao withdrawalPhotoValidationTableDao;

    public WithdrawalPhotoValidationTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        withdrawalPhotoValidationTableDao = daoSession.getWithdrawalPhotoValidationTableDao();
    }

    /***************Save PaymentQueries to local Storage*********/
    public void insertWithdrawalPhotoVerifToStorage(WithdrawalPhotoValidationTable withdrawalPhotoValidationTable){
        withdrawalPhotoValidationTableDao.insert(withdrawalPhotoValidationTable);
    }

    /************Load all collections from local Storage********/
    public List<WithdrawalPhotoValidationTable> loadAllWithdrawalPhotoVerif(String withdrawalId){
        return withdrawalPhotoValidationTableDao.queryBuilder()
                .where(WithdrawalPhotoValidationTableDao.Properties.WithdrawalId.eq(withdrawalId))
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public WithdrawalPhotoValidationTable loadSingleWithdrawalPhotoVerif(String photoId){
        List<WithdrawalPhotoValidationTable> list = withdrawalPhotoValidationTableDao.queryBuilder().where(WithdrawalPhotoValidationTableDao.Properties.WithdawalPhotoValidationId.eq(photoId)).build().list();

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    public void updatePhotoVerif(WithdrawalPhotoValidationTable withdrawalPhotoValidationTable){
        withdrawalPhotoValidationTableDao.update(withdrawalPhotoValidationTable);
    }

    public void deletePhotoVerif(WithdrawalPhotoValidationTable withdrawalPhotoValidationTable){
        withdrawalPhotoValidationTableDao.delete(withdrawalPhotoValidationTable);
    }
}
