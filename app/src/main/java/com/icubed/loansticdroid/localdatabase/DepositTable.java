package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DepositTable {
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
}
