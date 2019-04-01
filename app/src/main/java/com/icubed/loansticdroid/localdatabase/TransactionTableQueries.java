package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class TransactionTableQueries {

    private TransactionTableDao transactionTableDao;

    public TransactionTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        transactionTableDao = daoSession.getTransactionTableDao();
    }

    /***************Save PaymentQueries to local Storage*********/
    public void insertTransactionToStorage(TransactionTable transactionTable){
        transactionTableDao.insert(transactionTable);
    }

    /************Load all collections from local Storage********/
    public List<TransactionTable> loadAllTransactions(String savingsId){
        return transactionTableDao.queryBuilder()
                .where(TransactionTableDao.Properties.SavingsId.eq(savingsId))
                .orderDesc(TransactionTableDao.Properties.TransactionTime)
                .build()
                .list();
    }

    /************Load all collections from local Storage********/
    public List<TransactionTable> loadAllDebitTransactions(String savingsId){
        return transactionTableDao.queryBuilder()
                .where(TransactionTableDao.Properties.SavingsId.eq(savingsId))
                .where(TransactionTableDao.Properties.TransactionType.eq(TransactionTable.TYPE_DEBIT))
                .orderDesc(TransactionTableDao.Properties.TransactionTime)
                .build()
                .list();
    }

    /************Load all collections from local Storage********/
    public List<TransactionTable> loadAllCreditTransactions(String savingsId){
        return transactionTableDao.queryBuilder()
                .where(TransactionTableDao.Properties.SavingsId.eq(savingsId))
                .where(TransactionTableDao.Properties.TransactionType.eq(TransactionTable.TYPE_CREDIT))
                .orderDesc(TransactionTableDao.Properties.TransactionTime)
                .build()
                .list();
    }

    /**********Load a single collection from local Storage*******/
    public TransactionTable loadSingleTransaction(String transactionId){
        List<TransactionTable> list = transactionTableDao.queryBuilder().where(TransactionTableDao.Properties.TransactionId.eq(transactionId)).build().list();

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    public void updateTransaction(TransactionTable transactionTable){
        transactionTableDao.update(transactionTable);
    }

    public void deleteTransaction(TransactionTable transactionTable){
        transactionTableDao.delete(transactionTable);
    }

}
