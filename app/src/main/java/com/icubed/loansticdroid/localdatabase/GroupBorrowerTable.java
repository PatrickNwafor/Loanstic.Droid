package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity()
public class GroupBorrowerTable {

    @Unique
    private String groupId;

    private String groupName;
    private String groupLeaderId;
    private String loanOfficerId;
    private int numberOfGroupMembers;

    private Date timestamp;

    @Generated(hash = 201876450)
    public GroupBorrowerTable(String groupId, String groupName,
            String groupLeaderId, String loanOfficerId, int numberOfGroupMembers,
            Date timestamp) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupLeaderId = groupLeaderId;
        this.loanOfficerId = loanOfficerId;
        this.numberOfGroupMembers = numberOfGroupMembers;
        this.timestamp = timestamp;
    }

    @Generated(hash = 232308116)
    public GroupBorrowerTable() {
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupLeaderId() {
        return this.groupLeaderId;
    }

    public void setGroupLeaderId(String groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }

    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public int getNumberOfGroupMembers() {
        return this.numberOfGroupMembers;
    }

    public void setNumberOfGroupMembers(int numberOfGroupMembers) {
        this.numberOfGroupMembers = numberOfGroupMembers;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
