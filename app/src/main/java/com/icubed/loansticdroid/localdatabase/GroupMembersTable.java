package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupMembersTable {

    @Unique
    private String groupMemberId;
    @Id
    private Long id;

    private String groupId, borrowerId;
    private Date timestamp;
    @Generated(hash = 509070409)
    public GroupMembersTable(String groupMemberId, Long id, String groupId,
            String borrowerId, Date timestamp) {
        this.groupMemberId = groupMemberId;
        this.id = id;
        this.groupId = groupId;
        this.borrowerId = borrowerId;
        this.timestamp = timestamp;
    }
    @Generated(hash = 1113163682)
    public GroupMembersTable() {
    }
    public String getGroupMemberId() {
        return this.groupMemberId;
    }
    public void setGroupMemberId(String groupMemberId) {
        this.groupMemberId = groupMemberId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getBorrowerId() {
        return this.borrowerId;
    }
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
