package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsScheduleTable {

    @Transient
    public static final String TARGET_TYPE_TIME = "time";
    @Transient
    public static final String TARGET_TYPE_MONEY = "money";

    @Unique
    private String savingsScheduleId;

    @Id(autoincrement = true)
    private Long Id;

    private double amountTarget;
    private String targetType;
    private double savingsInterestRate, amountSaved;
    private Date savingsCreationDate;
    private Date lastUpdatedAt;
    private int savingsDuration;
    private String loanOfficerId, savingsInterestRateUnit, savingsSchedulePurpose
            , savingsDurationUnit, savingsAmountUnit, savingsScheduleNumber, savingsId;
    @Generated(hash = 196202185)
    public SavingsScheduleTable(String savingsScheduleId, Long Id, double amountTarget,
            String targetType, double savingsInterestRate, double amountSaved,
            Date savingsCreationDate, Date lastUpdatedAt, int savingsDuration,
            String loanOfficerId, String savingsInterestRateUnit,
            String savingsSchedulePurpose, String savingsDurationUnit,
            String savingsAmountUnit, String savingsScheduleNumber, String savingsId) {
        this.savingsScheduleId = savingsScheduleId;
        this.Id = Id;
        this.amountTarget = amountTarget;
        this.targetType = targetType;
        this.savingsInterestRate = savingsInterestRate;
        this.amountSaved = amountSaved;
        this.savingsCreationDate = savingsCreationDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.savingsDuration = savingsDuration;
        this.loanOfficerId = loanOfficerId;
        this.savingsInterestRateUnit = savingsInterestRateUnit;
        this.savingsSchedulePurpose = savingsSchedulePurpose;
        this.savingsDurationUnit = savingsDurationUnit;
        this.savingsAmountUnit = savingsAmountUnit;
        this.savingsScheduleNumber = savingsScheduleNumber;
        this.savingsId = savingsId;
    }
    @Generated(hash = 1506363868)
    public SavingsScheduleTable() {
    }
    public String getSavingsScheduleId() {
        return this.savingsScheduleId;
    }
    public void setSavingsScheduleId(String savingsScheduleId) {
        this.savingsScheduleId = savingsScheduleId;
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public double getAmountTarget() {
        return this.amountTarget;
    }
    public void setAmountTarget(double amountTarget) {
        this.amountTarget = amountTarget;
    }
    public String getTargetType() {
        return this.targetType;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
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
    public String getSavingsSchedulePurpose() {
        return this.savingsSchedulePurpose;
    }
    public void setSavingsSchedulePurpose(String savingsSchedulePurpose) {
        this.savingsSchedulePurpose = savingsSchedulePurpose;
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
    public String getSavingsScheduleNumber() {
        return this.savingsScheduleNumber;
    }
    public void setSavingsScheduleNumber(String savingsScheduleNumber) {
        this.savingsScheduleNumber = savingsScheduleNumber;
    }
    public String getSavingsId() {
        return this.savingsId;
    }
    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }
}
