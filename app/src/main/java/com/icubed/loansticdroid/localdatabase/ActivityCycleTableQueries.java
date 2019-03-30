package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class ActivityCycleTableQueries {
    
    private ActivityCycleTableDao activityCycleTableDao;

    public ActivityCycleTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        activityCycleTableDao = daoSession.getActivityCycleTableDao();
    }

    /***************Save ActivityCycle to local Storage*********/
    public void insertActivityCycleToStorage(ActivityCycleTable activityCycleTable){
        activityCycleTableDao.insert(activityCycleTable);
    }

    /************Load all collections from local Storage********/
    public List<ActivityCycleTable> loadAllActivityCycles(){
        return activityCycleTableDao.loadAll();
    }

    public List<ActivityCycleTable> loadAllActivityCyclesForBorrower(String borrowerId){
        return activityCycleTableDao.queryBuilder()
                .where(ActivityCycleTableDao.Properties.BorrowerId.eq(borrowerId))
                .build()
                .list();
    }

    /**********Load a single collection from local Storage*******/
    public ActivityCycleTable loadSingleActivityCycle(String activityCycleId){
        List<ActivityCycleTable> list = activityCycleTableDao.queryBuilder().where(ActivityCycleTableDao.Properties.ActivityCycleId.eq(activityCycleId)).build().list();

        if(list.isEmpty())return null;

        return list.get(0);
    }

    public ActivityCycleTable loadLastCreatedCycle(String borrowerId){
        List<ActivityCycleTable> list = activityCycleTableDao.queryBuilder().where(ActivityCycleTableDao.Properties.BorrowerId.eq(borrowerId)).orderDesc(ActivityCycleTableDao.Properties.StartCycleTime).build().list();

        if(!list.isEmpty()) return list.get(0);
        else return null;
    }

    /**********loadCurrentlyActiveCycleForBorrower from local Storage*******/
    public ActivityCycleTable loadCurrentlyActiveCycleForBorrower(String borrowerId){
        List<ActivityCycleTable> list = activityCycleTableDao.queryBuilder().where(ActivityCycleTableDao.Properties.BorrowerId.eq(borrowerId)).where(ActivityCycleTableDao.Properties.IsActive.eq(true)).build().list();

        if(!list.isEmpty()) return list.get(0);
        else return null;
    }

    /********Update Table When Due ActivityCycle is Confirmed*********/
    public void updateStorageAfterActivityCycleEnds(ActivityCycleTable activityCycleTable){
        activityCycleTableDao.update(activityCycleTable);
    }
    
}
