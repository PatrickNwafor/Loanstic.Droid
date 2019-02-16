package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

@Entity
public class LoansTable implements Parcelable {

    @Unique
    private String loanId;

    private String borrowerId, groupId;
    @Id(autoincrement = true)
    private Long id;

    private boolean isOtherLoanType, isLoanApproved;

    private double loanAmount, loanFees, repaymentAmount;
    private double loanInterestRate;
    private Date loanCreationDate, loanReleaseDate;
    private Date loanApprovedDate, lastUpdatedAt;
    private int loanDuration;
    private String loanTypeId, loanOfficerId, loanInterestRateUnit
            , loanDurationUnit, repaymentAmountUnit;
    @Generated(hash = 453351641)
    public LoansTable(String loanId, String borrowerId, String groupId, Long id,
            boolean isOtherLoanType, boolean isLoanApproved, double loanAmount,
            double loanFees, double repaymentAmount, double loanInterestRate,
            Date loanCreationDate, Date loanReleaseDate, Date loanApprovedDate,
            Date lastUpdatedAt, int loanDuration, String loanTypeId,
            String loanOfficerId, String loanInterestRateUnit,
            String loanDurationUnit, String repaymentAmountUnit) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
        this.groupId = groupId;
        this.id = id;
        this.isOtherLoanType = isOtherLoanType;
        this.isLoanApproved = isLoanApproved;
        this.loanAmount = loanAmount;
        this.loanFees = loanFees;
        this.repaymentAmount = repaymentAmount;
        this.loanInterestRate = loanInterestRate;
        this.loanCreationDate = loanCreationDate;
        this.loanReleaseDate = loanReleaseDate;
        this.loanApprovedDate = loanApprovedDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.loanDuration = loanDuration;
        this.loanTypeId = loanTypeId;
        this.loanOfficerId = loanOfficerId;
        this.loanInterestRateUnit = loanInterestRateUnit;
        this.loanDurationUnit = loanDurationUnit;
        this.repaymentAmountUnit = repaymentAmountUnit;
    }
    @Generated(hash = 1106604234)
    public LoansTable() {
    }
    public String getLoanId() {
        return this.loanId;
    }
    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }
    public String getBorrowerId() {
        return this.borrowerId;
    }
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean getIsOtherLoanType() {
        return this.isOtherLoanType;
    }
    public void setIsOtherLoanType(boolean isOtherLoanType) {
        this.isOtherLoanType = isOtherLoanType;
    }
    public boolean getIsLoanApproved() {
        return this.isLoanApproved;
    }
    public void setIsLoanApproved(boolean isLoanApproved) {
        this.isLoanApproved = isLoanApproved;
    }
    public double getLoanAmount() {
        return this.loanAmount;
    }
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }
    public double getLoanFees() {
        return this.loanFees;
    }
    public void setLoanFees(double loanFees) {
        this.loanFees = loanFees;
    }
    public double getRepaymentAmount() {
        return this.repaymentAmount;
    }
    public void setRepaymentAmount(double repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }
    public double getLoanInterestRate() {
        return this.loanInterestRate;
    }
    public void setLoanInterestRate(double loanInterestRate) {
        this.loanInterestRate = loanInterestRate;
    }
    public Date getLoanCreationDate() {
        return this.loanCreationDate;
    }
    public void setLoanCreationDate(Date loanCreationDate) {
        this.loanCreationDate = loanCreationDate;
    }
    public Date getLoanReleaseDate() {
        return this.loanReleaseDate;
    }
    public void setLoanReleaseDate(Date loanReleaseDate) {
        this.loanReleaseDate = loanReleaseDate;
    }
    public Date getLoanApprovedDate() {
        return this.loanApprovedDate;
    }
    public void setLoanApprovedDate(Date loanApprovedDate) {
        this.loanApprovedDate = loanApprovedDate;
    }
    public int getLoanDuration() {
        return this.loanDuration;
    }
    public void setLoanDuration(int loanDuration) {
        this.loanDuration = loanDuration;
    }
    public String getLoanTypeId() {
        return this.loanTypeId;
    }
    public void setLoanTypeId(String loanTypeId) {
        this.loanTypeId = loanTypeId;
    }
    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }
    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }
    public String getLoanInterestRateUnit() {
        return this.loanInterestRateUnit;
    }
    public void setLoanInterestRateUnit(String loanInterestRateUnit) {
        this.loanInterestRateUnit = loanInterestRateUnit;
    }
    public String getLoanDurationUnit() {
        return this.loanDurationUnit;
    }
    public void setLoanDurationUnit(String loanDurationUnit) {
        this.loanDurationUnit = loanDurationUnit;
    }
    public String getRepaymentAmountUnit() {
        return this.repaymentAmountUnit;
    }
    public void setRepaymentAmountUnit(String repaymentAmountUnit) {
        this.repaymentAmountUnit = repaymentAmountUnit;
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Override
    public String toString() {
        return "LoansTable{" + "loanId='" + loanId + '\'' + ", borrowerId='" + borrowerId + '\'' + ", groupId='" + groupId + '\'' + ", id=" + id + ", isOtherLoanType=" + isOtherLoanType + ", isLoanApproved=" + isLoanApproved + ", loanAmount=" + loanAmount + ", loanFees=" + loanFees + ", repaymentAmount=" + repaymentAmount + ", loanInterestRate=" + loanInterestRate + ", loanCreationDate=" + loanCreationDate + ", loanReleaseDate=" + loanReleaseDate + ", loanApprovedDate=" + loanApprovedDate + ", lastUpdatedAt=" + lastUpdatedAt + ", loanDuration=" + loanDuration + ", loanTypeId='" + loanTypeId + '\'' + ", loanOfficerId='" + loanOfficerId + '\'' + ", loanInterestRateUnit='" + loanInterestRateUnit + '\'' + ", loanDurationUnit='" + loanDurationUnit + '\'' + ", repaymentAmountUnit='" + repaymentAmountUnit + '\'' + '}';
    }

    // Parcelling part
    public LoansTable(Parcel in){
        String[] data = new String[19];

        in.readStringArray(data);
        this.loanId = data[0];
        this.borrowerId = data[1];
        this.groupId = data[2];
        this.isOtherLoanType = Boolean.parseBoolean(data[3]);
        this.isLoanApproved = Boolean.parseBoolean(data[4]);
        this.loanAmount = Double.parseDouble(data[5]);
        this.loanFees = Double.parseDouble(data[6]);
        this.repaymentAmount = Double.parseDouble(data[7]);
        this.loanInterestRate = Double.parseDouble(data[8]);
        this.loanDuration = Integer.parseInt(data[13]);
        this.loanTypeId = data[14];
        this.loanOfficerId = data[15];
        this.loanInterestRateUnit = data[16];
        this.loanDurationUnit = data[17];
        this.repaymentAmountUnit = data[18];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.loanId,
        this.borrowerId,
        this.groupId, String.valueOf(this.isOtherLoanType), String.valueOf(this.isLoanApproved), String.valueOf(this.loanAmount), String.valueOf(this.loanFees), String.valueOf(this.repaymentAmount), String.valueOf(this.loanInterestRate), String.valueOf(this.loanDuration),
        this.loanTypeId,
        this.loanOfficerId,
        this.loanInterestRateUnit,
        this.loanDurationUnit,
        this.repaymentAmountUnit});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LoansTable createFromParcel(Parcel in) {
            return new LoansTable(in);
        }

        public LoansTable[] newArray(int size) {
            return new LoansTable[size];
        }
    };
}
