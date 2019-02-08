package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class OtherLoanTypesTable{

    @Unique
    private String otherLoanTypeId;

    private String branchId;

    @Id(autoincrement = true)
    private Long Id;

    private String otherLoanTypeName;
    private String otherLoanTypeDescription;
    private Date lastUpdatedAt;

    private Date timestamp;

    @Generated(hash = 1138701537)
    public OtherLoanTypesTable(String otherLoanTypeId, String branchId, Long Id,
            String otherLoanTypeName, String otherLoanTypeDescription,
            Date lastUpdatedAt, Date timestamp) {
        this.otherLoanTypeId = otherLoanTypeId;
        this.branchId = branchId;
        this.Id = Id;
        this.otherLoanTypeName = otherLoanTypeName;
        this.otherLoanTypeDescription = otherLoanTypeDescription;
        this.lastUpdatedAt = lastUpdatedAt;
        this.timestamp = timestamp;
    }

    @Generated(hash = 1150651568)
    public OtherLoanTypesTable() {
    }

    public String getOtherLoanTypeId() {
        return this.otherLoanTypeId;
    }

    public void setOtherLoanTypeId(String otherLoanTypeId) {
        this.otherLoanTypeId = otherLoanTypeId;
    }

    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getOtherLoanTypeName() {
        return this.otherLoanTypeName;
    }

    public void setOtherLoanTypeName(String otherLoanTypeName) {
        this.otherLoanTypeName = otherLoanTypeName;
    }

    public String getOtherLoanTypeDescription() {
        return this.otherLoanTypeDescription;
    }

    public void setOtherLoanTypeDescription(String otherLoanTypeDescription) {
        this.otherLoanTypeDescription = otherLoanTypeDescription;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
