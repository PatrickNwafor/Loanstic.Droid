package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CollectionTable {

    @Unique
    private String collectionId;

    private String loanId;

    private int collectionNumber;

    @Id(autoincrement = true)
    private Long id;

    private Double collectionDueAmount, penalty;

    private Date collectionDueDate;
    private Date lastUpdatedAt;
    private Date timestamp;

    private Boolean isDueCollected;

    @Generated(hash = 1851244840)
    public CollectionTable(String collectionId, String loanId, int collectionNumber, Long id, Double collectionDueAmount, Double penalty, Date collectionDueDate, Date lastUpdatedAt, Date timestamp, Boolean isDueCollected) {
        this.collectionId = collectionId;
        this.loanId = loanId;
        this.collectionNumber = collectionNumber;
        this.id = id;
        this.collectionDueAmount = collectionDueAmount;
        this.penalty = penalty;
        this.collectionDueDate = collectionDueDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.timestamp = timestamp;
        this.isDueCollected = isDueCollected;
    }

    @Generated(hash = 1935108601)
    public CollectionTable() {
    }

    public String getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getLoanId() {
        return this.loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public int getCollectionNumber() {
        return this.collectionNumber;
    }

    public void setCollectionNumber(int collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCollectionDueAmount() {
        return this.collectionDueAmount;
    }

    public void setCollectionDueAmount(Double collectionDueAmount) {
        this.collectionDueAmount = collectionDueAmount;
    }

    public Date getCollectionDueDate() {
        return this.collectionDueDate;
    }

    public void setCollectionDueDate(Date collectionDueDate) {
        this.collectionDueDate = collectionDueDate;
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

    public Boolean getIsDueCollected() {
        return this.isDueCollected;
    }

    public void setIsDueCollected(Boolean isDueCollected) {
        this.isDueCollected = isDueCollected;
    }

    @Override
    public String toString() {
        return "CollectionTable{" + "collectionId='" + collectionId + '\'' + ", loanId='" + loanId + '\'' + ", collectionNumber=" + collectionNumber + ", id=" + id + ", collectionDueAmount=" + collectionDueAmount + ", collectionDueDate=" + collectionDueDate + ", lastUpdatedAt=" + lastUpdatedAt + ", timestamp=" + timestamp + ", isDueCollected=" + isDueCollected + '}';
    }

    public Double getPenalty() {
        return this.penalty;
    }

    public void setPenalty(Double penalty) {
        this.penalty = penalty;
    }
}
