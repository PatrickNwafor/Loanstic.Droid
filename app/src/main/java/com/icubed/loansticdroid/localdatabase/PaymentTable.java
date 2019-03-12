package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaymentTable {

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
}
