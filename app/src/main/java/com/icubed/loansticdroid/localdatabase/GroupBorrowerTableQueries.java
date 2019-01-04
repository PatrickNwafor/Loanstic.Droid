package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class GroupBorrowerTableQueries {

    private GroupBorrowerTableDao groupBorrowerTableDao;

    public GroupBorrowerTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        groupBorrowerTableDao = daoSession.getGroupBorrowerTableDao();
    }

    /***************Save Collection to local Storage*********/
    public void insertGroupToStorage(GroupBorrowerTable groupBorrowerTable){
        groupBorrowerTableDao.insert(groupBorrowerTable);
    }

    /************Load all collections from local Storage********/
    public List<GroupBorrowerTable> loadAllGroups(){
        return groupBorrowerTableDao.loadAll();
    }

    /**********Load a single collection from local Storage*******/
    public GroupBorrowerTable loadSingleBorrowerGroup(String groupId){
        return groupBorrowerTableDao.queryBuilder()
                .where(GroupBorrowerTableDao.Properties.GroupId.eq(groupId))
                .build()
                .list()
                .get(0);
    }

}