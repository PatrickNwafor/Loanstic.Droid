package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ActivityCycleTable {

    @Unique
    private String activityCycleId;

    @Id(autoincrement = true)
    private Long Id;

    private Boolean isActive;
    private String borrowerId;
    private Date startCycleTime;
    private Date endCycleTime, lastUpdatedAt;
    @Generated(hash = 76276144)
    public ActivityCycleTable(String activityCycleId, Long Id, Boolean isActive, String borrowerId, Date startCycleTime, Date endCycleTime, Date lastUpdatedAt) {
        this.activityCycleId = activityCycleId;
        this.Id = Id;
        this.isActive = isActive;
        this.borrowerId = borrowerId;
        this.startCycleTime = startCycleTime;
        this.endCycleTime = endCycleTime;
        this.lastUpdatedAt = lastUpdatedAt;
    }
    @Generated(hash = 1053731150)
    public ActivityCycleTable() {
    }
    public String getActivityCycleId() {
        return this.activityCycleId;
    }
    public void setActivityCycleId(String activityCycleId) {
        this.activityCycleId = activityCycleId;
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public String getBorrowerId() {
        return this.borrowerId;
    }
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }
    public Date getStartCycleTime() {
        return this.startCycleTime;
    }
    public void setStartCycleTime(Date startCycleTime) {
        this.startCycleTime = startCycleTime;
    }
    public Date getEndCycleTime() {
        return this.endCycleTime;
    }
    public void setEndCycleTime(Date endCycleTime) {
        this.endCycleTime = endCycleTime;
    }

    @Override
    public String toString() {
        return "ActivityCycleTable{" + "activityCycleId='" + activityCycleId + '\'' + ", Id=" + Id + ", isActive=" + isActive + ", borrowerId='" + borrowerId + '\'' + ", startCycleTime=" + startCycleTime + ", endCycleTime=" + endCycleTime + '}';
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
