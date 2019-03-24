package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CollectionTable implements Parcelable {

    @Unique
    private String collectionId;

    private String loanId, savingsId;

    private int collectionNumber;

    @Id(autoincrement = true)
    private Long id;

    private Double collectionDueAmount, penalty;

    private Date collectionDueDate;
    private Date lastUpdatedAt;
    private Date timestamp;
    private double amountPaid;
    private String collectionState;

    private Boolean isDueCollected;

    @Generated(hash = 1267406644)
    public CollectionTable(String collectionId, String loanId, String savingsId, int collectionNumber, Long id, Double collectionDueAmount, Double penalty, Date collectionDueDate, Date lastUpdatedAt, Date timestamp, double amountPaid, String collectionState, Boolean isDueCollected) {
        this.collectionId = collectionId;
        this.loanId = loanId;
        this.savingsId = savingsId;
        this.collectionNumber = collectionNumber;
        this.id = id;
        this.collectionDueAmount = collectionDueAmount;
        this.penalty = penalty;
        this.collectionDueDate = collectionDueDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.timestamp = timestamp;
        this.amountPaid = amountPaid;
        this.collectionState = collectionState;
        this.isDueCollected = isDueCollected;
    }

    @Generated(hash = 1935108601)
    public CollectionTable() {
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

    public int getCollectionNumber() {
        return this.collectionNumber;
    }

    public void setCollectionNumber(int collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCollectionDueAmount() {
        return this.collectionDueAmount;
    }

    public void setCollectionDueAmount(Double collectionDueAmount) {
        this.collectionDueAmount = collectionDueAmount;
    }

    public Date getCollectionDueDate() {
        return this.collectionDueDate;
    }

    public void setCollectionDueDate(Date collectionDueDate) {
        this.collectionDueDate = collectionDueDate;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsDueCollected() {
        return this.isDueCollected;
    }

    public void setIsDueCollected(Boolean isDueCollected) {
        this.isDueCollected = isDueCollected;
    }

    @Override
    public String toString() {
        return "CollectionTable{" + "collectionId='" + collectionId + '\'' + ", loanId='" + loanId + '\'' + ", collectionNumber=" + collectionNumber + ", id=" + id + ", collectionDueAmount=" + collectionDueAmount + ", penalty=" + penalty + ", collectionDueDate=" + collectionDueDate + ", lastUpdatedAt=" + lastUpdatedAt + ", timestamp=" + timestamp + ", amountPaid=" + amountPaid + ", collectionState='" + collectionState + '\'' + ", isDueCollected=" + isDueCollected + '}';
    }

    public Double getPenalty() {
        return this.penalty;
    }

    public void setPenalty(Double penalty) {
        this.penalty = penalty;
    }

    public double getAmountPaid() {
        return this.amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getCollectionState() {
        return this.collectionState;
    }

    public void setCollectionState(String collectionState) {
        this.collectionState = collectionState;
    }

    // Parcelling part
    public CollectionTable(Parcel in){
        this.collectionId = in.readString();
        this.loanId = in.readString();
        this.collectionNumber = in.readInt();
        this.id = in.readLong();
        this.collectionDueAmount = in.readDouble();
        this.penalty = in.readDouble();
        this.amountPaid = in.readDouble();
        this.collectionState = in.readString();
        this.isDueCollected = Boolean.valueOf(in.readString());
        this.savingsId = in.readString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.collectionId);
        dest.writeString(this.loanId);
        dest.writeInt(this.collectionNumber);
        dest.writeLong(this.id);
        dest.writeDouble(this.collectionDueAmount);
        dest.writeDouble(this.penalty);
        dest.writeDouble(this.amountPaid);
        dest.writeString(this.collectionState);
        dest.writeString(String.valueOf(this.isDueCollected));
        dest.writeString(this.savingsId);
    }

    public String getSavingsId() {
        return this.savingsId;
    }

    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CollectionTable createFromParcel(Parcel in) {
            return new CollectionTable(in);
        }

        public CollectionTable[] newArray(int size) {
            return new CollectionTable[size];
        }
    };
}
