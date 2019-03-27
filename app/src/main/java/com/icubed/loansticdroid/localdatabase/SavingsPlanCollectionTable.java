package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsPlanCollectionTable {

    @Unique
        private String savingsPlanCollectionId;

    private String savingsScheduleId;

    private int savingsCollectionNumber;

    @Id(autoincrement = true)
    private Long id;

    private Double savingsCollectionAmount;

    private Date savingsCollectionDueDate;
    private Date lastUpdatedAt;
    private Date timestamp;
    private double amountPaid;
    private String savingsCollectionState;

    private Boolean isSavingsCollected;

    @Generated(hash = 979394138)
    public SavingsPlanCollectionTable(String savingsPlanCollectionId,
            String savingsScheduleId, int savingsCollectionNumber, Long id,
            Double savingsCollectionAmount, Date savingsCollectionDueDate,
            Date lastUpdatedAt, Date timestamp, double amountPaid,
            String savingsCollectionState, Boolean isSavingsCollected) {
        this.savingsPlanCollectionId = savingsPlanCollectionId;
        this.savingsScheduleId = savingsScheduleId;
        this.savingsCollectionNumber = savingsCollectionNumber;
        this.id = id;
        this.savingsCollectionAmount = savingsCollectionAmount;
        this.savingsCollectionDueDate = savingsCollectionDueDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.timestamp = timestamp;
        this.amountPaid = amountPaid;
        this.savingsCollectionState = savingsCollectionState;
        this.isSavingsCollected = isSavingsCollected;
    }

    @Generated(hash = 41577661)
    public SavingsPlanCollectionTable() {
    }

    public String getSavingsPlanCollectionId() {
        return this.savingsPlanCollectionId;
    }

    public void setSavingsPlanCollectionId(String savingsPlanCollectionId) {
        this.savingsPlanCollectionId = savingsPlanCollectionId;
    }

    public String getSavingsScheduleId() {
        return this.savingsScheduleId;
    }

    public void setSavingsScheduleId(String savingsScheduleId) {
        this.savingsScheduleId = savingsScheduleId;
    }

    public int getSavingsCollectionNumber() {
        return this.savingsCollectionNumber;
    }

    public void setSavingsCollectionNumber(int savingsCollectionNumber) {
        this.savingsCollectionNumber = savingsCollectionNumber;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getSavingsCollectionAmount() {
        return this.savingsCollectionAmount;
    }

    public void setSavingsCollectionAmount(Double savingsCollectionAmount) {
        this.savingsCollectionAmount = savingsCollectionAmount;
    }

    public Date getSavingsCollectionDueDate() {
        return this.savingsCollectionDueDate;
    }

    public void setSavingsCollectionDueDate(Date savingsCollectionDueDate) {
        this.savingsCollectionDueDate = savingsCollectionDueDate;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmountPaid() {
        return this.amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getSavingsCollectionState() {
        return this.savingsCollectionState;
    }

    public void setSavingsCollectionState(String savingsCollectionState) {
        this.savingsCollectionState = savingsCollectionState;
    }

    public Boolean getIsSavingsCollected() {
        return this.isSavingsCollected;
    }

    public void setIsSavingsCollected(Boolean isSavingsCollected) {
        this.isSavingsCollected = isSavingsCollected;
    }
}
