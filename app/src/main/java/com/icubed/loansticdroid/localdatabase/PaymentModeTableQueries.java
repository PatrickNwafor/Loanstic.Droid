package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class PaymentModeTableQueries {
    private PaymentModeTableDao paymentModeTableDao;

    public PaymentModeTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        paymentModeTableDao = daoSession.getPaymentModeTableDao();
    }

    /***************Save PaymentQueries to local Storage*********/
    public void insertPaymentModeToStorage(PaymentModeTable paymentModeTable){
        paymentModeTableDao.insert(paymentModeTable);
    }

    /************Load all collections from local Storage********/
    public List<PaymentModeTable> loadAllPaymentModes(){
        return paymentModeTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public PaymentModeTable loadSinglePaymentMode(String paymentModeId){
        List<PaymentModeTable> list = paymentModeTableDao.queryBuilder().where(PaymentModeTableDao.Properties.PaymentModeId.eq(paymentModeId)).build().list();

        if(!list.isEmpty()){
            return paymentModeTableDao.queryBuilder().where(PaymentModeTableDao.Properties.PaymentModeId.eq(paymentModeId)).build().list().get(0);
        }

        return null;
    }

    public void deletePaymentMode(PaymentModeTable paymentModeTable){
        paymentModeTableDao.delete(paymentModeTable);
    }

    public void updatePaymentMode(PaymentModeTable paymentModeTable){
        paymentModeTableDao.update(paymentModeTable);
    }
}
