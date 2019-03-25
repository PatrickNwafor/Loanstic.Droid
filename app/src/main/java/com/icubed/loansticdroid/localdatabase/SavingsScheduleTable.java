package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsScheduleTable {

    @Unique
    private String savingsId;

    @Id(autoincrement = true)
    private Long Id;

    private String borrowerId, groupId;

    private double savingsAmount;
    private double savingsInterestRate, amountSaved;
    private Date savingsCreationDate;
    private Date savingsApprovedDate, lastUpdatedAt;
    private int savingsDuration;
    private String loanOfficerId, savingsInterestRateUnit
            , savingsDurationUnit, savingsAmountUnit, savingsNumber;
    @Generated(hash = 1525900887)
    public SavingsScheduleTable(String savingsId, Long Id, String borrowerId,
                                String groupId, double savingsAmount, double savingsInterestRate,
                                double amountSaved, Date savingsCreationDate, Date savingsApprovedDate,
                                Date lastUpdatedAt, int savingsDuration, String loanOfficerId,
                                String savingsInterestRateUnit, String savingsDurationUnit,
                                String savingsAmountUnit, String savingsNumber) {
        this.savingsId = savingsId;
        this.Id = Id;
        this.borrowerId = borrowerId;
        this.groupId = groupId;
        this.savingsAmount = savingsAmount;
        this.savingsInterestRate = savingsInterestRate;
        this.amountSaved = amountSaved;
        this.savingsCreationDate = savingsCreationDate;
        this.savingsApprovedDate = savingsApprovedDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.savingsDuration = savingsDuration;
        this.loanOfficerId = loanOfficerId;
        this.savingsInterestRateUnit = savingsInterestRateUnit;
        this.savingsDurationUnit = savingsDurationUnit;
        this.savingsAmountUnit = savingsAmountUnit;
        this.savingsNumber = savingsNumber;
    }
    @Generated(hash = 1055947197)
    public SavingsScheduleTable() {
    }
    public String getSavingsId() {
        return this.savingsId;
    }
    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getBorrowerId() {
        return this.borrowerId;
    }
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public double getSavingsAmount() {
        return this.savingsAmount;
    }
    public void setSavingsAmount(double savingsAmount) {
        this.savingsAmount = savingsAmount;
    }
    public double getSavingsInterestRate() {
        return this.savingsInterestRate;
    }
    public void setSavingsInterestRate(double savingsInterestRate) {
        this.savingsInterestRate = savingsInterestRate;
    }
    public double getAmountSaved() {
        return this.amountSaved;
    }
    public void setAmountSaved(double amountSaved) {
        this.amountSaved = amountSaved;
    }
    public Date getSavingsCreationDate() {
        return this.savingsCreationDate;
    }
    public void setSavingsCreationDate(Date savingsCreationDate) {
        this.savingsCreationDate = savingsCreationDate;
    }
    public Date getSavingsApprovedDate() {
        return this.savingsApprovedDate;
    }
    public void setSavingsApprovedDate(Date savingsApprovedDate) {
        this.savingsApprovedDate = savingsApprovedDate;
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    public int getSavingsDuration() {
        return this.savingsDuration;
    }
    public void setSavingsDuration(int savingsDuration) {
        this.savingsDuration = savingsDuration;
    }
    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }
    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }
    public String getSavingsInterestRateUnit() {
        return this.savingsInterestRateUnit;
    }
    public void setSavingsInterestRateUnit(String savingsInterestRateUnit) {
        this.savingsInterestRateUnit = savingsInterestRateUnit;
    }
    public String getSavingsDurationUnit() {
        return this.savingsDurationUnit;
    }
    public void setSavingsDurationUnit(String savingsDurationUnit) {
        this.savingsDurationUnit = savingsDurationUnit;
    }
    public String getSavingsAmountUnit() {
        return this.savingsAmountUnit;
    }
    public void setSavingsAmountUnit(String savingsAmountUnit) {
        this.savingsAmountUnit = savingsAmountUnit;
    }
    public String getSavingsNumber() {
        return this.savingsNumber;
    }
    public void setSavingsNumber(String savingsNumber) {
        this.savingsNumber = savingsNumber;
    }
}
