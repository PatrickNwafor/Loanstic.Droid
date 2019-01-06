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

    private String loanId;

    @Id(autoincrement = true)
    private Long id;

    private String paymentScheduleOrder;

    private String interestId;

    private double interestAmount;

    private double repaymentPrincipal;

    private double totalRepaymentDue;

    private Date repaymentDueDate;

    private Boolean isPaid;

    private Date actualPaymentDate;

    private double actualPaymentAmount;

    private String paymentLocationId;

    private String paymentValidationPhotoUrl;

    private String paymentValidationThumbPhotoUrl;

    private String paymentMethod;

    @Generated(hash = 15052619)
    public PaymentTable(String paymentId, String loanId, Long id,
            String paymentScheduleOrder, String interestId, double interestAmount,
            double repaymentPrincipal, double totalRepaymentDue,
            Date repaymentDueDate, Boolean isPaid, Date actualPaymentDate,
            double actualPaymentAmount, String paymentLocationId,
            String paymentValidationPhotoUrl, String paymentValidationThumbPhotoUrl,
            String paymentMethod) {
        this.paymentId = paymentId;
        this.loanId = loanId;
        this.id = id;
        this.paymentScheduleOrder = paymentScheduleOrder;
        this.interestId = interestId;
        this.interestAmount = interestAmount;
        this.repaymentPrincipal = repaymentPrincipal;
        this.totalRepaymentDue = totalRepaymentDue;
        this.repaymentDueDate = repaymentDueDate;
        this.isPaid = isPaid;
        this.actualPaymentDate = actualPaymentDate;
        this.actualPaymentAmount = actualPaymentAmount;
        this.paymentLocationId = paymentLocationId;
        this.paymentValidationPhotoUrl = paymentValidationPhotoUrl;
        this.paymentValidationThumbPhotoUrl = paymentValidationThumbPhotoUrl;
        this.paymentMethod = paymentMethod;
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

    public String getLoanId() {
        return this.loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentScheduleOrder() {
        return this.paymentScheduleOrder;
    }

    public void setPaymentScheduleOrder(String paymentScheduleOrder) {
        this.paymentScheduleOrder = paymentScheduleOrder;
    }

    public String getInterestId() {
        return this.interestId;
    }

    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }

    public double getInterestAmount() {
        return this.interestAmount;
    }

    public void setInterestAmount(double interestAmount) {
        this.interestAmount = interestAmount;
    }

    public double getRepaymentPrincipal() {
        return this.repaymentPrincipal;
    }

    public void setRepaymentPrincipal(double repaymentPrincipal) {
        this.repaymentPrincipal = repaymentPrincipal;
    }

    public double getTotalRepaymentDue() {
        return this.totalRepaymentDue;
    }

    public void setTotalRepaymentDue(double totalRepaymentDue) {
        this.totalRepaymentDue = totalRepaymentDue;
    }

    public Date getRepaymentDueDate() {
        return this.repaymentDueDate;
    }

    public void setRepaymentDueDate(Date repaymentDueDate) {
        this.repaymentDueDate = repaymentDueDate;
    }

    public Boolean getIsPaid() {
        return this.isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Date getActualPaymentDate() {
        return this.actualPaymentDate;
    }

    public void setActualPaymentDate(Date actualPaymentDate) {
        this.actualPaymentDate = actualPaymentDate;
    }

    public double getActualPaymentAmount() {
        return this.actualPaymentAmount;
    }

    public void setActualPaymentAmount(double actualPaymentAmount) {
        this.actualPaymentAmount = actualPaymentAmount;
    }

    public String getPaymentLocationId() {
        return this.paymentLocationId;
    }

    public void setPaymentLocationId(String paymentLocationId) {
        this.paymentLocationId = paymentLocationId;
    }

    public String getPaymentValidationPhotoUrl() {
        return this.paymentValidationPhotoUrl;
    }

    public void setPaymentValidationPhotoUrl(String paymentValidationPhotoUrl) {
        this.paymentValidationPhotoUrl = paymentValidationPhotoUrl;
    }

    public String getPaymentValidationThumbPhotoUrl() {
        return this.paymentValidationThumbPhotoUrl;
    }

    public void setPaymentValidationThumbPhotoUrl(
            String paymentValidationThumbPhotoUrl) {
        this.paymentValidationThumbPhotoUrl = paymentValidationThumbPhotoUrl;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
