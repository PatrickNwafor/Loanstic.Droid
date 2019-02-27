package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import com.icubed.loansticdroid.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollectionTableQueries {

    private CollectionTableDao collectionTableDao;

    public CollectionTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        collectionTableDao = daoSession.getCollectionTableDao();
    }

    /***************Save Collection to local Storage*********/
    public void insertCollectionToStorage(CollectionTable collectionTable){
        collectionTableDao.insert(collectionTable);
    }

    /************Load all collections from local Storage********/
    public List<CollectionTable> loadAllCollections(){
        return collectionTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public CollectionTable loadSingleCollection(String collectionId){
        List<CollectionTable> list = collectionTableDao.queryBuilder().where(CollectionTableDao.Properties.CollectionId.eq(collectionId)).build().list();

        if(list.isEmpty()) return null;

        return list.get(0);
    }

    /**********Load All Due Collections from local Storage***********/
    public List<CollectionTable>[] loadAllDueCollections(){
        // the list array, the zero index contains the dueCollection
        // the one index contains the overdueCollection
        List<CollectionTable> list = collectionTableDao.queryBuilder().build().list();

        List<CollectionTable>[] lists = new List[2];

        List<CollectionTable> dueCollectionList = new ArrayList<>();
        List<CollectionTable> overDueCollectionList = new ArrayList<>();

        for (CollectionTable collectionTable : list) {
            if(DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                    !collectionTable.getIsDueCollected()){
                dueCollectionList.add(collectionTable);
            }else if(collectionTable.getCollectionDueDate().before(new Date()) &&
                    !collectionTable.getIsDueCollected()){
                overDueCollectionList.add(collectionTable);
            }
        }

        lists[0] = dueCollectionList;
        lists[1] = overDueCollectionList;

        return lists;

    }

    /**********Load All Due Collections from local Storage***********/
    public List<CollectionTable> loadCollectionsForLoan(String loanId){
        return collectionTableDao.queryBuilder()
                .where(CollectionTableDao.Properties.LoanId.eq(loanId))
                .orderAsc(CollectionTableDao.Properties.CollectionNumber)
                .build()
                .list();
    }

    public List<CollectionTable> loadAllOverDueCollection(){
        return collectionTableDao.queryBuilder()
                .where(CollectionTableDao.Properties.IsDueCollected.eq(false))
                .where(CollectionTableDao.Properties.Timestamp.le(new Date()))
                .build()
                .list();
    }

    /********Update Table When Due Collection is Confirmed*********/
    public void updateCollection(CollectionTable collectionTable){
        collectionTableDao.update(collectionTable);
    }

    /****************Convert date to string format*****************/
    private String dateString(Date date){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat timeFormat = new SimpleDateFormat(myFormat, Locale.US);
        return timeFormat.format(date);
    }
}
