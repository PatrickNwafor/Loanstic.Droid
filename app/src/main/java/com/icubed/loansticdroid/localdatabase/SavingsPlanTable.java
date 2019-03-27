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
public class SavingsPlanTable implements Parcelable {

    @Transient
    public static final String TARGET_TYPE_TIME = "time";
    @Transient
    public static final String TARGET_TYPE_MONEY = "money";

    @Unique
    private String savingsPlanId;

    @Id(autoincrement = true)
    private Long Id;

    private double amountTarget;
    private String targetType;
    private double savingsInterestRate, amountSaved;
    private Date savingsCreationDate;
    private Date lastUpdatedAt;
    private int savingsDuration;
    private String loanOfficerId, savingsInterestRateUnit, savingsSchedulePurpose, savingsPlanTypeId
            , savingsDurationUnit, savingsAmountUnit, savingsScheduleNumber, savingsId;

    // Parcelling part
    public SavingsPlanTable(Parcel in) {
        this.savingsPlanId = in.readString();
        this.amountTarget = in.readDouble();
        this.targetType = in.readString();
        this.savingsInterestRate = in.readDouble();
        this.amountSaved = in.readDouble();
        this.savingsCreationDate = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
        this.savingsDuration = in.readInt();
        this.loanOfficerId = in.readString();
        this.savingsInterestRateUnit = in.readString();
        this.savingsSchedulePurpose = in.readString();
        this.savingsDurationUnit = in.readString();
        this.savingsAmountUnit = in.readString();
        this.savingsScheduleNumber = in.readString();
        this.savingsId = in.readString();
    }

    @Generated(hash = 1222501629)
    public SavingsPlanTable(String savingsPlanId, Long Id, double amountTarget, String targetType,
            double savingsInterestRate, double amountSaved, Date savingsCreationDate,
            Date lastUpdatedAt, int savingsDuration, String loanOfficerId,
            String savingsInterestRateUnit, String savingsSchedulePurpose, String savingsPlanTypeId,
            String savingsDurationUnit, String savingsAmountUnit, String savingsScheduleNumber,
            String savingsId) {
        this.savingsPlanId = savingsPlanId;
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
        this.savingsPlanTypeId = savingsPlanTypeId;
        this.savingsDurationUnit = savingsDurationUnit;
        this.savingsAmountUnit = savingsAmountUnit;
        this.savingsScheduleNumber = savingsScheduleNumber;
        this.savingsId = savingsId;
    }

    @Generated(hash = 1065555106)
    public SavingsPlanTable() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.savingsPlanId);
        dest.writeDouble(this.amountTarget);
        dest.writeString(this.targetType);
        dest.writeDouble(this.savingsInterestRate);
        dest.writeDouble(this.amountSaved);
        dest.writeLong(this.savingsCreationDate.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
        dest.writeInt(this.savingsDuration);
        dest.writeString(this.loanOfficerId);
        dest.writeString(this.savingsInterestRateUnit);
        dest.writeString(this.savingsSchedulePurpose);
        dest.writeString(this.savingsDurationUnit);
        dest.writeString(this.savingsAmountUnit);
        dest.writeString(this.savingsScheduleNumber);
        dest.writeString(this.savingsId);
    }

    public String getSavingsPlanId() {
        return this.savingsPlanId;
    }

    public void setSavingsPlanId(String savingsPlanId) {
        this.savingsPlanId = savingsPlanId;
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

    public String getSavingsPlanTypeId() {
        return this.savingsPlanTypeId;
    }

    public void setSavingsPlanTypeId(String savingsPlanTypeId) {
        this.savingsPlanTypeId = savingsPlanTypeId;
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SavingsPlanTable createFromParcel(Parcel in) {
            return new SavingsPlanTable(in);
        }

        public SavingsPlanTable[] newArray(int size) {
            return new SavingsPlanTable[size];
        }
    };
}