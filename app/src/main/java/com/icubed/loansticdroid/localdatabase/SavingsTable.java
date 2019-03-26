package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsTable implements Parcelable {

    @Unique
    private String savingsId;

    @Id(autoincrement = true)
    private Long id;

    private Date createdAt, lastUpdatedAt, lastDefaultSavings;
    private int accountNumber;
    private double amountSaved;
    private String borrowerId;

    @Generated(hash = 1301538599)
    public SavingsTable(String savingsId, Long id, Date createdAt, Date lastUpdatedAt, Date lastDefaultSavings, int accountNumber, double amountSaved, String borrowerId) {
        this.savingsId = savingsId;
        this.id = id;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.lastDefaultSavings = lastDefaultSavings;
        this.accountNumber = accountNumber;
        this.amountSaved = amountSaved;
        this.borrowerId = borrowerId;
    }

    @Generated(hash = 1055947197)
    public SavingsTable() {
    }

    public String getSavingsId() {
        return this.savingsId;
    }

    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmountSaved() {
        return this.amountSaved;
    }

    public void setAmountSaved(double amountSaved) {
        this.amountSaved = amountSaved;
    }

    public String getBorrowerId() {
        return this.borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public Date getLastDefaultSavings() {
        return this.lastDefaultSavings;
    }

    public void setLastDefaultSavings(Date lastDefaultSavings) {
        this.lastDefaultSavings = lastDefaultSavings;
    }

    // Parcelling part
    public SavingsTable(Parcel in) {
        this.savingsId = in.readString();
        this.createdAt = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
        this.lastDefaultSavings = new Date(in.readLong());
        this.accountNumber = in.readInt();
        this.amountSaved = in.readDouble();
        this.borrowerId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.savingsId);
        dest.writeLong(this.createdAt.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
        dest.writeLong(this.lastDefaultSavings.getTime());
        dest.writeInt(this.accountNumber);
        dest.writeDouble(this.amountSaved);
        dest.writeString(this.borrowerId);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SavingsTable createFromParcel(Parcel in) {
            return new SavingsTable(in);
        }

        public SavingsTable[] newArray(int size) {
            return new SavingsTable[size];
        }
    };
}
