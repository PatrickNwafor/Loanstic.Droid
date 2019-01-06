package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupBorrowerTable {

    @Unique
    private String groupId;

    @Id(autoincrement = true)
    private Long id;
    private String groupName;
    private String groupLeaderId;
    private String loanOfficerId;
    private int numberOfGroupMembers;
    private Boolean isGroupApproved;
    private String approvedBy;
    private String assignedBy;
    private Date timestamp;
    @Generated(hash = 828966481)
    public GroupBorrowerTable(String groupId, Long id, String groupName,
            String groupLeaderId, String loanOfficerId, int numberOfGroupMembers,
            Boolean isGroupApproved, String approvedBy, String assignedBy,
            Date timestamp) {
        this.groupId = groupId;
        this.id = id;
        this.groupName = groupName;
        this.groupLeaderId = groupLeaderId;
        this.loanOfficerId = loanOfficerId;
        this.numberOfGroupMembers = numberOfGroupMembers;
        this.isGroupApproved = isGroupApproved;
        this.approvedBy = approvedBy;
        this.assignedBy = assignedBy;
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
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Boolean getIsGroupApproved() {
        return this.isGroupApproved;
    }
    public void setIsGroupApproved(Boolean isGroupApproved) {
        this.isGroupApproved = isGroupApproved;
    }
    public String getApprovedBy() {
        return this.approvedBy;
    }
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    public String getAssignedBy() {
        return this.assignedBy;
    }
    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
