package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

@Entity
public class LoansTable {

    @Unique
    private String loanId;

    private String borrowerId;
    @Id(autoincrement = true)
    private Long id;

    private boolean isOtherLoanType;

    private double loanAmount;
    private Date loanCreationDate;
    private Date loanDuration, loanApprovedDate;
    private String loanTypeId, loanOfficerId, loanStatus;
    @Generated(hash = 573671248)
    public LoansTable(String loanId, String borrowerId, Long id,
            boolean isOtherLoanType, double loanAmount, Date loanCreationDate,
            Date loanDuration, Date loanApprovedDate, String loanTypeId,
            String loanOfficerId, String loanStatus) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
        this.id = id;
        this.isOtherLoanType = isOtherLoanType;
        this.loanAmount = loanAmount;
        this.loanCreationDate = loanCreationDate;
        this.loanDuration = loanDuration;
        this.loanApprovedDate = loanApprovedDate;
        this.loanTypeId = loanTypeId;
        this.loanOfficerId = loanOfficerId;
        this.loanStatus = loanStatus;
    }
    @Generated(hash = 1106604234)
    public LoansTable() {
    }
    public String getLoanId() {
        return this.loanId;
    }
    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }
    public String getBorrowerId() {
        return this.borrowerId;
    }
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getLoanAmount() {
        return this.loanAmount;
    }
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }
    public Date getLoanCreationDate() {
        return this.loanCreationDate;
    }
    public void setLoanCreationDate(Date loanCreationDate) {
        this.loanCreationDate = loanCreationDate;
    }
    public Date getLoanDuration() {
        return this.loanDuration;
    }
    public void setLoanDuration(Date loanDuration) {
        this.loanDuration = loanDuration;
    }
    public Date getLoanApprovedDate() {
        return this.loanApprovedDate;
    }
    public void setLoanApprovedDate(Date loanApprovedDate) {
        this.loanApprovedDate = loanApprovedDate;
    }
    public String getLoanTypeId() {
        return this.loanTypeId;
    }
    public void setLoanTypeId(String loanTypeId) {
        this.loanTypeId = loanTypeId;
    }
    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }
    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }
    public String getLoanStatus() {
        return this.loanStatus;
    }
    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }
    public boolean getIsOtherLoanType() {
        return this.isOtherLoanType;
    }
    public void setIsOtherLoanType(boolean isOtherLoanType) {
        this.isOtherLoanType = isOtherLoanType;
    }
}
