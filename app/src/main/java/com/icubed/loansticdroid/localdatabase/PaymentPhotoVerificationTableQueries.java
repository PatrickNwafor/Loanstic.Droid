package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class PaymentPhotoVerificationTableQueries {
    private PaymentPhotoVerificationTableDao paymentPhotoVerificationTableDao;

    public PaymentPhotoVerificationTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        paymentPhotoVerificationTableDao = daoSession.getPaymentPhotoVerificationTableDao();
    }

    /***************Save PaymentQueries to local Storage*********/
    public void insertPaymentPhotoVerifToStorage(PaymentPhotoVerificationTable paymentPhotoVerificationTable){
        paymentPhotoVerificationTableDao.insert(paymentPhotoVerificationTable);
    }

    /************Load all collections from local Storage********/
    public List<PaymentPhotoVerificationTable> loadAllPaymentPhotoVerif(String paymentId){
        return paymentPhotoVerificationTableDao.queryBuilder()
                .where(PaymentPhotoVerificationTableDao.Properties.PaymentId.eq(paymentId))
                .build().list();
    }

    /**********Load a single collection from local Storage*******/
    public PaymentPhotoVerificationTable loadSinglePaymentPhotoVerif(String photoId){
        List<PaymentPhotoVerificationTable> list = paymentPhotoVerificationTableDao.queryBuilder().where(PaymentPhotoVerificationTableDao.Properties.PaymentVerificationPhotoId.eq(photoId)).build().list();

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    public void updatePhotoVerif(PaymentPhotoVerificationTable paymentPhotoVerificationTable){
        paymentPhotoVerificationTableDao.update(paymentPhotoVerificationTable);
    }

    public void deletePhotoVerif(PaymentPhotoVerificationTable paymentPhotoVerificationTable){
        paymentPhotoVerificationTableDao.delete(paymentPhotoVerificationTable);
    }
}
