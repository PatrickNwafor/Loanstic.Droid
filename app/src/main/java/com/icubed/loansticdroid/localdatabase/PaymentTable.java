package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaymentTable implements Parcelable {

    @Unique
    private String paymentId;

    @Id(autoincrement = true)
    private Long id;

    private String collectionId, loanId, paymentModeId, loanOfficerId, paymentMode;
    private double amountPaid;
    private double photoLatitude, photoLongitude;
    private Date paymentTime, lastUpdatedAt;
    @Generated(hash = 1688859942)
    public PaymentTable(String paymentId, Long id, String collectionId,
            String loanId, String paymentModeId, String loanOfficerId,
            String paymentMode, double amountPaid, double photoLatitude,
            double photoLongitude, Date paymentTime, Date lastUpdatedAt) {
        this.paymentId = paymentId;
        this.id = id;
        this.collectionId = collectionId;
        this.loanId = loanId;
        this.paymentModeId = paymentModeId;
        this.loanOfficerId = loanOfficerId;
        this.paymentMode = paymentMode;
        this.amountPaid = amountPaid;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
        this.paymentTime = paymentTime;
        this.lastUpdatedAt = lastUpdatedAt;
    }
    @Generated(hash = 522700409)
    public PaymentTable() {
    }
    public String getPaymentId() {
        return this.paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCollectionId() {
        return this.collectionId;
    }
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    public String getLoanId() {
        return this.loanId;
    }
    public void setLoanId(String loanId) {
        this.loanId = loanId;
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
    public PaymentTable(Parcel in) {
        this.paymentId = in.readString();
        this.loanId = in.readString();
        this.loanOfficerId = in.readString();
        this.amountPaid = in.readDouble();
        this.photoLatitude = in.readDouble();
        this.photoLongitude = in.readDouble();
        this.paymentTime = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
        this.collectionId = in.readString();
        this.paymentModeId = in.readString();
        this.paymentMode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.paymentId);
        dest.writeString(this.loanId);
        dest.writeString(this.loanOfficerId);
        dest.writeDouble(this.amountPaid);
        dest.writeDouble(this.photoLatitude);
        dest.writeDouble(this.photoLongitude);
        dest.writeLong(this.paymentTime.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
        dest.writeString(this.collectionId);
        dest.writeString(this.paymentModeId);
        dest.writeString(this.paymentMode);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PaymentTable createFromParcel(Parcel in) {
            return new PaymentTable(in);
        }

        public PaymentTable[] newArray(int size) {
            return new PaymentTable[size];
        }
    };
}
