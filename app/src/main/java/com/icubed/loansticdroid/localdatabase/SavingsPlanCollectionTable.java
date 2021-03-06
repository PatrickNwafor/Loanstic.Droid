package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsPlanCollectionTable implements Parcelable {

    @Transient
    public static final String COLLECTION_STATE_FULL = "Full Collection";
    @Transient
    public static final String COLLECTION_STATE_PARTIAL = "Partial Collection";
    @Transient
    public static final String COLLECTION_STATE_NO = "No Collection";

    @Unique
    private String savingsPlanCollectionId;

    private String savingsId;

    private int savingsCollectionNumber;

    @Id(autoincrement = true)
    private Long id;

    private Double savingsCollectionAmount;

    private Date savingsCollectionDueDate;
    private Date lastUpdatedAt;
    private Date timestamp;
    private double amountPaid;
    private String collectionState;

    private Boolean isSavingsCollected;

    // Parcelling part
    public SavingsPlanCollectionTable(Parcel in){
        this.savingsPlanCollectionId = in.readString();
        this.savingsId = in.readString();
        this.savingsCollectionNumber = in.readInt();
        this.id = in.readLong();
        this.savingsCollectionAmount = in.readDouble();
        this.amountPaid = in.readDouble();
        this.collectionState = in.readString();
        this.isSavingsCollected = Boolean.valueOf(in.readString());
        this.savingsCollectionDueDate = new Date(in.readLong());
        this.lastUpdatedAt = new Date(in.readLong());
        this.timestamp = new Date(in.readLong());
    }

    @Generated(hash = 792024406)
    public SavingsPlanCollectionTable(String savingsPlanCollectionId, String savingsId, int savingsCollectionNumber, Long id, Double savingsCollectionAmount, Date savingsCollectionDueDate, Date lastUpdatedAt, Date timestamp, double amountPaid, String collectionState, Boolean isSavingsCollected) {
        this.savingsPlanCollectionId = savingsPlanCollectionId;
        this.savingsId = savingsId;
        this.savingsCollectionNumber = savingsCollectionNumber;
        this.id = id;
        this.savingsCollectionAmount = savingsCollectionAmount;
        this.savingsCollectionDueDate = savingsCollectionDueDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.timestamp = timestamp;
        this.amountPaid = amountPaid;
        this.collectionState = collectionState;
        this.isSavingsCollected = isSavingsCollected;
    }

    @Generated(hash = 41577661)
    public SavingsPlanCollectionTable() {
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.savingsPlanCollectionId);
        dest.writeString(this.savingsId);
        dest.writeInt(this.savingsCollectionNumber);
        dest.writeLong(this.id);
        dest.writeDouble(this.savingsCollectionAmount);
        dest.writeDouble(this.amountPaid);
        dest.writeString(this.collectionState);
        dest.writeString(String.valueOf(this.isSavingsCollected));
        dest.writeLong(this.savingsCollectionDueDate.getTime());
        dest.writeLong(this.lastUpdatedAt.getTime());
        dest.writeLong(this.timestamp.getTime());
    }

    @Override
    public String toString() {
        return "SavingsPlanCollectionTable{" + "savingsPlanCollectionId='" + savingsPlanCollectionId + '\'' + ", savingsId='" + savingsId + '\'' + ", savingsCollectionNumber=" + savingsCollectionNumber + ", id=" + id + ", savingsCollectionAmount=" + savingsCollectionAmount + ", savingsCollectionDueDate=" + savingsCollectionDueDate + ", lastUpdatedAt=" + lastUpdatedAt + ", timestamp=" + timestamp + ", amountPaid=" + amountPaid + ", collectionState='" + collectionState + '\'' + ", isSavingsCollected=" + isSavingsCollected + '}';
    }

    public String getSavingsPlanCollectionId() {
        return this.savingsPlanCollectionId;
    }

    public void setSavingsPlanCollectionId(String savingsPlanCollectionId) {
        this.savingsPlanCollectionId = savingsPlanCollectionId;
    }

    public String getSavingsId() {
        return this.savingsId;
    }

    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }

    public int getSavingsCollectionNumber() {
        return this.savingsCollectionNumber;
    }

    public void setSavingsCollectionNumber(int savingsCollectionNumber) {
        this.savingsCollectionNumber = savingsCollectionNumber;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getSavingsCollectionAmount() {
        return this.savingsCollectionAmount;
    }

    public void setSavingsCollectionAmount(Double savingsCollectionAmount) {
        this.savingsCollectionAmount = savingsCollectionAmount;
    }

    public Date getSavingsCollectionDueDate() {
        return this.savingsCollectionDueDate;
    }

    public void setSavingsCollectionDueDate(Date savingsCollectionDueDate) {
        this.savingsCollectionDueDate = savingsCollectionDueDate;
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

    public Boolean getIsSavingsCollected() {
        return this.isSavingsCollected;
    }

    public void setIsSavingsCollected(Boolean isSavingsCollected) {
        this.isSavingsCollected = isSavingsCollected;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SavingsPlanCollectionTable createFromParcel(Parcel in) {
            return new SavingsPlanCollectionTable(in);
        }

        public SavingsPlanCollectionTable[] newArray(int size) {
            return new SavingsPlanCollectionTable[size];
        }
    };
}
