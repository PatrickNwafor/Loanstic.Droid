package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class PaymentTableQueries {

    private PaymentTableDao paymentTableDao;

    public PaymentTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        paymentTableDao = daoSession.getPaymentTableDao();
    }

    /***************Save PaymentQueries to local Storage*********/
    public void insertPaymentToStorage(PaymentTable paymentTable){
        paymentTableDao.insert(paymentTable);
    }

    /************Load all collections from local Storage********/
    public List<PaymentTable> loadAllPayments(){
        return paymentTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public PaymentTable loadSinglePayment(String paymentId){
        return paymentTableDao.queryBuilder()
                .where(PaymentTableDao.Properties.PaymentId.eq(paymentId))
                .build()
                .list()
                .get(0);
    }

    /********Load all Unconfirmed PaymentQueries****************/
    public List<PaymentTable> loadUnconfimredPayment(){
        return paymentTableDao.queryBuilder()
                .where(PaymentTableDao.Properties.IsPaid.eq(false))
                .build()
                .list();
    }

    /********Load all Confirmed PaymentQueries****************/
    public List<PaymentTable> loadConfimredPayment(){
        return paymentTableDao.queryBuilder()
                .where(PaymentTableDao.Properties.IsPaid.eq(true))
                .build()
                .list();
    }

}
