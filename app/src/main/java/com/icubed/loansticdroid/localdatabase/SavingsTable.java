package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsTable implements Parcelable {

    @Transient
    public static final String TARGET_TYPE_TIME = "time";
    @Transient
    public static final String TARGET_TYPE_MONEY = "money";
    @Transient
    public static final String TARGET_TYPE_FIXED = "fixed";


    @Unique
    private String savingsId;

    @Id(autoincrement = true)
    private Long id;

    private Date createdAt, lastUpdatedAt, startDate, maturityDate, minimumMaturityDate;
    private String savingsNumber;
    private double amountSaved;
    private String borrowerId;
    private boolean isThereInterest;
    private boolean isSavingsPlanCompleted;
    private double schedulePaid;

    private double amountTarget, fixedAmount, depositAmount, totalExpectedPeriodicAmount;
    private String targetType, savingsPlanName, savingsPlanTypeId;
    private double savingsInterestRate;
    private int savingsDuration;
    private String loanOfficerId, savingsInterestRateUnit, savingsPlanPurpose
            , savingsDurationUnit;

    // Parcelling part
    public SavingsTable(Parcel in) {
        this.savingsId = in.readString();
        this.createdAt = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
        this.savingsNumber = in.readString();
        this.amountSaved = in.readDouble();
        this.borrowerId = in.readString();
        this.amountTarget = in.readDouble();
        this.targetType = in.readString();
        this.savingsPlanName = in.readString();
        this.savingsPlanTypeId = in.readString();
        this.savingsInterestRate = in.readDouble();
        this.savingsDuration = in.readInt();
        this.loanOfficerId = in.readString();
        this.savingsInterestRateUnit = in.readString();
        this.savingsPlanPurpose = in.readString();
        this.savingsDurationUnit = in.readString();
        this.fixedAmount = in.readDouble();
        this.depositAmount = in.readDouble();
        this.startDate = new Date(in.readLong());
        this.maturityDate = new Date(in.readLong());
        this.isThereInterest = Boolean.parseBoolean(in.readString());
        this.totalExpectedPeriodicAmount = in.readDouble();
        this.minimumMaturityDate = new Date(in.readLong());
        this.isSavingsPlanCompleted = Boolean.parseBoolean(in.readString());
        this.schedulePaid = in.readDouble();
    }

    @Generated(hash = 2083113382)
    public SavingsTable(String savingsId, Long id, Date createdAt, Date lastUpdatedAt, Date startDate, Date maturityDate, Date minimumMaturityDate, String savingsNumber, double amountSaved, String borrowerId, boolean isThereInterest, boolean isSavingsPlanCompleted, double schedulePaid, double amountTarget, double fixedAmount, double depositAmount, double totalExpectedPeriodicAmount, String targetType, String savingsPlanName, String savingsPlanTypeId, double savingsInterestRate, int savingsDuration, String loanOfficerId, String savingsInterestRateUnit, String savingsPlanPurpose, String savingsDurationUnit) {
        this.savingsId = savingsId;
        this.id = id;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.minimumMaturityDate = minimumMaturityDate;
        this.savingsNumber = savingsNumber;
        this.amountSaved = amountSaved;
        this.borrowerId = borrowerId;
        this.isThereInterest = isThereInterest;
        this.isSavingsPlanCompleted = isSavingsPlanCompleted;
        this.schedulePaid = schedulePaid;
        this.amountTarget = amountTarget;
        this.fixedAmount = fixedAmount;
        this.depositAmount = depositAmount;
        this.totalExpectedPeriodicAmount = totalExpectedPeriodicAmount;
        this.targetType = targetType;
        this.savingsPlanName = savingsPlanName;
        this.savingsPlanTypeId = savingsPlanTypeId;
        this.savingsInterestRate = savingsInterestRate;
        this.savingsDuration = savingsDuration;
        this.loanOfficerId = loanOfficerId;
        this.savingsInterestRateUnit = savingsInterestRateUnit;
        this.savingsPlanPurpose = savingsPlanPurpose;
        this.savingsDurationUnit = savingsDurationUnit;
    }

    @Generated(hash = 1055947197)
    public SavingsTable() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.savingsId);
        dest.writeLong(this.createdAt.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
        dest.writeString(this.savingsNumber);
        dest.writeDouble(this.amountSaved);
        dest.writeString(this.borrowerId);
        dest.writeDouble(this.amountTarget);
        dest.writeString(this.targetType);
        dest.writeString(this.savingsPlanName);
        dest.writeString(this.savingsPlanTypeId);
        dest.writeDouble(this.savingsInterestRate);
        dest.writeInt(this.savingsDuration);
        dest.writeString(this.loanOfficerId);
        dest.writeString(this.savingsInterestRateUnit);
        dest.writeString(this.savingsPlanPurpose);
        dest.writeString(this.savingsDurationUnit);
        dest.writeDouble(this.fixedAmount);
        dest.writeDouble(this.depositAmount);
        dest.writeLong(this.startDate.getTime());
        dest.writeLong(this.maturityDate.getTime());
        dest.writeString(String.valueOf(this.isThereInterest));
        dest.writeDouble(this.totalExpectedPeriodicAmount);
        dest.writeLong(this.minimumMaturityDate.getTime());
        dest.writeString(String.valueOf(this.isSavingsPlanCompleted));
        dest.writeDouble(this.schedulePaid);
    }

    @Override
    public String toString() {
        return "SavingsTable{" + "savingsId='" + savingsId + '\'' + ", id=" + id + ", createdAt=" + createdAt + ", lastUpdatedAt=" + lastUpdatedAt + ", startDate=" + startDate + ", maturityDate=" + maturityDate + ", savingsNumber='" + savingsNumber + '\'' + ", amountSaved=" + amountSaved + ", borrowerId='" + borrowerId + '\'' + ", isThereInterest=" + isThereInterest + ", amountTarget=" + amountTarget + ", fixedAmount=" + fixedAmount + ", depositAmount=" + depositAmount + ", totalExpectedPeriodicAmount=" + totalExpectedPeriodicAmount + ", targetType='" + targetType + '\'' + ", savingsPlanName='" + savingsPlanName + '\'' + ", savingsPlanTypeId='" + savingsPlanTypeId + '\'' + ", savingsInterestRate=" + savingsInterestRate + ", savingsDuration=" + savingsDuration + ", loanOfficerId='" + loanOfficerId + '\'' + ", savingsInterestRateUnit='" + savingsInterestRateUnit + '\'' + ", savingsPlanPurpose='" + savingsPlanPurpose + '\'' + ", savingsDurationUnit='" + savingsDurationUnit + '\'' + '}';
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

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getMaturityDate() {
        return this.maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getSavingsNumber() {
        return this.savingsNumber;
    }

    public void setSavingsNumber(String savingsNumber) {
        this.savingsNumber = savingsNumber;
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

    public boolean getIsThereInterest() {
        return this.isThereInterest;
    }

    public void setIsThereInterest(boolean isThereInterest) {
        this.isThereInterest = isThereInterest;
    }

    public double getAmountTarget() {
        return this.amountTarget;
    }

    public void setAmountTarget(double amountTarget) {
        this.amountTarget = amountTarget;
    }

    public double getFixedAmount() {
        return this.fixedAmount;
    }

    public void setFixedAmount(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public double getDepositAmount() {
        return this.depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getTargetType() {
        return this.targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getSavingsPlanName() {
        return this.savingsPlanName;
    }

    public void setSavingsPlanName(String savingsPlanName) {
        this.savingsPlanName = savingsPlanName;
    }

    public String getSavingsPlanTypeId() {
        return this.savingsPlanTypeId;
    }

    public void setSavingsPlanTypeId(String savingsPlanTypeId) {
        this.savingsPlanTypeId = savingsPlanTypeId;
    }

    public double getSavingsInterestRate() {
        return this.savingsInterestRate;
    }

    public void setSavingsInterestRate(double savingsInterestRate) {
        this.savingsInterestRate = savingsInterestRate;
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

    public String getSavingsPlanPurpose() {
        return this.savingsPlanPurpose;
    }

    public void setSavingsPlanPurpose(String savingsPlanPurpose) {
        this.savingsPlanPurpose = savingsPlanPurpose;
    }

    public String getSavingsDurationUnit() {
        return this.savingsDurationUnit;
    }

    public void setSavingsDurationUnit(String savingsDurationUnit) {
        this.savingsDurationUnit = savingsDurationUnit;
    }

    public double getTotalExpectedPeriodicAmount() {
        return this.totalExpectedPeriodicAmount;
    }

    public void setTotalExpectedPeriodicAmount(double totalExpectedPeriodicAmount) {
        this.totalExpectedPeriodicAmount = totalExpectedPeriodicAmount;
    }

    public Date getMinimumMaturityDate() {
        return this.minimumMaturityDate;
    }

    public void setMinimumMaturityDate(Date minimumMaturityDate) {
        this.minimumMaturityDate = minimumMaturityDate;
    }

    public boolean getIsSavingsPlanCompleted() {
        return this.isSavingsPlanCompleted;
    }

    public void setIsSavingsPlanCompleted(boolean isSavingsPlanCompleted) {
        this.isSavingsPlanCompleted = isSavingsPlanCompleted;
    }

    public double getSchedulePaid() {
        return this.schedulePaid;
    }

    public void setSchedulePaid(double schedulePaid) {
        this.schedulePaid = schedulePaid;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SavingsTable createFromParcel(Parcel in) {
            return new SavingsTable(in);
        }

        public SavingsTable[] newArray(int size) {
            return new SavingsTable[size];
        }
    };
}
