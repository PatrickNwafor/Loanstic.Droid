package com.icubed.loansticdroid.notification;

import java.util.Date;

public class GroupNotificationTable {
    private String loanOfficerId;
    private String groupId;
    private Date timestamp;

    public GroupNotificationTable() {
    }

    public GroupNotificationTable(String loanOfficerId, String groupId, Date timestamp) {
        this.loanOfficerId = loanOfficerId;
        this.groupId = groupId;
        this.timestamp = timestamp;
    }

    public String getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
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
