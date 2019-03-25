package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsTable {

    @Unique
    private String savingsId;

    @Id(autoincrement = true)
    private Long id;

    private Date createdAt, lastUpdatedAt;
    private int accountNumber;
    private double amountSaved;
    private String borrowerId;
    @Generated(hash = 2146617774)
    public SavingsTable(String savingsId, Long id, Date createdAt,
            Date lastUpdatedAt, int accountNumber, double amountSaved,
            String borrowerId) {
        this.savingsId = savingsId;
        this.id = id;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.accountNumber = accountNumber;
        this.amountSaved = amountSaved;
        this.borrowerId = borrowerId;
    }
    @Generated(hash = 1055947197)
    public SavingsTable() {
    }
    public String getSavingsId() {
        return this.savingsId;
    }
    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    public int getAccountNumber() {
        return this.accountNumber;
    }
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
    public double getAmountSaved() {
        return this.amountSaved;
    }
    public void setAmountSaved(double amountSaved) {
        this.amountSaved = amountSaved;
    }
    public String getBorrowerId() {
        return this.borrowerId;
    }
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

}
