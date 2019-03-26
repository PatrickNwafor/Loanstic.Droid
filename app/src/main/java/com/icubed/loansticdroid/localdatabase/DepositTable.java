package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DepositTable implements Parcelable {
    @Unique
    private String depositId;

    @Id(autoincrement = true)
    private Long id;

    private boolean isDepositFromRegular;

    private String savingsScheduleCollectionId, savingsId, paymentModeId, loanOfficerId, paymentMode;
    private double amountPaid;
    private double photoLatitude, photoLongitude;
    private Date paymentTime, lastUpdatedAt;
    @Generated(hash = 1694697846)
    public DepositTable(String depositId, Long id, boolean isDepositFromRegular,
            String savingsScheduleCollectionId, String savingsId, String paymentModeId,
            String loanOfficerId, String paymentMode, double amountPaid, double photoLatitude,
            double photoLongitude, Date paymentTime, Date lastUpdatedAt) {
        this.depositId = depositId;
        this.id = id;
        this.isDepositFromRegular = isDepositFromRegular;
        this.savingsScheduleCollectionId = savingsScheduleCollectionId;
        this.savingsId = savingsId;
        this.paymentModeId = paymentModeId;
        this.loanOfficerId = loanOfficerId;
        this.paymentMode = paymentMode;
        this.amountPaid = amountPaid;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
        this.paymentTime = paymentTime;
        this.lastUpdatedAt = lastUpdatedAt;
    }
    @Generated(hash = 2016384102)
    public DepositTable() {
    }
    public String getDepositId() {
        return this.depositId;
    }
    public void setDepositId(String depositId) {
        this.depositId = depositId;
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
    public String getSavingsScheduleCollectionId() {
        return this.savingsScheduleCollectionId;
    }
    public void setSavingsScheduleCollectionId(String savingsScheduleCollectionId) {
        this.savingsScheduleCollectionId = savingsScheduleCollectionId;
    }
    public String getSavingsId() {
        return this.savingsId;
    }
    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }
    public String getPaymentModeId() {
        return this.paymentModeId;
    }
    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }
    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }
    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }
    public String getPaymentMode() {
        return this.paymentMode;
    }
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
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
    public DepositTable(Parcel in) {
        this.depositId = in.readString();
        this.savingsId = in.readString();
        this.loanOfficerId = in.readString();
        this.amountPaid = in.readDouble();
        this.photoLatitude = in.readDouble();
        this.photoLongitude = in.readDouble();
        this.paymentTime = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
        this.savingsScheduleCollectionId = in.readString();
        this.paymentModeId = in.readString();
        this.paymentMode = in.readString();
        this.isDepositFromRegular = Boolean.parseBoolean(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.depositId);
        dest.writeString(this.savingsId);
        dest.writeString(this.loanOfficerId);
        dest.writeDouble(this.amountPaid);
        dest.writeDouble(this.photoLatitude);
        dest.writeDouble(this.photoLongitude);
        dest.writeLong(this.paymentTime.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
        dest.writeString(this.savingsScheduleCollectionId);
        dest.writeString(this.paymentModeId);
        dest.writeString(this.paymentMode);
        dest.writeString(String.valueOf(this.isDepositFromRegular));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DepositTable createFromParcel(Parcel in) {
            return new DepositTable(in);
        }

        public DepositTable[] newArray(int size) {
            return new DepositTable[size];
        }
    };
}
