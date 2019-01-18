package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BorrowerGroupsTable {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String documentId;

    private String groupId;
    private String borrowerId;
    private Date timestamp;
    @Generated(hash = 1904989122)
    public BorrowerGroupsTable(Long id, String documentId, String groupId,
            String borrowerId, Date timestamp) {
        this.id = id;
        this.documentId = documentId;
        this.groupId = groupId;
        this.borrowerId = borrowerId;
        this.timestamp = timestamp;
    }
    @Generated(hash = 5069995)
    public BorrowerGroupsTable() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDocumentId() {
        return this.documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
