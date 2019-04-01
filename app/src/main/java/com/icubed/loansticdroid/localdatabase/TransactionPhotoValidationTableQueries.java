package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class TransactionPhotoValidationTableQueries {
    private TransactionPhotoValidationTableDao transactionPhotoValidationTableDao;

    public TransactionPhotoValidationTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        transactionPhotoValidationTableDao = daoSession.getTransactionPhotoValidationTableDao();
    }

    /***************Save PaymentQueries to local Storage*********/
    public void insertTransactionPhotoVerifToStorage(TransactionPhotoValidationTable transactionPhotoValidationTable){
        transactionPhotoValidationTableDao.insert(transactionPhotoValidationTable);
    }

    /************Load all collections from local Storage********/
    public List<TransactionPhotoValidationTable> loadAllSingleTransactionPhotoVerif(String transactionId){
        return transactionPhotoValidationTableDao.queryBuilder()
                .where(TransactionPhotoValidationTableDao.Properties.TransactionId.eq(transactionId))
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public TransactionPhotoValidationTable loadSingleTransactionPhotoVerif(String photoId){
        List<TransactionPhotoValidationTable> list = transactionPhotoValidationTableDao.queryBuilder().where(TransactionPhotoValidationTableDao.Properties.TransactionPhotoValidationId.eq(photoId)).build().list();

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    public void updatePhotoVerif(TransactionPhotoValidationTable transactionPhotoValidationTable){
        transactionPhotoValidationTableDao.update(transactionPhotoValidationTable);
    }

    public void deletePhotoVerif(TransactionPhotoValidationTable transactionPhotoValidationTable){
        transactionPhotoValidationTableDao.delete(transactionPhotoValidationTable);
    }
}
