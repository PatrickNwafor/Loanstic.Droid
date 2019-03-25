package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class DepositPhotoValidationTableQueries {
    private DepositPhotoValidationTableDao depositPhotoValidationTableDao;

    public DepositPhotoValidationTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        depositPhotoValidationTableDao = daoSession.getDepositPhotoValidationTableDao();
    }

    /***************Save PaymentQueries to local Storage*********/
    public void insertDepositPhotoVerifToStorage(DepositPhotoValidationTable paymentPhotoVerificationTable){
        depositPhotoValidationTableDao.insert(paymentPhotoVerificationTable);
    }

    /************Load all collections from local Storage********/
    public List<DepositPhotoValidationTable> loadAllDepositPhotoVerif(String depositId){
        return depositPhotoValidationTableDao.queryBuilder()
                .where(DepositPhotoValidationTableDao.Properties.DepositId.eq(depositId))
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public DepositPhotoValidationTable loadSingleDepositPhotoVerif(String photoId){
        List<DepositPhotoValidationTable> list = depositPhotoValidationTableDao.queryBuilder().where(DepositPhotoValidationTableDao.Properties.DepositPhotoValidationId.eq(photoId)).build().list();

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    public void updatePhotoVerif(DepositPhotoValidationTable paymentPhotoVerificationTable){
        depositPhotoValidationTableDao.update(paymentPhotoVerificationTable);
    }

    public void deletePhotoVerif(DepositPhotoValidationTable paymentPhotoVerificationTable){
        depositPhotoValidationTableDao.delete(paymentPhotoVerificationTable);
    }
}
