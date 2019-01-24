package com.icubed.loansticdroid.notification;

import java.util.Date;

public class GroupNotificationTable {
    private String groupId;
    private Date timestamp;

    public GroupNotificationTable() {
    }

    public GroupNotificationTable(String groupId, Date timestamp) {
        this.groupId = groupId;
        this.timestamp = timestamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
