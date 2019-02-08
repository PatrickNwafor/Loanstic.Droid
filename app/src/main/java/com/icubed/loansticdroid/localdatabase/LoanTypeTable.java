package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Arrays;
import java.util.Date;

@Entity()
public class LoanTypeTable implements Parcelable {

    @Unique
    private String loanTypeId;

    private String branchId;

    @Id(autoincrement = true)
    private Long Id;

    private String loanTypeName;
    private Date lastUpdatedAt;
    private String loanTypeDescription;

    private String loanTypeImageUri;
    private String loanTypeImageThumbUri;
    private Date timestamp;
    private byte[] loanTypeImageByteArray;
    @Generated(hash = 1885994709)
    public LoanTypeTable(String loanTypeId, String branchId, Long Id, String loanTypeName, Date lastUpdatedAt, String loanTypeDescription, String loanTypeImageUri, String loanTypeImageThumbUri, Date timestamp, byte[] loanTypeImageByteArray) {
        this.loanTypeId = loanTypeId;
        this.branchId = branchId;
        this.Id = Id;
        this.loanTypeName = loanTypeName;
        this.lastUpdatedAt = lastUpdatedAt;
        this.loanTypeDescription = loanTypeDescription;
        this.loanTypeImageUri = loanTypeImageUri;
        this.loanTypeImageThumbUri = loanTypeImageThumbUri;
        this.timestamp = timestamp;
        this.loanTypeImageByteArray = loanTypeImageByteArray;
    }
    @Generated(hash = 108642373)
    public LoanTypeTable() {
    }
    public String getLoanTypeId() {
        return this.loanTypeId;
    }
    public void setLoanTypeId(String loanTypeId) {
        this.loanTypeId = loanTypeId;
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getLoanTypeName() {
        return this.loanTypeName;
    }
    public void setLoanTypeName(String loanTypeName) {
        this.loanTypeName = loanTypeName;
    }
    public String getLoanTypeDescription() {
        return this.loanTypeDescription;
    }
    public void setLoanTypeDescription(String loanTypeDescription) {
        this.loanTypeDescription = loanTypeDescription;
    }
    public String getLoanTypeImageUri() {
        return this.loanTypeImageUri;
    }
    public void setLoanTypeImageUri(String loanTypeImageUri) {
        this.loanTypeImageUri = loanTypeImageUri;
    }
    public String getLoanTypeImageThumbUri() {
        return this.loanTypeImageThumbUri;
    }
    public void setLoanTypeImageThumbUri(String loanTypeImageThumbUri) {
        this.loanTypeImageThumbUri = loanTypeImageThumbUri;
    }
    public byte[] getLoanTypeImageByteArray() {
        return this.loanTypeImageByteArray;
    }
    public void setLoanTypeImageByteArray(byte[] loanTypeImageByteArray) {
        this.loanTypeImageByteArray = loanTypeImageByteArray;
    }
    public String getBranchId() {
        return this.branchId;
    }
    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LoanTypeTable{" + "loanTypeId='" + loanTypeId + '\'' + ", branchId='" + branchId + '\'' + ", Id=" + Id + ", loanTypeName='" + loanTypeName + '\'' + ", loanTypeDescription='" + loanTypeDescription + '\'' + ", loanTypeImageUri='" + loanTypeImageUri + '\'' + ", loanTypeImageThumbUri='" + loanTypeImageThumbUri + '\'' + ", timestamp=" + timestamp + ", loanTypeImageByteArray=" + Arrays.toString(loanTypeImageByteArray) + '}';
    }

    // Parcelling part
    public LoanTypeTable(Parcel in){
        String[] data = new String[4];

        in.readStringArray(data);
        this.loanTypeId = data[0];
        this.loanTypeName = data[1];
        this.loanTypeDescription = data[2];
        this.branchId = data[3];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.loanTypeId,
                this.loanTypeName,
                this.loanTypeDescription,
                this.branchId});
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LoanTypeTable createFromParcel(Parcel in) {
            return new LoanTypeTable(in);
        }

        public LoanTypeTable[] newArray(int size) {
            return new LoanTypeTable[size];
        }
    };
}
