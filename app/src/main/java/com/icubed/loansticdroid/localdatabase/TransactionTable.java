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
public class TransactionTable implements Parcelable {

    @Transient
    public static final String TYPE_DEBIT = "DEBIT";
    @Transient
    public static final String TYPE_CREDIT = "CREDIT";

    @Unique
    private String transactionId;

    @Id(autoincrement = true)
    private Long id;

    private boolean isDepositFromRegular;

    private String savingsId, loanOfficerId, transactionType;
    private String savingsPlanCollectionId, paymentModeId, paymentMode;
    private double transactionAmount;
    private double photoLatitude, photoLongitude;
    private Date transactionTime, lastUpdatedAt;

    // Parcelling part
    public TransactionTable(Parcel in) {
        this.transactionId = in.readString();
        this.savingsId = in.readString();
        this.loanOfficerId = in.readString();
        this.transactionAmount = in.readDouble();
        this.photoLatitude = in.readDouble();
        this.photoLongitude = in.readDouble();
        this.transactionTime = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
        this.isDepositFromRegular = Boolean.parseBoolean(in.readString());
        this.savingsPlanCollectionId = in.readString();
        this.paymentMode = in.readString();
        this.paymentModeId = in.readString();
        this.transactionType = in.readString();
    }

    @Generated(hash = 1748615673)
    public TransactionTable(String transactionId, Long id,
            boolean isDepositFromRegular, String savingsId, String loanOfficerId,
            String transactionType, String savingsPlanCollectionId,
            String paymentModeId, String paymentMode, double transactionAmount,
            double photoLatitude, double photoLongitude, Date transactionTime,
            Date lastUpdatedAt) {
        this.transactionId = transactionId;
        this.id = id;
        this.isDepositFromRegular = isDepositFromRegular;
        this.savingsId = savingsId;
        this.loanOfficerId = loanOfficerId;
        this.transactionType = transactionType;
        this.savingsPlanCollectionId = savingsPlanCollectionId;
        this.paymentModeId = paymentModeId;
        this.paymentMode = paymentMode;
        this.transactionAmount = transactionAmount;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
        this.transactionTime = transactionTime;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Generated(hash = 1598703011)
    public TransactionTable() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transactionId);
        dest.writeString(this.savingsId);
        dest.writeString(this.loanOfficerId);
        dest.writeDouble(this.transactionAmount);
        dest.writeDouble(this.photoLatitude);
        dest.writeDouble(this.photoLongitude);
        dest.writeLong(this.transactionTime.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
        dest.writeString(String.valueOf(this.isDepositFromRegular));
        dest.writeString(this.savingsPlanCollectionId);
        dest.writeString(this.paymentMode);
        dest.writeString(this.paymentModeId);
        dest.writeString(this.transactionType);
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsDepositFromRegular() {
        return this.isDepositFromRegular;
    }

    public void setIsDepositFromRegular(boolean isDepositFromRegular) {
        this.isDepositFromRegular = isDepositFromRegular;
    }

    public String getSavingsId() {
        return this.savingsId;
    }

    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }

    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSavingsPlanCollectionId() {
        return this.savingsPlanCollectionId;
    }

    public void setSavingsPlanCollectionId(String savingsPlanCollectionId) {
        this.savingsPlanCollectionId = savingsPlanCollectionId;
    }

    public String getPaymentModeId() {
        return this.paymentModeId;
    }

    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getPaymentMode() {
        return this.paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public double getPhotoLatitude() {
        return this.photoLatitude;
    }

    public void setPhotoLatitude(double photoLatitude) {
        this.photoLatitude = photoLatitude;
    }

    public double getPhotoLongitude() {
        return this.photoLongitude;
    }

    public void setPhotoLongitude(double photoLongitude) {
        this.photoLongitude = photoLongitude;
    }

    public Date getTransactionTime() {
        return this.transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TransactionTable createFromParcel(Parcel in) {
            return new TransactionTable(in);
        }

        public TransactionTable[] newArray(int size) {
            return new TransactionTable[size];
        }
    };
}
