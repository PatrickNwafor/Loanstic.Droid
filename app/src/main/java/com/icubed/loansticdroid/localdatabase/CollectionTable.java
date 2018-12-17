package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "COLLECTION")
public class CollectionTable {

    @Unique
    private String collectionId;

    private String borrowersId;

    private String loanOfficerId;

    private String loanId;

    private String loanDate;

    private int collectionNumber;

    private Double collectionDueAmount;

    private String collectionDueDate;

    private Date timestamp;

    private Boolean isDueCollected;

    @Generated(hash = 1806556757)
    public CollectionTable(String collectionId, String borrowersId,
            String loanOfficerId, String loanId, String loanDate,
            int collectionNumber, Double collectionDueAmount,
            String collectionDueDate, Date timestamp, Boolean isDueCollected) {
        this.collectionId = collectionId;
        this.borrowersId = borrowersId;
        this.loanOfficerId = loanOfficerId;
        this.loanId = loanId;
        this.loanDate = loanDate;
        this.collectionNumber = collectionNumber;
        this.collectionDueAmount = collectionDueAmount;
        this.collectionDueDate = collectionDueDate;
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

    public String getBorrowersId() {
        return this.borrowersId;
    }

    public void setBorrowersId(String borrowersId) {
        this.borrowersId = borrowersId;
    }

    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getLoanId() {
        return this.loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getLoanDate() {
        return this.loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public int getCollectionNumber() {
        return this.collectionNumber;
    }

    public void setCollectionNumber(int collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public Double getCollectionDueAmount() {
        return this.collectionDueAmount;
    }

    public void setCollectionDueAmount(Double collectionDueAmount) {
        this.collectionDueAmount = collectionDueAmount;
    }

    public String getCollectionDueDate() {
        return this.collectionDueDate;
    }

    public void setCollectionDueDate(String collectionDueDate) {
        this.collectionDueDate = collectionDueDate;
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
        return "CollectionTable{" + "collectionId='" + collectionId + '\'' + ", borrowersId='" + borrowersId + '\'' + ", loanOfficerId='" + loanOfficerId + '\'' + ", loanId='" + loanId + '\'' + ", loanDate='" + loanDate + '\'' + ", collectionNumber=" + collectionNumber + ", collectionDueAmount=" + collectionDueAmount + ", collectionDueDate='" + collectionDueDate + '\'' + ", timestamp=" + timestamp + ", isDueCollected=" + isDueCollected + '}';
    }
}
