package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WithdrawalTable implements Parcelable {
    @Unique
    private String withdrawalId;

    @Id(autoincrement = true)
    private Long id;

    private String savingsId, loanOfficerId;
    private double amountPaid;
    private double photoLatitude, photoLongitude;
    private Date paymentTime, lastUpdatedAt;
    @Generated(hash = 100707937)
    public WithdrawalTable(String withdrawalId, Long id, String savingsId,
            String loanOfficerId, double amountPaid, double photoLatitude,
            double photoLongitude, Date paymentTime, Date lastUpdatedAt) {
        this.withdrawalId = withdrawalId;
        this.id = id;
        this.savingsId = savingsId;
        this.loanOfficerId = loanOfficerId;
        this.amountPaid = amountPaid;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
        this.paymentTime = paymentTime;
        this.lastUpdatedAt = lastUpdatedAt;
    }
    @Generated(hash = 567392738)
    public WithdrawalTable() {
    }
    public String getWithdrawalId() {
        return this.withdrawalId;
    }
    public void setWithdrawalId(String withdrawalId) {
        this.withdrawalId = withdrawalId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public double getAmountPaid() {
        return this.amountPaid;
    }
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
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
    public Date getPaymentTime() {
        return this.paymentTime;
    }
    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    // Parcelling part
    public WithdrawalTable(Parcel in) {
        this.withdrawalId = in.readString();
        this.savingsId = in.readString();
        this.loanOfficerId = in.readString();
        this.amountPaid = in.readDouble();
        this.photoLatitude = in.readDouble();
        this.photoLongitude = in.readDouble();
        this.paymentTime = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.withdrawalId);
        dest.writeString(this.savingsId);
        dest.writeString(this.loanOfficerId);
        dest.writeDouble(this.amountPaid);
        dest.writeDouble(this.photoLatitude);
        dest.writeDouble(this.photoLongitude);
        dest.writeLong(this.paymentTime.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WithdrawalTable createFromParcel(Parcel in) {
            return new WithdrawalTable(in);
        }

        public WithdrawalTable[] newArray(int size) {
            return new WithdrawalTable[size];
        }
    };
}
