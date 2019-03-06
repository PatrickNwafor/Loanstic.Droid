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
        List<PaymentTable> list = paymentTableDao.queryBuilder().where(PaymentTableDao.Properties.PaymentId.eq(paymentId)).build().list();

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    public void updatePayment(PaymentTable paymentTable){
        paymentTableDao.update(paymentTable);
    }

    public void deletePayment(PaymentTable paymentTable){
        paymentTableDao.delete(paymentTable);
    }

}
