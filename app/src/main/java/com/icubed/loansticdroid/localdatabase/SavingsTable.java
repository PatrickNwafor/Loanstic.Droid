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

    @Unique
    private String savingsId;

    @Id(autoincrement = true)
    private Long id;

    private Date createdAt, lastUpdatedAt;
    private String savingsNumber;
    private double amountSaved;
    private String borrowerId;

    private double amountTarget;
    private String targetType, savingsPlanName, savingsPlanTypeId;
    private double savingsInterestRate;
    private int savingsDuration;
    private String loanOfficerId, savingsInterestRateUnit, savingsSchedulePurpose
            , savingsDurationUnit, savingsAmountUnit;

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
        this.savingsSchedulePurpose = in.readString();
        this.savingsDurationUnit = in.readString();
        this.savingsAmountUnit = in.readString();
    }

    @Generated(hash = 1808753213)
    public SavingsTable(String savingsId, Long id, Date createdAt,
            Date lastUpdatedAt, String savingsNumber, double amountSaved,
            String borrowerId, double amountTarget, String targetType,
            String savingsPlanName, String savingsPlanTypeId,
            double savingsInterestRate, int savingsDuration, String loanOfficerId,
            String savingsInterestRateUnit, String savingsSchedulePurpose,
            String savingsDurationUnit, String savingsAmountUnit) {
        this.savingsId = savingsId;
        this.id = id;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.savingsNumber = savingsNumber;
        this.amountSaved = amountSaved;
        this.borrowerId = borrowerId;
        this.amountTarget = amountTarget;
        this.targetType = targetType;
        this.savingsPlanName = savingsPlanName;
        this.savingsPlanTypeId = savingsPlanTypeId;
        this.savingsInterestRate = savingsInterestRate;
        this.savingsDuration = savingsDuration;
        this.loanOfficerId = loanOfficerId;
        this.savingsInterestRateUnit = savingsInterestRateUnit;
        this.savingsSchedulePurpose = savingsSchedulePurpose;
        this.savingsDurationUnit = savingsDurationUnit;
        this.savingsAmountUnit = savingsAmountUnit;
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
        dest.writeString(this.savingsSchedulePurpose);
        dest.writeString(this.savingsDurationUnit);
        dest.writeString(this.savingsAmountUnit);
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SavingsTable createFromParcel(Parcel in) {
            return new SavingsTable(in);
        }

        public SavingsTable[] newArray(int size) {
            return new SavingsTable[size];
        }
    };
}
