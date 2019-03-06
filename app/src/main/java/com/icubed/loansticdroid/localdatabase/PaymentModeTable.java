package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

@Entity
public class PaymentModeTable {

    @Unique
    private String paymentModeId;

    @Id(autoincrement = true)
    private Long id;

    private String paymentMode;
    private Date lastUpdatedAt;

    @Generated(hash = 1198921654)
    public PaymentModeTable(String paymentModeId, Long id, String paymentMode,
            Date lastUpdatedAt) {
        this.paymentModeId = paymentModeId;
        this.id = id;
        this.paymentMode = paymentMode;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Generated(hash = 1449627166)
    public PaymentModeTable() {
    }

    public String getPaymentModeId() {
        return this.paymentModeId;
    }

    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentMode() {
        return this.paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
