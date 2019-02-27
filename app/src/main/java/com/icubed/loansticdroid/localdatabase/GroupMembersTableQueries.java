package com.icubed.loansticdroid.localdatabase;

import android.app.Application;

import java.util.List;

public class GroupMembersTableQueries {

    private GroupMembersTableDao groupMembersTableDao;

    public GroupMembersTableQueries(Application application){
        DaoSession daoSession = ((App) application).getDaoSession();
        groupMembersTableDao = daoSession.getGroupMembersTableDao();
    }

    /***************Save DueCollection to local Storage*********/
    public void insertGroupMemberToStorage(GroupMembersTable groupMembersTable){
        groupMembersTableDao.insert(groupMembersTable);
    }

    /**********Load a single collection from local Storage*******/
    public List<GroupMembersTable> loadGroupMembers(String groupId){
        return groupMembersTableDao.queryBuilder()
                .where(GroupMembersTableDao.Properties.GroupId.eq(groupId))
                .build()
                .list();
    }

    /***************Delete Group from local Storage*********/
    public void deleteGroupFromStorage(GroupMembersTable groupMembersTable){
        groupMembersTableDao.delete(groupMembersTable);
    }

    public void updateGroupDetails(GroupMembersTable groupMembersTable){
        groupMembersTableDao.update(groupMembersTable);
    }
    
}
